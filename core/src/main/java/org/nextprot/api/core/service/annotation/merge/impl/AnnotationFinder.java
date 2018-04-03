package org.nextprot.api.core.service.annotation.merge.impl;

import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.annotation.merge.AnnotationContainerFinder;
import org.nextprot.api.core.service.annotation.merge.AnnotationSimilarityPredicate;

import java.util.Collection;
import java.util.Optional;

/**
 * Find similar annotation in collection of annotations
 *
 * Created by fnikitin on 02/08/16.
 */
public class AnnotationFinder implements AnnotationContainerFinder<Annotation> {

    @Override
    public Optional<Annotation> find(Annotation searchedAnnotation, Collection<Annotation> annotations) {

        Optional<AnnotationSimilarityPredicate> predicate = newPredicate(searchedAnnotation);

        if (predicate.isPresent()) {
            for (Annotation annotation : annotations) {
                if (predicate.get().isSimilar(searchedAnnotation, annotation))
                    return Optional.of(annotation);
            }
        }

        return Optional.empty();
    }

    protected Optional<AnnotationSimilarityPredicate> newPredicate(Annotation annotation) {

        return AnnotationSimilarityPredicate.newSimilarityPredicate(annotation.getAPICategory());
    }
}
