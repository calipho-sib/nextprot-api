package org.nextprot.api.isoform.mapper.service.impl;

import org.nextprot.api.commons.bio.variation.prot.SequenceVariationBuildException;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.isoform.mapper.domain.query.FeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.feature.SequenceFeature;
import org.nextprot.api.isoform.mapper.domain.query.SingleFeatureQuery;
import org.nextprot.api.isoform.mapper.domain.feature.impl.SequenceModification;
import org.nextprot.api.isoform.mapper.domain.feature.impl.SequenceVariant;
import org.nextprot.api.isoform.mapper.domain.impl.exception.InvalidFeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.impl.exception.InvalidFeatureQueryFormatException;
import org.nextprot.api.isoform.mapper.domain.impl.exception.InvalidFeatureQueryTypeException;
import org.nextprot.api.isoform.mapper.service.SequenceFeatureFactoryService;
import org.springframework.stereotype.Service;

import java.text.ParseException;


@Service
public class SequenceFeatureFactoryServiceImpl implements SequenceFeatureFactoryService {

    @Override
    public SequenceFeature newSequenceFeature(String featureName, String featureType) throws ParseException, SequenceVariationBuildException {

        AnnotationCategory annotationCategory = AnnotationCategory.getDecamelizedAnnotationTypeName(featureType);

        switch (annotationCategory) {
            case MUTAGENESIS:
                return SequenceVariant.mutagenesis(featureName);
            case VARIANT:
                return SequenceVariant.variant(featureName);
            case GENERIC_PTM:
                return new SequenceModification(featureName);
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
                    return SequenceVariant.mutagenesis(query.getFeature());
                case VARIANT:
                    return SequenceVariant.variant(query.getFeature());
                case GENERIC_PTM:
                    return new SequenceModification(query.getFeature());
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
