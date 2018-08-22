package org.nextprot.api.isoform.mapper.domain;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.service.BeanService;
import org.nextprot.api.isoform.mapper.domain.impl.SequenceModification;
import org.nextprot.api.isoform.mapper.domain.impl.SequenceVariant;
import org.nextprot.api.isoform.mapper.domain.impl.exception.InvalidFeatureQueryFormatException;
import org.nextprot.api.isoform.mapper.domain.impl.exception.InvalidFeatureQueryTypeException;

/**
 * A sequence feature on an isoform sequence on a specific gene
 */
public interface SequenceFeatureFactory {

    static SequenceFeature newSequenceFeature(String featureName, String featureType, BeanService beanService) throws Exception {

        AnnotationCategory annotationCategory = AnnotationCategory.getDecamelizedAnnotationTypeName(featureType);

        switch (annotationCategory) {
            case MUTAGENESIS:
                return SequenceVariant.mutagenesis(featureName, beanService);
            case VARIANT:
                return SequenceVariant.variant(featureName, beanService);
            case GENERIC_PTM:
                return new SequenceModification(featureName, beanService);
            default:
                throw new NextProtException("invalid feature type " + featureType + ", feature name=" + featureName);
        }
    }

    /*static SequenceFeature newSequenceFeature(SingleFeatureQuery query, BeanService beanService) throws FeatureQueryException {

        // throw exception if invalid query
        query.checkFeatureQuery();

        try {
            return newSequenceFeature(query.getFeature(), query.getFeatureType(), beanService);
        } catch (Exception e) {
            throw new InvalidFeatureQueryTypeException(query);
        }
    }*/

    static SequenceFeature newSequenceFeature(SingleFeatureQuery query, BeanService beanService) throws FeatureQueryException {
        AnnotationCategory annotationCategory = AnnotationCategory.getDecamelizedAnnotationTypeName(query.getFeatureType());

        // throw exception if invalid query
        query.checkFeatureQuery();

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
        } catch (Exception e) {
            throw new InvalidFeatureQueryFormatException(query, e);
        }
    }
}
