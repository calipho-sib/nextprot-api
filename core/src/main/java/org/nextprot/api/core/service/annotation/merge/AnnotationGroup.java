package org.nextprot.api.core.service.annotation.merge;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A group of annotation of the same category
 */
public class AnnotationGroup {

    private final AnnotationCategory category;
    private final List<Annotation> group;

    public AnnotationGroup(AnnotationCategory category) {

        Preconditions.checkNotNull(category);
        this.category = category;
        group = new ArrayList<>();
    }

    public static AnnotationGroup fromAnnotation(Annotation annotation) {

        AnnotationGroup annotationGroup = new AnnotationGroup(annotation.getAPICategory());
        annotationGroup.group.add(annotation);
        return annotationGroup;
    }

    public static List<AnnotationGroup> fromAnnotationList(List<Annotation> annotations) {

        List<AnnotationGroup> annotationGroups = new ArrayList<>(annotations.size());
        annotationGroups.addAll(annotations.stream()
                .map(AnnotationGroup::fromAnnotation)
                .collect(Collectors.toList()));

        return annotationGroups;
    }

    public boolean add(Annotation annotation) throws InvalidAnnotationGroupCategoryException {

        if (annotation.getAPICategory() != category)
            throw new InvalidAnnotationGroupCategoryException(annotation, category);

        return group.add(annotation);
    }

    public int size() {

        return group.size();
    }

    public List<Annotation> getAnnotations() {

        return Collections.unmodifiableList(group);
    }

    public AnnotationCategory getCategory() {

        return category;
    }

    public static class InvalidAnnotationGroupCategoryException extends Exception {

        public InvalidAnnotationGroupCategoryException(Annotation annotation, AnnotationCategory expectedCategory) {

            super("could not add annotation of different category "+annotation.getAPICategory() + " (expected: "+expectedCategory+")");
        }
    }
}