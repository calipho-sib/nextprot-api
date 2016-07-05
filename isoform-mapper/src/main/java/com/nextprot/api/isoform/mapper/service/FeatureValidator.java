package com.nextprot.api.isoform.mapper.service;

import com.nextprot.api.isoform.mapper.domain.MappedIsoformsFeatureResult;
import com.nextprot.api.isoform.mapper.service.impl.PtmValidator;
import com.nextprot.api.isoform.mapper.service.impl.VariantValidator;
import com.nextprot.api.isoform.mapper.utils.EntryIsoform;
import org.nextprot.api.commons.constants.AnnotationCategory;

public interface FeatureValidator {

    MappedIsoformsFeatureResult validate(MappedIsoformsFeatureResult.Query query, EntryIsoform entryIsoform);

    static FeatureValidator createValidator(AnnotationCategory annotationCategory) {

        switch (annotationCategory) {
            case VARIANT:
                return new VariantValidator();
            case GENERIC_PTM:
                return new PtmValidator();
            default:
                throw new IllegalArgumentException("cannot validate feature category " + annotationCategory);
        }
    }
}
