package org.nextprot.api.core.utils.annot.merge.impl;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.annot.merge.AnnotationSimilarityPredicate;

import java.util.List;

/**
 * A list of SimilarityPredicate applied with conjunction operator (AND)
 *
 * Created by fnikitin on 02/08/16.
 */
public class SimilarityPredicateChain implements AnnotationSimilarityPredicate {

    private final List<AnnotationSimilarityPredicate> criteria;

    public SimilarityPredicateChain(List<AnnotationSimilarityPredicate> criteria) {

        Preconditions.checkNotNull(criteria);
        Preconditions.checkArgument(!criteria.isEmpty());

        this.criteria = criteria;
    }

    @Override
    public boolean isSimilar(Annotation annotation1, Annotation annotation2) {

        for (AnnotationSimilarityPredicate criterium : criteria) {

            if (!criterium.isSimilar(annotation1, annotation2))
                return false;
        }

        return true;
    }
}
