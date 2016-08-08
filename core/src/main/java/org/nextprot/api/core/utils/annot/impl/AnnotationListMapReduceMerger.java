package org.nextprot.api.core.utils.annot.impl;

import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.annot.AnnotationCluster;
import org.nextprot.api.core.utils.annot.AnnotationListMerger;

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

                foundAnnotationCluster.add(srcAnnotation);
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

        return annotationClusters.stream().map(this::merge).collect(Collectors.toList());
    }

    // TODO: implement the merge
    private Annotation merge(AnnotationCluster cluster) {

        if (cluster.size() == 1)
            return cluster.getAnnotations().get(0);

        // do the merge

        return null;
    }
}
