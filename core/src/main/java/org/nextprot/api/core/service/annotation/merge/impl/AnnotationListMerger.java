package org.nextprot.api.core.service.annotation.merge.impl;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.annotation.Annotation;
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

    private final String geneName;
    private final SimilarGroupBuilder similarGroupBuilder;

    // TODO: we should check the unicity of the given annotations
    public AnnotationListMerger(String geneName, List<Annotation> uniqueAnnotations) {

        Preconditions.checkNotNull(geneName);
        Preconditions.checkNotNull(uniqueAnnotations);
        Preconditions.checkArgument(!uniqueAnnotations.isEmpty());

        this.geneName = geneName;
        this.similarGroupBuilder = new SimilarGroupBuilder(uniqueAnnotations);
    }

    public List<Annotation> merge(List<Annotation> otherAnnotations) {

        return similarGroupBuilder.groupBySimilarity(otherAnnotations).stream()
                .map(annotationGroup -> new ReducedAnnotation(geneName, annotationGroup).reduce())
                .collect(Collectors.toList());
    }
}
