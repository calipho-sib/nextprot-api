package org.nextprot.api.isoform.mapper.service.impl;

import com.google.common.base.Strings;
import org.nextprot.api.commons.bio.variation.SequenceVariation;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.MasterIsoformMappingService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.utils.IsoformUtils;
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
import java.util.List;
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

    // TODO: refactor this method, it is too complex (probably a propagator object)
    private void propagate(SingleFeatureQuerySuccessImpl successResults) throws ParseException {

        SingleFeatureQuery query = successResults.getQuery();

        query.setPropagableFeature(true);

        SequenceFeature isoFeature = successResults.getIsoformSequenceFeature();

        Isoform featureIsoform = isoFeature.getIsoform(successResults.getEntry());

        SequenceVariation variation = isoFeature.getProteinVariation();

        String expectedAAs = featureIsoform.getSequence().substring(
                variation.getFirstChangingAminoAcidPos() - 1, variation.getLastChangingAminoAcidPos()
        );

        // get all others
        List<Isoform> others = IsoformUtils.getOtherIsoforms(successResults.getEntry(), featureIsoform.getUniqueName());

        // propagate the feature to other isoforms
        for (Isoform otherIsoform : others) {

            Integer firstIsoPos = IsoformSequencePositionMapper.getProjectedPosition(featureIsoform,
                    variation.getFirstChangingAminoAcidPos(), otherIsoform);

            Integer lastIsoPos =
                    (firstIsoPos != null &&
                            IsoformSequencePositionMapper.checkAminoAcidsFromPosition(otherIsoform, firstIsoPos, expectedAAs)) ?
                            getLastProjectedPosition(firstIsoPos, variation, featureIsoform, otherIsoform) : null;

            if (firstIsoPos != null && lastIsoPos != null)
                successResults.addMappedFeature(otherIsoform, firstIsoPos, lastIsoPos);
            else
                successResults.addUnmappedFeature(otherIsoform);
        }
    }

    private Integer getLastProjectedPosition(int firstIsoPos, SequenceVariation srcIsoformVariation, Isoform srcIsoform, Isoform otherIsoform) {

        if (srcIsoformVariation.isMultipleChangingAminoAcids()) {

            return IsoformSequencePositionMapper.getProjectedPosition(srcIsoform,
                    srcIsoformVariation.getLastChangingAminoAcidPos(), otherIsoform);
        }
        return firstIsoPos;
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
}
