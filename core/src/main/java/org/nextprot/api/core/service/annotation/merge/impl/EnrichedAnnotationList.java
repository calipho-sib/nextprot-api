package org.nextprot.api.core.service.annotation.merge.impl;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.annotation.merge.AnnotationGroup;
import org.nextprot.api.core.service.annotation.merge.AnnotationPairReduction;
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

    private final AnnotationPairReduction annotationPairReduction;
    private List<Annotation> annotations;

    public EnrichedAnnotationList(List<Annotation> annotations) {

        this(annotations, new AnnotationUpdater());
    }

    private EnrichedAnnotationList(List<Annotation> originalAnnotations, AnnotationPairReduction annotationPairReduction) {

        Preconditions.checkNotNull(originalAnnotations);
        Preconditions.checkArgument(!originalAnnotations.isEmpty());
        Preconditions.checkNotNull(annotationPairReduction);

        this.annotations = new ArrayList<>(originalAnnotations);
        this.annotationPairReduction = annotationPairReduction;
    }

    @Override
    public List<Annotation> getAnnotations() {

        return Collections.unmodifiableList(annotations);
    }

    /** @return merged annotations */
    @Override
    public boolean merge(List<Annotation> externalAnnotations) {

        if (externalAnnotations == null || externalAnnotations.isEmpty()) {
            return false;
        }

        annotations = groupSimilarAnnotations(externalAnnotations).stream()
                .map(this::reduceClusterAnnotations)
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

    private Annotation reduceClusterAnnotations(AnnotationGroup group) {

        if (group.size() == 0)
            throw new IllegalStateException("cluster "+ group.getCategory()+" should not be empty");
        else if (group.size() == 1)
            return group.getAnnotations().get(0);
        else if (group.size() == 2)
            // the first annotation is the original one
            return annotationPairReduction.reduce(group.getAnnotations().get(0), group.getAnnotations().get(1));
        else {
            // TODO
            throw new NextProtException("TODO: no yet able to reduce more than 2 annotations from group "+group.getAnnotations());
        }
    }
}
