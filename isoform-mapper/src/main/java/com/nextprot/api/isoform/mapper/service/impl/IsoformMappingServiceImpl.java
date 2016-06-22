package com.nextprot.api.isoform.mapper.service.impl;

import com.nextprot.api.isoform.mapper.domain.IsoformFeature;
import com.nextprot.api.isoform.mapper.domain.IsoformFeatureMapping;
import com.nextprot.api.isoform.mapper.service.IsoformMappingService;
import com.nextprot.api.isoform.mapper.utils.EntryIsoform;
import com.nextprot.api.isoform.mapper.utils.GeneVariantParser;
import com.nextprot.api.isoform.mapper.utils.Propagator;
import org.nextprot.api.commons.bio.variation.ProteinSequenceVariation;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.Entry;
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
    public IsoformFeatureMapping validateFeature(String featureName, AnnotationCategory annotationCategory, String nextprotAccession, boolean propagate) {

        EntryIsoform isoform = EntryIsoform.parseAccession(nextprotAccession, entryBuilderService);

        switch (annotationCategory) {
            case VARIANT:
                return validateVariant(featureName, isoform, propagate);
            case PTM_INFO:
                return validatePtm(featureName, isoform, propagate);
            default:
                throw new IllegalArgumentException("cannot handle annotation category " + annotationCategory);
        }
    }

    /**
     * Check that the changing amino-acid(s) exists on isoform and return status
     *
     * @param isoform the isoform to va
     * @param variation the variation on which expected changing amino-acids is found
     * @return isoform feature
     */
    private IsoformFeature getIsoformFeature(Isoform isoform, ProteinSequenceVariation variation) {

        IsoformFeature feature = new IsoformFeature();

        feature.setIsoformName(isoform.getUniqueName());

        boolean firstPosCheck = Propagator.checkAminoAcidPosition(isoform, variation.getFirstChangingAminoAcidPos(),
                String.valueOf(variation.getFirstChangingAminoAcid().get1LetterCode()));

        boolean lastPosCheck = Propagator.checkAminoAcidPosition(isoform, variation.getLastChangingAminoAcidPos(),
                String.valueOf(variation.getLastChangingAminoAcid().get1LetterCode()));

        StringBuilder sb = new StringBuilder("unmapped position(s): ");

        if (!firstPosCheck) {
            sb.append("first="+feature.getFirstPositionOnIsoform()).append(" ");
        } else {
            feature.setFirstPositionOnIsoform(variation.getFirstChangingAminoAcidPos());
        }
        if (!lastPosCheck) {
            sb.append("last="+feature.getFirstPositionOnIsoform());
        } else {
            feature.setLastPositionOnIsoform(variation.getLastChangingAminoAcidPos());
        }

        if (sb.length()>0) {
            feature.setMessage(sb.toString());
            feature.setStatus(IsoformFeature.Status.UNMAPPED);
        }else {
            feature.setStatus(IsoformFeature.Status.MAPPED);
        }
        return feature;
    }

    /**
     * Deduce the variation on the given isoform given the original variation on another isoform
     */
    private ProteinSequenceVariation createPotentialVariationOnTargetIsoform(ProteinSequenceVariation sourceVariation,
                                                                             Isoform source, Isoform dest) {

        Integer posOnDestIsoform = Propagator.getProjectedPosition(source, sourceVariation.getFirstChangingAminoAcidPos(), dest);

        return null;
    }

    private IsoformFeatureMapping validateVariant(String variant, EntryIsoform entryIsoform, boolean propagate) {

        IsoformFeatureMapping mapping = new IsoformFeatureMapping();

        try {
            GeneVariantParser parser = new GeneVariantParser(variant, entryIsoform);
            ProteinSequenceVariation entryIsoformVariation = parser.getProteinSequenceVariation();

            // check isoform feature
            IsoformFeature isoformFeature = getIsoformFeature(entryIsoform.getIsoform(), entryIsoformVariation);
            mapping.addIsoformFeature(isoformFeature.getIsoformName(), isoformFeature);

            // propagation to other isoforms ?
            //2) propagate if flag = true returns a map with N isoforms
            if (propagate) {

                propagate();
                //3) check rules
                checkRules();
            }
        } catch (ParseException e) {

            throw new RuntimeException(variant + ": invalid variant name", e);
        }

        return mapping;
    }

    private IsoformFeatureMapping validatePtm(String featureName, EntryIsoform isoform, boolean propagate) {

        throw new IllegalStateException("ptm validation not yet implemented");
    }

    private boolean validateIsoformPosition(ProteinSequenceVariation variation, Entry entry) {

        // 2. check AA exists in isoform at specified position
        Propagator propagator = new Propagator(entry);


        return true;
    }

    private void propagate() {

        // propagate the feature to other isoforms
        // need to locate those feature on isoforms

    }

    private void checkRules() {

    }

}
