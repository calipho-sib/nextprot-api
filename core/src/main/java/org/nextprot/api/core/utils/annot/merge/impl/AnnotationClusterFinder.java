package org.nextprot.api.core.utils.annot.merge.impl;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.annot.merge.AnnotationCluster;
import org.nextprot.api.core.utils.annot.merge.AnnotationContainerFinder;
import org.nextprot.api.core.utils.annot.merge.SimilarityPredicate;

import java.util.Collection;
import java.util.Optional;

/**
 * Find cluster containing similar annotations
 *
 * Created by fnikitin on 02/08/16.
 */
public class AnnotationClusterFinder implements AnnotationContainerFinder<AnnotationCluster> {

    private final SimilarityPredicate criteria;

    public AnnotationClusterFinder(SimilarityPredicate criteria) {

        Preconditions.checkNotNull(criteria);

        this.criteria = criteria;
    }

    @Override
    public Optional<AnnotationCluster> find(Annotation searchedAnnotation, Collection<AnnotationCluster> annotationClusters) {

        for (AnnotationCluster annotationCluster : annotationClusters) {

            for (Annotation annotation : annotationCluster.getAnnotations()) {
                if (criteria.isSimilar(searchedAnnotation, annotation))
                    return Optional.of(annotationCluster);
            }
        }

        return Optional.empty();
    }
}
