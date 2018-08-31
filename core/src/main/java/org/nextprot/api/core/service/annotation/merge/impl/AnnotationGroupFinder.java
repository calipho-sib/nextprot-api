package org.nextprot.api.core.service.annotation.merge.impl;

import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.annotation.merge.AnnotationContainerFinder;
import org.nextprot.api.core.service.annotation.merge.AnnotationGroup;

import java.util.Collection;
import java.util.Optional;

/**
 * Find cluster containing similar annotations
 *
 * Created by fnikitin on 02/08/16.
 */
public class AnnotationGroupFinder implements AnnotationContainerFinder<AnnotationGroup> {

    private final AnnotationFinder annotationFinder = new AnnotationFinder();

    @Override
    public Optional<AnnotationGroup> findAnnotationContainer(Annotation searchedAnnotation, Collection<AnnotationGroup> annotationGroups) {

        for (AnnotationGroup annotationGroup : annotationGroups) {
            if (annotationFinder.findAnnotationContainer(searchedAnnotation, annotationGroup.getAnnotations()).isPresent())
                return Optional.of(annotationGroup);
        }

        return Optional.empty();
    }
}
