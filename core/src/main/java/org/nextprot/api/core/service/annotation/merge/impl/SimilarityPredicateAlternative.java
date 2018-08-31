package org.nextprot.api.core.service.annotation.merge.impl;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.annotation.merge.AnnotationSimilarityPredicate;

import java.util.ArrayList;
import java.util.List;

/**
 * A list of SimilarityPredicate applied with an OR operator
 *
 * Created by fnikitin on 02/08/16.
 */
public class SimilarityPredicateAlternative implements AnnotationSimilarityPredicate {

    private final List<AnnotationSimilarityPredicate> alternativeCriteria;

    public SimilarityPredicateAlternative(List<AnnotationSimilarityPredicate> alternativeCriteria) {

        Preconditions.checkNotNull(alternativeCriteria);
        Preconditions.checkArgument(!alternativeCriteria.isEmpty());

        this.alternativeCriteria = new ArrayList<>(alternativeCriteria);
    }

    @Override
    public boolean isSimilar(Annotation annotation1, Annotation annotation2) {

        for (AnnotationSimilarityPredicate criterium : alternativeCriteria) {

            if (criterium.isSimilar(annotation1, annotation2))
                return true;
        }

        return false;
    }
}
