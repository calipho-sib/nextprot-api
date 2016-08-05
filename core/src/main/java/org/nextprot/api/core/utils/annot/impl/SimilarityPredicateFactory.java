package org.nextprot.api.core.utils.annot.impl;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.annot.SimilarityPredicate;

import java.util.Arrays;

public class SimilarityPredicateFactory {

    /**
     * Factory method based on AnnotationCategory.
     * @return an instance of SimilarityPredicate given a category
     */
    public static SimilarityPredicate newSimilarityPredicate(AnnotationCategory category) {

        Preconditions.checkNotNull(category);

        switch (category) {
            case GO_BIOLOGICAL_PROCESS:
            case GO_CELLULAR_COMPONENT:
            case GO_MOLECULAR_FUNCTION:
                // TODO: there is a 'is_negative' field to consider in the future
                return new ObjectSimilarityPredicate<>(Annotation::getCvTermAccessionCode);
            case VARIANT:
            case MUTAGENESIS:
                return new SimilarityPredicatePipeline(Arrays.asList(
                        new ObjectSimilarityPredicate<>(Annotation::getCvTermAccessionCode),
                        new ObjectSimilarityPredicate<>(Annotation::getVariant,
                                (v1, v2) -> v1.getOriginal().equals(v2.getOriginal()) && v1.getVariant().equals(v2.getVariant())),
                        new ObjectSimilarityPredicate<>(Annotation::getTargetingIsoformsMap, new VariantPositionMatcher()
                        )
                ));
            default:
                return new ObjectSimilarityPredicate<>(Annotation::getAnnotationId);
        }
    }
}
