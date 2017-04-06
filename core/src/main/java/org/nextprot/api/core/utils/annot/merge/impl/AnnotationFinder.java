package org.nextprot.api.core.utils.annot.merge.impl;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.annot.merge.AnnotationContainerFinder;
import org.nextprot.api.core.utils.annot.merge.SimilarityPredicate;

import java.util.Collection;
import java.util.Optional;

/**
 * Find similar annotation in collection of annotations
 *
 * Created by fnikitin on 02/08/16.
 */
public class AnnotationFinder implements AnnotationContainerFinder<Annotation> {

    private final SimilarityPredicate criteria;

    /**
     * Constructor needs a criteria to find similar annotations
     */
    public AnnotationFinder(SimilarityPredicate criteria) {

        Preconditions.checkNotNull(criteria);

        this.criteria = criteria;
    }

    @Override
    public Optional<Annotation> find(Annotation searchedAnnotation, Collection<Annotation> annotations) {

        for (Annotation annotation : annotations) {
            if (criteria.isSimilar(searchedAnnotation, annotation))
                return Optional.of(annotation);
        }

        return Optional.empty();
    }
}
