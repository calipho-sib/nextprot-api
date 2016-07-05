package com.nextprot.api.isoform.mapper.service.impl;

import com.nextprot.api.isoform.mapper.service.FeatureValidator;
import org.nextprot.api.commons.constants.AnnotationCategory;

import java.util.Optional;

/**
 * Create FeatureValidators
 */
public class ValidatorFactory {

    /**
     * Creates a new instance of FeatureValidator given an annotation category
     *
     * @param annotationCategory annotation category
     * @return an Optional.empty if not found
     */
    public static Optional<FeatureValidator> creates(AnnotationCategory annotationCategory) {

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

}
