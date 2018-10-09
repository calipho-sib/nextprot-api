package org.nextprot.api.core.service.annotation.merge.impl;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.annotation.merge.AnnotationSimilarityPredicate;

import java.util.ArrayList;
import java.util.List;

/**
 * A list of SimilarityPredicate applied with an AND operator
 *
 * Created by fnikitin on 02/08/16.
 */
public class SimilarityPredicateConjunctive implements AnnotationSimilarityPredicate {

    private final List<AnnotationSimilarityPredicate> conjunctiveCriteria;

    public SimilarityPredicateConjunctive(List<AnnotationSimilarityPredicate> conjunctiveCriteria) {

        Preconditions.checkNotNull(conjunctiveCriteria);
        Preconditions.checkArgument(!conjunctiveCriteria.isEmpty());

        this.conjunctiveCriteria = new ArrayList<>(conjunctiveCriteria);
    }

    @Override
    public boolean isSimilar(Annotation annotation1, Annotation annotation2) {

        for (AnnotationSimilarityPredicate criterium : conjunctiveCriteria) {

            if (!criterium.isSimilar(annotation1, annotation2))
                return false;
        }

        return true;
    }
}
