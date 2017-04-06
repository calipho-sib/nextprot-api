package org.nextprot.api.core.utils.annot.merge.impl;

import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.annot.merge.AnnotationCluster;
import org.nextprot.api.core.utils.annot.merge.AnnotationContainerFinder;

import java.util.Collection;
import java.util.Optional;

/**
 * Find cluster containing similar annotations
 *
 * Created by fnikitin on 02/08/16.
 */
public class AnnotationClusterFinder implements AnnotationContainerFinder<AnnotationCluster> {

    private final AnnotationFinder annotationFinder = new AnnotationFinder();

    @Override
    public Optional<AnnotationCluster> find(Annotation searchedAnnotation, Collection<AnnotationCluster> annotationClusters) {

        for (AnnotationCluster annotationCluster : annotationClusters) {
            if (annotationFinder.find(searchedAnnotation, annotationCluster.getAnnotations()).isPresent())
                return Optional.of(annotationCluster);
        }

        return Optional.empty();
    }
}
