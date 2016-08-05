package org.nextprot.api.core.utils.annot;

import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.annot.impl.AnnotationClusterFinder;

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
public class Merger {

    public List<Annotation> merge(List<Annotation> annotations1, List<Annotation> annotations2) {

        if (annotations1 == null || annotations1.isEmpty()) {
            return annotations2;
        }
        else if (annotations2 == null || annotations2.isEmpty()) {
            return annotations1;
        }

        return reduce(map(annotations1, annotations2));
    }

    private List<AnnotationCluster> map(List<Annotation> srcAnnotationList, List<Annotation> destAnnotationList) {

        List<AnnotationCluster> annotationClusters = AnnotationCluster.valueOfClusters(destAnnotationList);

        for (Annotation srcAnnotation : srcAnnotationList) {

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
