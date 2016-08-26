package org.nextprot.api.core.utils.annot.merge.impl;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.annot.merge.Finder;
import org.nextprot.api.core.utils.annot.merge.SimilarityPredicate;

import java.util.Collection;

/**
 * Find similar annotation in collection of annotations
 *
 * Created by fnikitin on 02/08/16.
 */
public class AnnotationFinder implements Finder<Annotation> {

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
     */
    @Override
    public Annotation find(Annotation searchedAnnotation, Collection<Annotation> annotations) {

        for (Annotation annotation : annotations) {

            if (criteria.isSimilar(searchedAnnotation, annotation))
                return annotation;
        }

        return null;
    }
}
