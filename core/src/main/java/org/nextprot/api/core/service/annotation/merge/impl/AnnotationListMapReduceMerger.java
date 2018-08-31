package org.nextprot.api.core.service.annotation.merge.impl;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.annotation.merge.AnnotationCluster;
import org.nextprot.api.core.service.annotation.merge.AnnotationListMerger;
import org.nextprot.api.core.service.annotation.merge.AnnotationMerger;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Merge two lists of annotations together and return a new list.
 * <p>
 * Merging is done in two steps:
 *
 * <ol>
 * <li>a mapping step where groups of annotations are made by similarity</li>
 * <li>a reducing (merging) step where each group is reduced to a merged annotation</li>
 * </ol>
 */
public class AnnotationListMapReduceMerger implements AnnotationListMerger {

    private final AnnotationMerger annotationMerger;

    public AnnotationListMapReduceMerger() {

        this(new AnnotationUpdater());
    }

    public AnnotationListMapReduceMerger(AnnotationMerger annotationMerger) {

        Preconditions.checkNotNull(annotationMerger);

        this.annotationMerger = annotationMerger;
    }

    // throw an exception when cluster size > 2
    /** @return merged annotations */
    public List<Annotation> merge(List<Annotation> annotations1, List<Annotation> annotations2) {

        if (annotations1 == null || annotations1.isEmpty()) {
            return annotations2;
        }
        else if (annotations2 == null || annotations2.isEmpty()) {
            return annotations1;
        }

        // Reduce clusters into merged annotations
        return clusterSimilarAnnotations(annotations1, annotations2).stream()
                .map(this::doMerge)
                .collect(Collectors.toList());
    }

    /**
     * Map similar annotations in cluster
     *
     * @param annotationList1 first annotation list
     * @param annotationList2 second annotation list
     * @return a list of clusters
     */
    private List<AnnotationCluster> clusterSimilarAnnotations(List<Annotation> annotationList1, List<Annotation> annotationList2) {

        // wrap each annotation from second list in its own cluster
        List<AnnotationCluster> annotationClusters = AnnotationCluster.valueOfClusters(annotationList2);

        //clusterAnnotations(annotationList2, annotationClusters);
        clusterAnnotations(annotationList1, annotationClusters);

        return annotationClusters;
    }

    private void clusterAnnotations(List<Annotation> annotations, List<AnnotationCluster> annotationClusters) {

        AnnotationClusterFinder finder = new AnnotationClusterFinder();

        for (Annotation annotation : annotations) {

            Optional<AnnotationCluster> foundAnnotationCluster = finder.find(annotation, annotationClusters);

            if (foundAnnotationCluster.isPresent()) {
                try {
                    foundAnnotationCluster.get().add(annotation);
                } catch (AnnotationCluster.InvalidAnnotationClusterCategoryException e) {
                    throw new NextProtException(e);
                }
            }
            else {
                annotationClusters.add(AnnotationCluster.valueOf(annotation));
            }
        }
    }

    private Annotation doMerge(AnnotationCluster cluster) {

        if (cluster.size() == 0)
            throw new IllegalStateException("cluster "+ cluster.getCategory()+" should not be empty");
        else if (cluster.size() == 1)
            return cluster.getAnnotations().get(0);
        else if (cluster.size() == 2)
            // the first annotation is the original one
            return annotationMerger.merge(cluster.getAnnotations().get(0), cluster.getAnnotations().get(1));
        else {
            throw new NextProtException("cannot merge more than 2 annotations from cluster "+cluster.getAnnotations());
        }
    }
}
