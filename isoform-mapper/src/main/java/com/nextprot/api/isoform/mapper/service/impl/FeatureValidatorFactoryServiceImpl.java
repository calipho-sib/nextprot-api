package com.nextprot.api.isoform.mapper.service.impl;

import com.nextprot.api.isoform.mapper.service.FeatureValidatorFactoryService;
import com.nextprot.api.isoform.mapper.service.SequenceFeatureValidator;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FeatureValidatorFactoryServiceImpl implements FeatureValidatorFactoryService {

    @Override
    public Optional<SequenceFeatureValidator> createsFeatureValidator(AnnotationCategory annotationCategory) {

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
