package org.nextprot.api.core.utils.annot.impl;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.annot.AnnotationCluster;
import org.nextprot.api.core.utils.annot.AnnotationListMerger;
import org.nextprot.api.core.utils.annot.AnnotationMerger;

import java.util.List;
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

    private final AnnotationMerger updater = new AnnotationUpdater();

    /** @return merged annotations */
    public List<Annotation> merge(List<Annotation> annotations1, List<Annotation> annotations2) {

        if (annotations1 == null || annotations1.isEmpty()) {
            return annotations2;
        }
        else if (annotations2 == null || annotations2.isEmpty()) {
            return annotations1;
        }

        return reduce(map(annotations1, annotations2));
    }

    /**
     * Map similar annotations in cluster
     *
     * @param annotations1 first annotation list
     * @param annotations2 second annotation list
     * @return a list of clusters
     */
    private List<AnnotationCluster> map(List<Annotation> annotations1, List<Annotation> annotations2) {

        List<AnnotationCluster> annotationClusters = AnnotationCluster.valueOfClusters(annotations2);

        for (Annotation srcAnnotation : annotations1) {

            AnnotationClusterFinder finder = AnnotationClusterFinder.valueOf(srcAnnotation.getAPICategory());

            AnnotationCluster foundAnnotationCluster = finder.find(srcAnnotation, annotationClusters);

            if (foundAnnotationCluster == null) {

                annotationClusters.add(AnnotationCluster.valueOf(srcAnnotation));
            }
            else {
                try {
                    foundAnnotationCluster.add(srcAnnotation);
                } catch (AnnotationCluster.InvalidAnnotationClusterCategoryException e) {

                    throw new NextProtException(e);
                }
            }
        }

        return annotationClusters;
    }

    /**
     * Reduce clusters into merged annotations
     * @param annotationClusters the clusters to reduce
     * @return a list of merged annotations
     */
    private List<Annotation> reduce(List<AnnotationCluster> annotationClusters) {

        return annotationClusters.stream().map(this::doMerge).collect(Collectors.toList());
    }

    private Annotation doMerge(AnnotationCluster cluster) {

        if (cluster.size() == 0)
            throw new IllegalStateException("cluster "+ cluster.getCategory()+" should not be empty");
        else if (cluster.size() == 1)
            return cluster.getAnnotations().get(0);
        else if (cluster.size() == 2)
            // the first annotation is the original one
            return updater.merge(cluster.getAnnotations().get(0), cluster.getAnnotations().get(1));
        else {
            Annotation[] otherSources = new Annotation[cluster.getAnnotations().size() - 2];
            cluster.getAnnotations().subList(2, cluster.getAnnotations().size()).toArray(otherSources);

            return updater.merge(cluster.getAnnotations().get(0), cluster.getAnnotations().get(1), otherSources);
        }
    }
}
