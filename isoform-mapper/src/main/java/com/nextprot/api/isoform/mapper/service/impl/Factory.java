package com.nextprot.api.isoform.mapper.service.impl;

import com.nextprot.api.isoform.mapper.service.FeatureValidator;
import com.nextprot.api.isoform.mapper.utils.GeneVariantPair;
import org.nextprot.api.commons.constants.AnnotationCategory;

import java.util.Optional;

/**
 * Create FeatureValidators
 */
public class Factory {

    /**
     * Creates a new instance of FeatureValidator given an annotation category
     *
     * @param annotationCategory annotation category
     * @return an Optional.empty if not found
     */
    public static Optional<FeatureValidator> createsFeatureValidator(AnnotationCategory annotationCategory) {

        FeatureValidator validator = null;

        switch (annotationCategory) {
            case VARIANT:
                validator = new VariantValidator();
                break;
            case GENERIC_PTM:
                validator = new PtmValidator();
        }

        return Optional.ofNullable(validator);
    }

    public static Optional<GeneVariantPair.GeneFeaturePairParser> createsFeaturePairParser(AnnotationCategory annotationCategory) {

        GeneVariantPair.GeneFeaturePairParser parser = null;

        switch (annotationCategory) {
            case VARIANT:
                parser = new GeneVariantPair.GeneVariantPairParser();
                break;
            case GENERIC_PTM:
                parser = null;
        }

        return Optional.ofNullable(parser);
    }

}
