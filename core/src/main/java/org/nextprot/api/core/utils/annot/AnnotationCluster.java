package org.nextprot.api.core.utils.annot;

import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.ArrayList;
import java.util.List;

public class AnnotationCluster {

    private final List<Annotation> group;

    public AnnotationCluster() {

        group = new ArrayList<>();
    }

    public static AnnotationCluster valueOf(Annotation annotation) {

        AnnotationCluster annotationCluster = new AnnotationCluster();
        annotationCluster.add(annotation);
        return annotationCluster;
    }

    public static List<AnnotationCluster> valueOfClusters(List<Annotation> annotations) {

        List<AnnotationCluster> annotationClusters = new ArrayList<>(annotations.size());
        for (Annotation annotation : annotations) {

            annotationClusters.add(valueOf(annotation));
        }
        return annotationClusters;
    }

    public boolean add(Annotation annotation) {

        return group.add(annotation);
    }

    public int size() {
        return group.size();
    }

    public List<Annotation> getAnnotations() {
        return group;
    }
}