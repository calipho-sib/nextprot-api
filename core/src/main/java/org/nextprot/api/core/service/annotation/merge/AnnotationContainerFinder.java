package org.nextprot.api.core.service.annotation.merge;

import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.Collection;
import java.util.Optional;

/**
 * Find Annotation container
 *
 * @param <T> annotation container object
 */
public interface AnnotationContainerFinder<T> {

    /**
     * Find object containing the given annotation
     * @param searchedAnnotation the annotation to search in container
     * @param objects the objects to find annotation in
     * @return optional container object
     */
    Optional<T> findAnnotationContainer(Annotation searchedAnnotation, Collection<T> objects);
}
