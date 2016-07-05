package com.nextprot.api.isoform.mapper.service;

import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.MappedIsoformsFeatureResult;
import com.nextprot.api.isoform.mapper.service.impl.PtmValidator;
import com.nextprot.api.isoform.mapper.service.impl.VariantValidator;
import com.nextprot.api.isoform.mapper.utils.EntryIsoform;
import org.nextprot.api.commons.constants.AnnotationCategory;

import java.util.Optional;

public interface FeatureValidator {

    MappedIsoformsFeatureResult validate(FeatureQuery query, EntryIsoform entryIsoform);

    static Optional<FeatureValidator> createValidator(AnnotationCategory annotationCategory) {

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
