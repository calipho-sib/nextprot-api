package org.nextprot.api.core.utils.annot.impl;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.annot.AnnotationCluster;
import org.nextprot.api.core.utils.annot.Finder;
import org.nextprot.api.core.utils.annot.SimilarityPredicate;

import java.util.Collection;

/**
 * Find cluster containing similar annotations
 *
 * Created by fnikitin on 02/08/16.
 */
public class AnnotationClusterFinder implements Finder<AnnotationCluster> {

    private final SimilarityPredicate criteria;

    public AnnotationClusterFinder(SimilarityPredicate criteria) {

        Preconditions.checkNotNull(criteria);

        this.criteria = criteria;
    }

    public static AnnotationClusterFinder valueOf(AnnotationCategory category) {

        return new AnnotationClusterFinder(SimilarityPredicateFactory.newSimilarityPredicate(category));
    }

    @Override
    public AnnotationCluster find(Annotation searchedAnnotation, Collection<AnnotationCluster> annotationClusters) {

        for (AnnotationCluster annotationCluster : annotationClusters) {

            for (Annotation annotation : annotationCluster.getAnnotations()) {
                if (criteria.isSimilar(searchedAnnotation, annotation))
                    return annotationCluster;
            }
        }

        return null;
    }
}
