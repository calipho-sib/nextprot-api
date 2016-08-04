package org.nextprot.api.core.utils.annot;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.annot.impl.SimilarityPredicateFactory;

import java.util.Collection;

/**
 * Find similar annotation in collection of annotations
 *
 * Created by fnikitin on 02/08/16.
 */
public class AnnotationFinder {

    private final SimilarityPredicate criteria;

    /**
     * Constructor needs a criteria to find similar annotations
     */
    public AnnotationFinder(SimilarityPredicate criteria) {

        Preconditions.checkNotNull(criteria);

        this.criteria = criteria;
    }

    public static AnnotationFinder valueOf(AnnotationCategory category) {

        return new AnnotationFinder(SimilarityPredicateFactory.newSimilarityPredicate(category));
    }

    /**
     * @return the annotation found from a list of annotations or null if not found
     *
     * TODO: does find() need to return a collection instead of one instance ?
     */
    public Annotation find(Annotation searchedAnnotation, Collection<Annotation> annotations) {

        for (Annotation annotation : annotations) {

            if (criteria.isSimilar(searchedAnnotation, annotation))
                return annotation;
        }

        return null;
    }
}
