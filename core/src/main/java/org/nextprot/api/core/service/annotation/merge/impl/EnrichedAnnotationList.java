package org.nextprot.api.core.service.annotation.merge.impl;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.annotation.merge.AnnotationGroup;
import org.nextprot.api.core.service.annotation.merge.EnrichableAnnotationList;

import java.util.ArrayList;
import java.util.Collections;
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
public class EnrichedAnnotationList implements EnrichableAnnotationList {

    private List<Annotation> annotations;

    public EnrichedAnnotationList(List<Annotation> annotations) {

        Preconditions.checkNotNull(annotations);
        Preconditions.checkArgument(!annotations.isEmpty());

        this.annotations = new ArrayList<>(annotations);
    }

    @Override
    public List<Annotation> getMergedAnnotations() {

        return Collections.unmodifiableList(annotations);
    }

    @Override
    public boolean merge(List<Annotation> externalAnnotations) {

        if (externalAnnotations == null || externalAnnotations.isEmpty()) {
            return false;
        }

        annotations = groupSimilarAnnotations(externalAnnotations).stream()
                .map(annotationGroup -> new ReducedAnnotation(annotationGroup.getAnnotations()).reduce())
                .collect(Collectors.toList());

        return true;
    }

    /**
     * Group similar annotations in cluster
     *
     * @param externalAnnotations supplementary annotations
     * @return a list of clusters
     */
    private List<AnnotationGroup> groupSimilarAnnotations(List<Annotation> externalAnnotations) {

        // wrap each annotation from second list in its own cluster
        List<AnnotationGroup> annotationGroups = AnnotationGroup.fromAnnotationList(annotations);

        AnnotationGroupFinder finder = new AnnotationGroupFinder();

        for (Annotation externalAnnotation : externalAnnotations) {

            Optional<AnnotationGroup> foundAnnotationGroup =
                    finder.findAnnotationContainer(externalAnnotation, annotationGroups);

            if (foundAnnotationGroup.isPresent()) {
                try {
                    foundAnnotationGroup.get().add(externalAnnotation);
                } catch (AnnotationGroup.InvalidAnnotationGroupCategoryException e) {
                    throw new NextProtException(e);
                }
            }
            else {
                annotationGroups.add(AnnotationGroup.fromAnnotation(externalAnnotation));
            }
        }

        return annotationGroups;
    }
}
