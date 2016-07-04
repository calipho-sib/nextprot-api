package com.nextprot.api.isoform.mapper.service.impl;

import com.nextprot.api.isoform.mapper.domain.MappedIsoformsFeatureError;
import com.nextprot.api.isoform.mapper.domain.MappedIsoformsFeatureResult;
import com.nextprot.api.isoform.mapper.domain.MappedIsoformsFeatureSuccess;
import com.nextprot.api.isoform.mapper.service.IsoformMappingService;
import com.nextprot.api.isoform.mapper.utils.EntryIsoform;
import com.nextprot.api.isoform.mapper.utils.GeneVariantSplitter;
import com.nextprot.api.isoform.mapper.utils.IsoformSequencePositionMapper;
import org.nextprot.api.commons.bio.variation.ProteinSequenceVariation;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.dao.EntityName;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.MasterIsoformMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;


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
    public MappedIsoformsFeatureResult validateFeature(String featureName, AnnotationCategory annotationCategory, String nextprotAccession) {

        MappedIsoformsFeatureResult.Query query = new MappedIsoformsFeatureResult.Query(
                nextprotAccession, featureName, annotationCategory, false);

        EntryIsoform entryIsoform = EntryIsoform.parseAccession(query.getAccession(), entryBuilderService);

        switch (annotationCategory) {
            case VARIANT:
                return validateVariant(query, entryIsoform);
            case PTM_INFO:
                return validatePtm(query, entryIsoform);
            default:
                throw new IllegalArgumentException("cannot handle annotation category " + annotationCategory);
        }
    }

    @Override
    public MappedIsoformsFeatureResult propagateFeature(String featureName, AnnotationCategory annotationCategory, String nextprotAccession) {

        MappedIsoformsFeatureResult results = validateFeature(featureName, annotationCategory, nextprotAccession);

        // TODO: DRY: already parsed in other "validateFeature" handler
        EntryIsoform entryIsoform = EntryIsoform.parseAccession(results.getQuery().getAccession(), entryBuilderService);

        try {
            propagate(results, entryIsoform);
        } catch (ParseException e) {
            throw new NextProtException(e.getMessage());
        }

        return results;
    }

    private MappedIsoformsFeatureResult validateVariant(MappedIsoformsFeatureResult.Query query, EntryIsoform entryIsoform) {

        try {
            GeneVariantSplitter splitter = new GeneVariantSplitter(query.getFeature());
            if (!splitter.isValidGeneName(entryIsoform.getEntry())) {
                MappedIsoformsFeatureError error = new MappedIsoformsFeatureError(query);

                List<String> expectedGeneNames = entryIsoform.getEntry().getOverview().getGeneNames().stream()
                        .map(EntityName::getName).collect(Collectors.toList());

                error.setErrorValue(new MappedIsoformsFeatureError.IncompatibleGeneAndProteinName(splitter.getGeneName(),
                        entryIsoform.getEntry().getUniqueName(), expectedGeneNames));
                return error;
            }

            ProteinSequenceVariation entryIsoformVariation = splitter.getVariant();

            return checkFeatureOnIsoform(query, entryIsoform.getIsoform(), entryIsoformVariation);
        } catch (ParseException e) {

            MappedIsoformsFeatureError error = new MappedIsoformsFeatureError(query);
            error.setErrorValue(new MappedIsoformsFeatureError.InvalidFeatureFormat(query.getFeature()));
            return error;
        }
    }

    /**
     * Check that variating amino-acid(s) on isoform sequence exists and return status report
     *
     * @param isoform the isoform to check variating amino-acids
     * @param variation the variation on which expected changing amino-acids is found
     */
    private MappedIsoformsFeatureResult checkFeatureOnIsoform(MappedIsoformsFeatureResult.Query query, Isoform isoform,
                                                              ProteinSequenceVariation variation) {
        MappedIsoformsFeatureResult result;

        MappedIsoformsFeatureError.FeatureErrorValue firstPosErrorValue = checkIsoformPos(isoform, variation.getFirstChangingAminoAcidPos(),
                String.valueOf(variation.getFirstChangingAminoAcid().get1LetterCode()));

        MappedIsoformsFeatureError.FeatureErrorValue lastPosErrorValue = checkIsoformPos(isoform, variation.getLastChangingAminoAcidPos(),
                String.valueOf(variation.getLastChangingAminoAcid().get1LetterCode()));

        if (firstPosErrorValue == null && lastPosErrorValue == null) {

            result = new MappedIsoformsFeatureSuccess(query);
            ((MappedIsoformsFeatureSuccess)result).addMappedIsoformFeature(isoform.getUniqueName(),
                    variation.getFirstChangingAminoAcidPos(), variation.getLastChangingAminoAcidPos());
        } else {

            result = new MappedIsoformsFeatureError(query);

            if (firstPosErrorValue != null) {

                ((MappedIsoformsFeatureError)result).setErrorValue(firstPosErrorValue);
            }
            else {

                ((MappedIsoformsFeatureError)result).setErrorValue(lastPosErrorValue);
            }
        }
        return result;
    }

    /**
     *
     * @param isoform
     * @param position
     * @param aas
     * @return an ErrorValue if invalid else null
     */
    private MappedIsoformsFeatureError.FeatureErrorValue checkIsoformPos(Isoform isoform, int position, String aas) {

        boolean insertionMode = (aas == null || aas.isEmpty());
        boolean valid = IsoformSequencePositionMapper.checkSequencePosition(isoform, position, insertionMode);

        if (!valid) {
            return new MappedIsoformsFeatureError.InvalidFeaturePosition(isoform.getUniqueName(), position);
        }

        if (!insertionMode) {
            valid = IsoformSequencePositionMapper.checkAminoAcidsFromPosition(isoform, position, aas);

            if (!valid) {
                return new MappedIsoformsFeatureError.InvalidFeatureAminoAcid(isoform.getUniqueName(),
                        isoform.getSequence().substring(position - 1, position + aas.length() - 1), aas);
            }
        }

        return null;
    }

    private MappedIsoformsFeatureResult validatePtm(MappedIsoformsFeatureResult.Query query, EntryIsoform entryIsoform) {

        throw new IllegalStateException("ptm validation not yet implemented");
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
