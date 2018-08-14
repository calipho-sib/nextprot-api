package org.nextprot.api.isoform.mapper.domain;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.service.BeanService;
import org.nextprot.api.isoform.mapper.domain.impl.SequenceModification;
import org.nextprot.api.isoform.mapper.domain.impl.SequenceVariant;
import org.nextprot.api.isoform.mapper.domain.impl.exception.InvalidFeatureQueryFormatException;
import org.nextprot.api.isoform.mapper.domain.impl.exception.InvalidFeatureQueryTypeException;

/**
 * A sequence feature on an isoform sequence on a specific gene
 */
public interface SequenceFeatureFactory {

    static SequenceFeature newSequenceFeature(SingleFeatureQuery query, BeanService beanService) throws FeatureQueryException {

        // throw exception if invalid query
        query.checkFeatureQuery();

        AnnotationCategory annotationCategory = AnnotationCategory.getDecamelizedAnnotationTypeName(query.getFeatureType());

        try {
            switch (annotationCategory) {
                case MUTAGENESIS:
                    return SequenceVariant.mutagenesis(query.getFeature(), beanService);
                case VARIANT:
                    return SequenceVariant.variant(query.getFeature(), beanService);
                case GENERIC_PTM:
                    return new SequenceModification(query.getFeature(), beanService);
                default:
                    throw new InvalidFeatureQueryTypeException(query);
            }
        }
        catch (Exception e) {
            throw new InvalidFeatureQueryFormatException(query, e);
        }
    }
}
