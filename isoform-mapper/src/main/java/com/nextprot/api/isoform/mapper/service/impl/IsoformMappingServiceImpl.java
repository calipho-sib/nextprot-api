package com.nextprot.api.isoform.mapper.service.impl;

import com.nextprot.api.isoform.mapper.domain.*;
import com.nextprot.api.isoform.mapper.domain.impl.InvalidFeatureQueryTypeException;
import com.nextprot.api.isoform.mapper.service.FeatureValidator;
import com.nextprot.api.isoform.mapper.service.IsoformMappingService;
import com.nextprot.api.isoform.mapper.utils.EntryIsoform;
import com.nextprot.api.isoform.mapper.utils.GeneVariantPair;
import com.nextprot.api.isoform.mapper.utils.IsoformSequencePositionMapper;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.EntryBuilderService;
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
    private EntryBuilderService entryBuilderService;

    @Autowired
    public MasterIsoformMappingService masterIsoformMappingService;

    @Override
    public FeatureQueryResult validateFeature(String featureName, String featureType, String nextprotAccession) {

        try {
            EntryIsoform isoform = EntryIsoform.parseEntryIsoform(nextprotAccession, entryBuilderService);
            FeatureQuery query = new FeatureQuery(isoform, featureName, featureType, false);

            Optional<FeatureValidator> validator = Factory.createsFeatureValidator(AnnotationCategory.getDecamelizedAnnotationTypeName(featureType));

            if (validator.isPresent()) {

                return validator.get().validate(query, isoform);
            }
            throw new InvalidFeatureQueryTypeException(query);
        } catch (FeatureQueryException e) {

            return new FeatureQueryFailure(e);
        }
    }

    @Override
    public FeatureQueryResult propagateFeature(String featureName, String featureType, String nextprotAccession) {

        FeatureQueryResult results = validateFeature(featureName, featureType, nextprotAccession);

        if (!results.isSuccess()) return results;

        try {
            propagate((FeatureQuerySuccess) results);
        } catch (ParseException e) {
            throw new NextProtException(e.getMessage());
        }

        return results;
    }

    private void propagate(FeatureQuerySuccess successResults) throws ParseException {

        EntryIsoform entryIsoform = successResults.getQuery().getEntryIsoform();

        GeneVariantPair geneVariantPair = new GeneVariantPair(successResults.getQuery().getFeature());

        IsoformFeature isoformFeature = geneVariantPair.getFeature();
        String expectedAAs = entryIsoform.getIsoform().getSequence().substring(
                isoformFeature.getFirstChangingAminoAcidPos()-1, isoformFeature.getLastChangingAminoAcidPos()
        );

        // get all others
        List<Isoform> others = entryIsoform.getOtherIsoforms();

        // propagate the feature to other isoforms
        for (Isoform otherIsoform : others) {

            Integer firstPos = IsoformSequencePositionMapper.getProjectedPosition(entryIsoform.getIsoform(),
                    isoformFeature.getFirstChangingAminoAcidPos(), otherIsoform);

            if (firstPos != null && IsoformSequencePositionMapper.checkAminoAcidsFromPosition(otherIsoform, firstPos, expectedAAs)) {
                Integer lastPos = IsoformSequencePositionMapper.getProjectedPosition(entryIsoform.getIsoform(),
                        isoformFeature.getLastChangingAminoAcidPos(), otherIsoform);

                successResults.addMappedFeature(otherIsoform.getUniqueName(), firstPos, lastPos);
            } else {
                successResults.addUnmappedFeature(otherIsoform.getUniqueName());
            }
        }
    }
}
