package com.nextprot.api.isoform.mapper.service.impl;

import com.nextprot.api.isoform.mapper.domain.MappedIsoformsFeatureResult;
import com.nextprot.api.isoform.mapper.domain.impl.InvalidFeatureTypeFailure;
import com.nextprot.api.isoform.mapper.domain.impl.MappedIsoformsFeatureSuccess;
import com.nextprot.api.isoform.mapper.domain.impl.Query;
import com.nextprot.api.isoform.mapper.domain.impl.UnknownFeatureTypeFailure;
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
    public MappedIsoformsFeatureResult validateFeature(String featureName, String featureType, String nextprotAccession) {

        Query query = new Query(nextprotAccession, featureName, featureType, false);

        if (!AnnotationCategory.hasAnnotationByApiName(featureType))
            return new UnknownFeatureTypeFailure(query);

        AnnotationCategory annotationCategory = AnnotationCategory.getDecamelizedAnnotationTypeName(featureType);

        Optional<FeatureValidator> validator = ValidatorFactory.creates(annotationCategory);

        if (validator.isPresent())
            return validator.get().validate(query, EntryIsoform.parseAccession(query.getAccession(), entryBuilderService));

        return new InvalidFeatureTypeFailure(query);
    }

    @Override
    public MappedIsoformsFeatureResult propagateFeature(String featureName, String featureType, String nextprotAccession) {

        MappedIsoformsFeatureResult results = validateFeature(featureName, featureType, nextprotAccession);

        // TODO: Should not break DRY principle: already parsed in other "validateFeature" handler
        EntryIsoform entryIsoform = EntryIsoform.parseAccession(results.getQuery().getAccession(), entryBuilderService);

        try {
            propagate(results, entryIsoform);
        } catch (ParseException e) {
            throw new NextProtException(e.getMessage());
        }

        return results;
    }

    private void propagate(MappedIsoformsFeatureResult results, EntryIsoform entryIsoform) throws ParseException {

        if (!results.isSuccess()) { return; }

        MappedIsoformsFeatureSuccess successResults = (MappedIsoformsFeatureSuccess) results;

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

                successResults.addMappedIsoformFeature(otherIsoform.getUniqueName(), firstPos, lastPos);
            } else {
                successResults.addNonMappedIsoformFeature(otherIsoform.getUniqueName());
            }
        }
    }
}
