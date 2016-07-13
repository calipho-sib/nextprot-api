package com.nextprot.api.isoform.mapper.service.impl;

import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryResult;
import com.nextprot.api.isoform.mapper.domain.impl.FeatureQueryFailure;
import com.nextprot.api.isoform.mapper.domain.impl.FeatureQuerySuccess;
import com.nextprot.api.isoform.mapper.domain.impl.exception.InvalidFeatureQueryTypeException;
import com.nextprot.api.isoform.mapper.service.EntryIsoformFactoryService;
import com.nextprot.api.isoform.mapper.service.FeatureValidatorFactoryService;
import com.nextprot.api.isoform.mapper.service.IsoformMappingService;
import com.nextprot.api.isoform.mapper.service.SequenceFeatureValidator;
import com.nextprot.api.isoform.mapper.domain.EntryIsoform;
import com.nextprot.api.isoform.mapper.utils.IsoformSequencePositionMapper;
import org.nextprot.api.commons.bio.variation.SequenceVariation;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.MasterIsoformMappingService;
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
    public EntryIsoformFactoryService entryIsoformFactoryService;

    /**
     * Use/Cases
     *
     * 1. Deduce isoform from the given nextprotAccession
     * 2. Check that first and last amino-acid(s) described by the feature exists on isoform sequence at given positions
     * 3.
     */
    @Override
    public FeatureQueryResult validateFeature(String featureName, String featureType, String nextprotAccession) {

        try {
            FeatureQuery query = new FeatureQuery(nextprotAccession, featureName, featureType, false);

            Optional<SequenceFeatureValidator> validator =
                    featureValidatorFactoryService.createsFeatureValidator(AnnotationCategory.getDecamelizedAnnotationTypeName(featureType));

            // TODO: replace get() call with future ifPresentOrElse method (https://dzone.com/articles/java-8-optional-replace-your-get-calls?edition=188596&utm_source=Daily%20Digest&utm_medium=email&utm_campaign=dd%202016-07-06)
            if (validator.isPresent()) {

                return validator.get().validate(query, entryIsoformFactoryService.createsEntryIsoform(nextprotAccession));
            }
            else
                throw new InvalidFeatureQueryTypeException(query);
        } catch (FeatureQueryException e) {

            return new FeatureQueryFailure(e);
        }
    }

    @Override
    public FeatureQueryResult propagateFeature(String featureName, String featureType, String nextprotAccession) {

        FeatureQueryResult results = validateFeature(featureName, featureType, nextprotAccession);

        if (results.isSuccess()) {
            try {
                propagate((FeatureQuerySuccess) results);
            } catch (ParseException e) {
                throw new NextProtException(e.getMessage());
            }
        }

        return results;
    }

    private void propagate(FeatureQuerySuccess successResults) throws ParseException {

        EntryIsoform entryIsoform = successResults.getEntryIsoform();

        SequenceVariation variation = successResults.getIsoformSequenceVariation();

        String expectedAAs = entryIsoform.getIsoform().getSequence().substring(
                variation.getFirstChangingAminoAcidPos()-1, variation.getLastChangingAminoAcidPos()
        );

        // get all others
        List<Isoform> others = entryIsoform.getOtherIsoforms();

        // propagate the feature to other isoforms
        for (Isoform otherIsoform : others) {

            Integer firstPos = IsoformSequencePositionMapper.getProjectedPosition(entryIsoform.getIsoform(),
                    variation.getFirstChangingAminoAcidPos(), otherIsoform);

            if (firstPos != null && IsoformSequencePositionMapper.checkAminoAcidsFromPosition(otherIsoform, firstPos, expectedAAs)) {
                Integer lastPos = IsoformSequencePositionMapper.getProjectedPosition(entryIsoform.getIsoform(),
                        variation.getLastChangingAminoAcidPos(), otherIsoform);

                successResults.addMappedFeature(otherIsoform, firstPos, lastPos);
            } else {
                successResults.addUnmappedFeature(otherIsoform);
            }
        }
    }
}
