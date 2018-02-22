package org.nextprot.api.isoform.mapper.service.impl;

import com.google.common.base.Strings;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariation;
import org.nextprot.api.commons.bio.variation.prot.seqchange.SequenceChange;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.service.MasterIsoformMappingService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.utils.IsoformUtils;
import org.nextprot.api.core.utils.seqmap.GeneMasterCodonPosition;
import org.nextprot.api.core.utils.seqmap.IsoformSequencePositionMapper;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.SequenceFeature;
import org.nextprot.api.isoform.mapper.domain.SingleFeatureQuery;
import org.nextprot.api.isoform.mapper.domain.impl.BaseFeatureQueryResult;
import org.nextprot.api.isoform.mapper.domain.impl.FeatureQueryFailureImpl;
import org.nextprot.api.isoform.mapper.domain.impl.SequenceFeatureBase;
import org.nextprot.api.isoform.mapper.domain.impl.SingleFeatureQuerySuccessImpl;
import org.nextprot.api.isoform.mapper.domain.impl.exception.EntryAccessionNotFoundForGeneException;
import org.nextprot.api.isoform.mapper.domain.impl.exception.MultipleEntryAccessionForGeneException;
import org.nextprot.api.isoform.mapper.service.IsoformMappingService;
import org.nextprot.api.isoform.mapper.service.SequenceFeatureValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Set;


/**
 * Specs: https://issues.isb-sib.ch/browse/BIOEDITOR-397
 */
@Service
public class IsoformMappingServiceImpl implements IsoformMappingService {

    @Autowired
    public MasterIsoformMappingService masterIsoformMappingService;

    @Autowired
    private EntryBuilderService entryBuilderService;

    @Autowired
    private MasterIdentifierService masterIdentifierService;

    @Override
    public BaseFeatureQueryResult validateFeature(SingleFeatureQuery query) {

        try {
            SequenceFeature sequenceFeature = SequenceFeatureBase.newFeature(query);

            if (Strings.isNullOrEmpty(query.getAccession()))
                query.setAccession(findAccessionFromGeneName(query, sequenceFeature.getGeneName()));

            Entry entry = entryBuilderService.build(EntryConfig.newConfig(query.getAccession())
                    .withTargetIsoforms().withOverview());

            SequenceFeatureValidator validator = new SequenceFeatureValidator(entry, query);

            return validator.validate(sequenceFeature);
        } catch (FeatureQueryException e) {

            return new FeatureQueryFailureImpl(e);
        }
    }

    @Override
    public BaseFeatureQueryResult propagateFeature(SingleFeatureQuery query) {

        BaseFeatureQueryResult results = validateFeature(query);

        if (results.isSuccess()) {
            try {
                propagate((SingleFeatureQuerySuccessImpl) results);
            } catch (ParseException e) {
                throw new NextProtException(e.getMessage());
            }
        }

        return results;
    }

    // TODO: refactor this method, it is too complex (probably a propagator object with strategy pattern for the mapping)
    private void propagate(SingleFeatureQuerySuccessImpl successResults) throws ParseException {

        SingleFeatureQuery query = successResults.getQuery();
        query.setPropagableFeature(true);

        SequenceFeature isoFeature = successResults.getIsoformSequenceFeature();

        Isoform featureIsoform = isoFeature.getIsoform(successResults.getEntry());
        SequenceVariation variation = isoFeature.getProteinVariation();

        OriginalAminoAcids originalAminoAcids = getOriginalAminoAcids(featureIsoform.getSequence(), variation);

        GeneMasterCodonPosition originalFirstMasterCodonPos = IsoformSequencePositionMapper.getCodonPositionsOnMaster(originalAminoAcids.getFirstAAPos(), featureIsoform);
        GeneMasterCodonPosition originalLastMasterCodonPos = IsoformSequencePositionMapper.getCodonPositionsOnMaster(originalAminoAcids.getLastAAPos(), featureIsoform);

        // try to propagate the feature to other isoforms
        for (Isoform otherIsoform : IsoformUtils.getOtherIsoforms(successResults.getEntry(), featureIsoform.getIsoformAccession())) {

            Integer firstIsoPos = IsoformSequencePositionMapper.getProjectedPosition(featureIsoform, originalAminoAcids.getFirstAAPos(), otherIsoform);
            Integer lastIsoPos = IsoformSequencePositionMapper.getProjectedPosition(featureIsoform, originalAminoAcids.getLastAAPos(), otherIsoform);

            boolean propagable = false;

            if (firstIsoPos != null && lastIsoPos != null) {

                if (variation.getVaryingSequence().isMultipleAminoAcids()) {

                    int originalSequenceLength = lastIsoPos - firstIsoPos + 1;
                    int isoformSequenceLength = originalAminoAcids.getAas().length();

                    if (originalSequenceLength == isoformSequenceLength) {

                        String isoformSequence = otherIsoform.getSequence().substring(firstIsoPos-1, lastIsoPos);

                        if (isoformSequence.equals(originalAminoAcids.getAas())) {

                            GeneMasterCodonPosition firstMasterCodonPos = IsoformSequencePositionMapper.getCodonPositionsOnMaster(firstIsoPos, otherIsoform);
                            GeneMasterCodonPosition lastMasterCodonPos = IsoformSequencePositionMapper.getCodonPositionsOnMaster(lastIsoPos, otherIsoform);

                            propagable = firstMasterCodonPos.getNucleotidePosition(0).intValue() == originalFirstMasterCodonPos.getNucleotidePosition(0)
                                && lastMasterCodonPos.getNucleotidePosition(2).intValue() == originalLastMasterCodonPos.getNucleotidePosition(2);
                        }
                    }
                }
                // check a single amino-acid
                else {

                    propagable = IsoformSequencePositionMapper.checkAminoAcidsFromPosition(otherIsoform, firstIsoPos, originalAminoAcids.getAas());
                }
            }

            if (propagable) {
                addPropagation(otherIsoform, firstIsoPos, lastIsoPos, originalAminoAcids.isExtensionTerminal(), successResults);
            }
            else {
                successResults.addUnmappedFeature(otherIsoform);
            }
        }
    }

    private void addPropagation(Isoform otherIsoform, Integer firstIsoPos, Integer lastIsoPos, boolean isExtensionTerminal, SingleFeatureQuerySuccessImpl successResults) {

        // success
        if (isExtensionTerminal)
            successResults.addMappedFeature(otherIsoform, firstIsoPos + 1, lastIsoPos + 1);
        else
            successResults.addMappedFeature(otherIsoform, firstIsoPos, lastIsoPos);
    }

    private OriginalAminoAcids getOriginalAminoAcids(String sequence, SequenceVariation variation) {

        SequenceChange.Type variationType = variation.getSequenceChange().getType();

        int firstPos = variation.getVaryingSequence().getFirstAminoAcidPos();
        int lastPos = variation.getVaryingSequence().getLastAminoAcidPos();
        boolean isTerminalExtension = false;

        if (variationType == SequenceChange.Type.EXTENSION_TERM) {

            firstPos = sequence.length();
            lastPos = sequence.length();
            isTerminalExtension = true;
        }
        return new OriginalAminoAcids(sequence, firstPos, lastPos, isTerminalExtension);
    }

    /**
     * Find entry accession from geneName
     */
    private String findAccessionFromGeneName(SingleFeatureQuery query, String geneName) throws FeatureQueryException {

        Set<String> accessions = masterIdentifierService.findEntryAccessionByGeneName(geneName, false);

        if (accessions.isEmpty()) {
            throw new EntryAccessionNotFoundForGeneException(query, geneName);
        } else if (accessions.size() > 1) {
            throw new MultipleEntryAccessionForGeneException(query, geneName, accessions);
        }
        // found one single entry accession
        return accessions.iterator().next();
    }

    private static class OriginalAminoAcids {

        private final String aas;
        private final int firstAAPos;
        private final int lastAAPos;
        private final boolean isExtensionTerminal;

        public OriginalAminoAcids(String sequence, int firstAAPos, int lastAAPos, boolean isExtensionTerminal) {
            this.aas = sequence.substring(firstAAPos-1, lastAAPos);
            this.firstAAPos = firstAAPos;
            this.lastAAPos = lastAAPos;
            this.isExtensionTerminal = isExtensionTerminal;
        }

        public String getAas() {
            return aas;
        }

        public int getFirstAAPos() {
            return firstAAPos;
        }

        public int getLastAAPos() {
            return lastAAPos;
        }

        public boolean isExtensionTerminal() {
            return isExtensionTerminal;
        }
    }
}
