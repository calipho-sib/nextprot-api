package com.nextprot.api.isoform.mapper.service.impl;

import com.nextprot.api.isoform.mapper.domain.MappedIsoformsFeatureError;
import com.nextprot.api.isoform.mapper.domain.MappedIsoformsFeatureResult;
import com.nextprot.api.isoform.mapper.domain.MappedIsoformsFeatureSuccess;
import com.nextprot.api.isoform.mapper.service.IsoformMappingService;
import com.nextprot.api.isoform.mapper.utils.EntryIsoform;
import com.nextprot.api.isoform.mapper.utils.GeneVariantBuilder;
import com.nextprot.api.isoform.mapper.utils.IsoformSequencePositionMapper;
import org.nextprot.api.commons.bio.variation.ProteinSequenceVariation;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.MasterIsoformMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;


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

        switch (annotationCategory) {
            case VARIANT:
                return validateVariant(query);
            case PTM_INFO:
                return validatePtm(query);
            default:
                throw new IllegalArgumentException("cannot handle annotation category " + annotationCategory);
        }
    }

    @Override
    public MappedIsoformsFeatureResult propagateFeature(String featureName, AnnotationCategory annotationCategory, String nextprotAccession) {

        MappedIsoformsFeatureResult results = validateFeature(featureName, annotationCategory, nextprotAccession);

        // propagation to other isoforms ?
        //2) propagate if flag = true returns a map with N isoforms
        propagate(results);

        return results;
    }

    private MappedIsoformsFeatureResult validateVariant(MappedIsoformsFeatureResult.Query query) {

        EntryIsoform entryIsoform = EntryIsoform.parseAccession(query.getAccession(), entryBuilderService);

        try {
            GeneVariantBuilder builder = new GeneVariantBuilder(query.getFeature(), entryIsoform.getEntry());
            ProteinSequenceVariation entryIsoformVariation = builder.getProteinSequenceVariation();

            return checkFeatureOnIsoform(query, entryIsoform.getIsoform(), entryIsoformVariation);
        } catch (ParseException e) {

            MappedIsoformsFeatureError error = new MappedIsoformsFeatureError(query);
            error.setErrorValue(new MappedIsoformsFeatureError.InvalidVariantName(query.getFeature()));
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

        MappedIsoformsFeatureError.ErrorValue firstPosErrorValue = checkIsoformPos(isoform, variation.getFirstChangingAminoAcidPos(),
                String.valueOf(variation.getFirstChangingAminoAcid().get1LetterCode()));

        MappedIsoformsFeatureError.ErrorValue lastPosErrorValue = checkIsoformPos(isoform, variation.getLastChangingAminoAcidPos(),
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

        // TODO: I don't like that !
        result.loadContentValue();

        return result;
    }

    /**
     *
     * @param isoform
     * @param position
     * @param aas
     * @return an ErrorValue if invalid else null
     */
    private MappedIsoformsFeatureError.ErrorValue checkIsoformPos(Isoform isoform, int position, String aas) {

        boolean insertionMode = (aas == null || aas.isEmpty());
        boolean valid = IsoformSequencePositionMapper.checkSequencePosition(isoform, position, insertionMode);

        if (!valid) {
            return new MappedIsoformsFeatureError.InvalidPosition(isoform.getUniqueName(), position);
        }

        if (!insertionMode) {
            valid = IsoformSequencePositionMapper.checkAminoAcidsFromPosition(isoform, position, aas);

            if (!valid) {
                return new MappedIsoformsFeatureError.UnexpectedAminoAcids(
                        isoform.getSequence().substring(position - 1, position + aas.length() - 1), aas);
            }
        }

        return null;
    }

    private MappedIsoformsFeatureResult validatePtm(MappedIsoformsFeatureResult.Query query) {

        throw new IllegalStateException("ptm validation not yet implemented");
    }

    private void propagate(MappedIsoformsFeatureResult results) {

        // propagate the feature to other isoforms
        // need to locate those feature on isoforms
    }
}
