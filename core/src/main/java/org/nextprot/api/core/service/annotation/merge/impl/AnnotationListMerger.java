package org.nextprot.api.core.service.annotation.merge.impl;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.EntityNameService;
import org.nextprot.api.core.service.annotation.merge.SimilarGroupBuilder;

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
public class AnnotationListMerger {

    private final SimilarGroupBuilder similarGroupBuilder;
    private final EntityNameService entityNameService;

    // TODO: we should check the unicity of the given annotations
    public AnnotationListMerger(List<Annotation> uniqueAnnotations, EntityNameService entityNameService) {

        Preconditions.checkNotNull(uniqueAnnotations);
        Preconditions.checkNotNull(entityNameService);
        Preconditions.checkArgument(!uniqueAnnotations.isEmpty());

        this.entityNameService = entityNameService;
        this.similarGroupBuilder = new SimilarGroupBuilder(uniqueAnnotations);
    }

    public List<Annotation> merge(List<Annotation> otherAnnotations) {

        return similarGroupBuilder.groupBySimilarity(otherAnnotations).stream()
                .map(annotationGroup -> new ReducedAnnotation(annotationGroup, entityNameService).reduce())
                .collect(Collectors.toList());
    }
}
