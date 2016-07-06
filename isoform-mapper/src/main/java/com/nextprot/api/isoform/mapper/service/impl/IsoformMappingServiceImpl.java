package com.nextprot.api.isoform.mapper.service.impl;

import com.nextprot.api.isoform.mapper.domain.*;
import com.nextprot.api.isoform.mapper.domain.impl.*;
import com.nextprot.api.isoform.mapper.service.FeatureValidator;
import com.nextprot.api.isoform.mapper.service.IsoformMappingService;
import com.nextprot.api.isoform.mapper.utils.EntryIsoform;
import com.nextprot.api.isoform.mapper.utils.GeneVariantSplitter;
import com.nextprot.api.isoform.mapper.utils.IsoformSequencePositionMapper;
import org.nextprot.api.commons.bio.variation.ProteinSequenceVariation;
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
            FeatureQuery query = new FeatureQuery(nextprotAccession, featureName, featureType, false);

            Optional<FeatureValidator> validator = ValidatorFactory.creates(AnnotationCategory.getDecamelizedAnnotationTypeName(featureType));

            if (validator.isPresent()) {

                return validator.get().validate(query, EntryIsoform.parseEntryIsoform(query.getAccession(), entryBuilderService));
            }

            throw new InvalidFeatureQueryTypeException(query);
        } catch (FeatureQueryException e) {

            return new FeatureQueryFailure(e);
        }
    }

    @Override
    public FeatureQueryResult propagateFeature(String featureName, String featureType, String nextprotAccession) {

        FeatureQueryResult results = validateFeature(featureName, featureType, nextprotAccession);

        // TODO: Should not break DRY principle: already parsed in other "validateFeature" handler
        EntryIsoform entryIsoform = EntryIsoform.parseEntryIsoform(results.getQuery().getAccession(), entryBuilderService);

        try {
            propagate(results, entryIsoform);
        } catch (ParseException e) {
            throw new NextProtException(e.getMessage());
        }

        return results;
    }

    private void propagate(FeatureQueryResult results, EntryIsoform entryIsoform) throws ParseException {

        if (!results.isSuccess()) { return; }

        FeatureQuerySuccess successResults = (FeatureQuerySuccess) results;

        GeneVariantSplitter splitter = new GeneVariantSplitter(results.getQuery().getFeature());
        ProteinSequenceVariation variant = splitter.getVariant();
        String expectedAAs = entryIsoform.getIsoform().getSequence().substring(
                variant.getFirstChangingAminoAcidPos()-1, variant.getLastChangingAminoAcidPos()
        );

        // get all others
        List<Isoform> others = entryIsoform.getOtherIsoforms();

        // propagate the feature to other isoforms
        for (Isoform otherIsoform : others) {

            Integer firstPos = IsoformSequencePositionMapper.getProjectedPosition(entryIsoform.getIsoform(),
                    variant.getFirstChangingAminoAcidPos(), otherIsoform);

            if (firstPos != null && IsoformSequencePositionMapper.checkAminoAcidsFromPosition(otherIsoform, firstPos, expectedAAs)) {
                Integer lastPos = IsoformSequencePositionMapper.getProjectedPosition(entryIsoform.getIsoform(),
                        variant.getLastChangingAminoAcidPos(), otherIsoform);

                successResults.addMappedFeature(otherIsoform.getUniqueName(), firstPos, lastPos);
            } else {
                successResults.addUnmappedFeature(otherIsoform.getUniqueName());
            }
        }
    }
}
