package com.nextprot.api.isoform.mapper.service.impl;

import com.nextprot.api.isoform.mapper.service.SequenceFeatureValidator;
import org.nextprot.api.commons.constants.AnnotationCategory;

import java.util.Optional;

/**
 * Create FeatureValidators
 */
public class FeatureValidatorFactory {

    /**
     * Creates a new instance of FeatureValidator given an annotation category
     *
     * @param annotationCategory annotation category
     * @return an Optional.empty if not found
     */
    public static Optional<SequenceFeatureValidator> createsFeatureValidator(AnnotationCategory annotationCategory) {

        SequenceFeatureValidator validator = null;

        switch (annotationCategory) {
            case VARIANT:
                validator = new SequenceVariantValidator();
                break;
            case GENERIC_PTM:
                validator = new SequencePtmValidator();
        }

        return Optional.ofNullable(validator);
    }
}
