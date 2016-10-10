package org.nextprot.api.core.utils.annot.merge;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A group of annotation of the same category
 */
public class AnnotationCluster {

    private final AnnotationCategory category;
    private final List<Annotation> group;

    public AnnotationCluster(AnnotationCategory category) {

        Preconditions.checkNotNull(category);
        this.category = category;
        group = new ArrayList<>();
    }

    /**
     * Wrap an annotation into AnnotationCluster
     */
    public static AnnotationCluster valueOf(Annotation annotation) {

        AnnotationCluster annotationCluster = new AnnotationCluster(annotation.getAPICategory());
        annotationCluster.group.add(annotation);
        return annotationCluster;
    }

    /**
     * Wrap annotations into AnnotationClusters
     */
    public static List<AnnotationCluster> valueOfClusters(List<Annotation> annotations) {

        List<AnnotationCluster> annotationClusters = new ArrayList<>(annotations.size());
        annotationClusters.addAll(annotations.stream().map(AnnotationCluster::valueOf).collect(Collectors.toList()));

        return annotationClusters;
    }

    public boolean add(Annotation annotation) throws InvalidAnnotationClusterCategoryException {

        if (annotation.getAPICategory() != category)
            throw new InvalidAnnotationClusterCategoryException(annotation, category);

        return group.add(annotation);
    }

    public int size() {
        return group.size();
    }

    public List<Annotation> getAnnotations() {
        return group;
    }

    public AnnotationCategory getCategory() {

        return category;
    }

    public static class InvalidAnnotationClusterCategoryException extends Exception {

        public InvalidAnnotationClusterCategoryException(Annotation annotation, AnnotationCategory expectedCategory) {

            super("could not add annotation of different category "+annotation.getAPICategory() + " (expected: "+expectedCategory+")");
        }
    }
}