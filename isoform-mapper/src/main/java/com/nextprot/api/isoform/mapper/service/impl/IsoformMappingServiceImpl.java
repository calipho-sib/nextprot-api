package com.nextprot.api.isoform.mapper.service.impl;

import com.nextprot.api.isoform.mapper.domain.SequenceFeature;
import com.nextprot.api.isoform.mapper.utils.EntryIsoformUtils;
import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryResult;
import com.nextprot.api.isoform.mapper.domain.impl.FeatureQueryFailure;
import com.nextprot.api.isoform.mapper.domain.impl.FeatureQuerySuccess;
import com.nextprot.api.isoform.mapper.domain.impl.exception.InvalidFeatureQueryTypeException;
import com.nextprot.api.isoform.mapper.service.FeatureValidatorFactoryService;
import com.nextprot.api.isoform.mapper.service.IsoformMappingService;
import com.nextprot.api.isoform.mapper.service.SequenceFeatureValidator;
import com.nextprot.api.isoform.mapper.utils.IsoformSequencePositionMapper;
import org.nextprot.api.commons.bio.variation.SequenceVariation;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.MasterIsoformMappingService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;


/**
 * Specs: https://issues.isb-sib.ch/browse/BIOEDITOR-397
 */
@Service
public class IsoformMappingServiceImpl implements IsoformMappingService {

    @Autowired
    public MasterIsoformMappingService masterIsoformMappingService;

    @Autowired
    public FeatureValidatorFactoryService featureValidatorFactoryService;

    @Autowired
    private EntryBuilderService entryBuilderService;

    @Override
    public FeatureQueryResult validateFeature(String featureName, String featureType, String nextprotEntryAccession) {

        if (nextprotEntryAccession.contains("-")) {
            int dashIndex = nextprotEntryAccession.indexOf("-");

            throw new NextProtException("Invalid entry accession " + nextprotEntryAccession
                    + ": " + nextprotEntryAccession.substring(0, dashIndex)+" was expected");
        }

        Entry entry = entryBuilderService.build(EntryConfig.newConfig(nextprotEntryAccession).withTargetIsoforms().withOverview());

        try {
            FeatureQuery query = new FeatureQuery(entry, featureName, featureType);

            Optional<SequenceFeatureValidator> validator = featureValidatorFactoryService.createsFeatureValidator(query);

            if (validator.isPresent()) {
                return validator.get().validate();
            }
            throw new InvalidFeatureQueryTypeException(query);
        } catch (FeatureQueryException e) {

            return new FeatureQueryFailure(e);
        }
    }

    @Override
    public FeatureQueryResult propagateFeature(String featureName, String featureType, String nextprotEntryAccession) {

        FeatureQueryResult results = validateFeature(featureName, featureType, nextprotEntryAccession);

        if (results.isSuccess()) {
            try {
                propagate((FeatureQuerySuccess) results);
            } catch (ParseException e) {
                throw new NextProtException(e.getMessage());
            }
        }

        return results;
    }

    // TODO: refactor this method, it is too complex (probably a propagator object)
    private void propagate(FeatureQuerySuccess successResults) throws ParseException {

        FeatureQuery query = successResults.getQuery();

        query.setPropagableFeature(true);

        SequenceFeature isoFeature = successResults.getIsoformSequenceFeature();

        Isoform featureIsoform = isoFeature.getIsoform(query.getEntry());;

        SequenceVariation variation = isoFeature.getProteinVariation();

        String expectedAAs = featureIsoform.getSequence().substring(
                variation.getFirstChangingAminoAcidPos()-1, variation.getLastChangingAminoAcidPos()
        );

        // get all others
        List<Isoform> others = EntryIsoformUtils.getOtherIsoforms(query.getEntry(), featureIsoform.getUniqueName());

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
}
