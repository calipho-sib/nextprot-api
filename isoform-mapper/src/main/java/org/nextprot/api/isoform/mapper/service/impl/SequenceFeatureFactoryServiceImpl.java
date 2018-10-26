package org.nextprot.api.isoform.mapper.service.impl;

import org.nextprot.api.commons.bio.variation.prot.SequenceVariationBuildException;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.service.BeanDiscoveryService;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.SequenceFeature;
import org.nextprot.api.isoform.mapper.domain.SingleFeatureQuery;
import org.nextprot.api.isoform.mapper.domain.impl.SequenceModification;
import org.nextprot.api.isoform.mapper.domain.impl.SequenceVariant;
import org.nextprot.api.isoform.mapper.domain.impl.exception.InvalidFeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.impl.exception.InvalidFeatureQueryFormatException;
import org.nextprot.api.isoform.mapper.domain.impl.exception.InvalidFeatureQueryTypeException;
import org.nextprot.api.isoform.mapper.service.SequenceFeatureFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;


@Service
public class SequenceFeatureFactoryServiceImpl implements SequenceFeatureFactoryService {

    @Autowired
    private BeanDiscoveryService beanDiscoveryService;

    @Override
    public SequenceFeature newSequenceFeature(String featureName, String featureType) throws ParseException, SequenceVariationBuildException {

        AnnotationCategory annotationCategory = AnnotationCategory.getDecamelizedAnnotationTypeName(featureType);

        switch (annotationCategory) {
            case MUTAGENESIS:
                return SequenceVariant.mutagenesis(featureName, beanDiscoveryService);
            case VARIANT:
                return SequenceVariant.variant(featureName, beanDiscoveryService);
            case GENERIC_PTM:
                return new SequenceModification(featureName, beanDiscoveryService);
            default:
                throw new NextProtException("invalid feature type " + featureType + ", feature name=" + featureName);
        }
    }

    @Override
    public SequenceFeature newSequenceFeature(SingleFeatureQuery query) throws FeatureQueryException {

        AnnotationCategory annotationCategory = AnnotationCategory.getDecamelizedAnnotationTypeName(query.getFeatureType());

        // throw exception if invalid query
        query.checkFeatureQuery();

        try {
            switch (annotationCategory) {
                case MUTAGENESIS:
                    return SequenceVariant.mutagenesis(query.getFeature(), beanDiscoveryService);
                case VARIANT:
                    return SequenceVariant.variant(query.getFeature(), beanDiscoveryService);
                case GENERIC_PTM:
                    return new SequenceModification(query.getFeature(), beanDiscoveryService);
                default:
                    throw new InvalidFeatureQueryTypeException(query);
            }
        }
        catch (InvalidFeatureQueryTypeException | ParseException e) {
            throw new InvalidFeatureQueryFormatException(query, e);
        }
        catch (SequenceVariationBuildException e) {
            throw new InvalidFeatureQueryException(query, e);
        }
    }
}
