package com.nextprot.api.isoform.mapper.service.impl;

import com.nextprot.api.isoform.mapper.domain.IsoformFeatureMapping;
import com.nextprot.api.isoform.mapper.service.IsoformMappingService;
import org.nextprot.api.commons.bio.mutation.ProteinMutation;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.service.MasterIsoformMappingService;
import org.nextprot.api.core.service.OverviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;


/**
 * Specs: https://issues.isb-sib.ch/browse/BIOEDITOR-397
 */
@Service
public class IsoformMappingServiceImpl implements IsoformMappingService {

    @Autowired
    public OverviewService overviewService;

    @Autowired
    public MasterIsoformMappingService masterIsoformMappingService;

    @Override
    public IsoformFeatureMapping validateFeature(String featureName, AnnotationCategory annotationCategory, String nextprotAccession, boolean propagate) {

        switch (annotationCategory) {
            case VARIANT:
                return validateVariant(featureName, nextprotAccession, propagate);
            case PTM_INFO:
                return validatePtm(featureName, nextprotAccession, propagate);
            default:
                throw new IllegalArgumentException("cannot handle annotation category " + annotationCategory);
        }
    }

    private IsoformFeatureMapping validateVariant(String featureName, String nextprotAccession, boolean propagate) {

        IsoformFeatureMapping mapping = new IsoformFeatureMapping();

        try {
            GeneVariantParser parser = new GeneVariantParser(featureName, nextprotAccession, overviewService);
            ProteinMutation mutation = parser.getProteinMutation();
            String geneName = parser.getGeneName();

            //1) Validate
            if (validateIsoformPosition(mutation)) {

                //2) propagate if flag = true returns a map with N isoforms
                if (propagate) {

                    propagate();
                    //3) check rules
                    checkRules();
                }

            } else {
                // not validated
                // error message...

            }
        } catch (ParseException e) {

            throw new RuntimeException(featureName + ": invalid feature name", e);
        }

        return mapping;
    }

    private IsoformFeatureMapping validatePtm(String featureName, String nextprotAccession, boolean propagate) {

        throw new IllegalStateException("ptm validation not yet implemented");
    }

    private boolean validateIsoformPosition(ProteinMutation mutation) {

        // 2. check AA exists in isoform at specified position



        return true;
    }

    private void propagate() {

    }

    private void checkRules() {

    }
}
