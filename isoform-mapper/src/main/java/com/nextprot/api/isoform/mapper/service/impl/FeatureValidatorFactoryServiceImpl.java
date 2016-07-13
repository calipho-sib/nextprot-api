package com.nextprot.api.isoform.mapper.service.impl;

import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.service.FeatureValidatorFactoryService;
import com.nextprot.api.isoform.mapper.service.SequenceFeatureValidator;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FeatureValidatorFactoryServiceImpl implements FeatureValidatorFactoryService {

    @Override
    public Optional<SequenceFeatureValidator> createsFeatureValidator(FeatureQuery query) {

        SequenceFeatureValidator validator = null;

        AnnotationCategory annotationCategory = AnnotationCategory.getDecamelizedAnnotationTypeName(query.getFeatureType());

        switch (annotationCategory) {
            case VARIANT:
                validator = new SequenceVariantValidator(query);
                break;
            case GENERIC_PTM:
                validator = new SequencePtmValidator(query);
        }

        return Optional.ofNullable(validator);
    }
}
