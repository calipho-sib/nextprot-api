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
                return new ObjectSimilarityPredicate(Annotation::getCvTermAccessionCode);
            case VARIANT:
            case MUTAGENESIS:
                return new SimilarityPredicatePipeline(Arrays.asList(
                        new ObjectSimilarityPredicate(Annotation::getVariant),
                        new ObjectSimilarityPredicate(Annotation::getCvTermAccessionCode),
                        new ObjectSimilarityPredicate(Annotation::getTargetingIsoformsMap)
                ));
            default:
                return new ObjectSimilarityPredicate(Annotation::getVariant);
        }
    }
}
