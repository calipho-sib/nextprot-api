package org.nextprot.api.core.utils.annot;

import org.nextprot.api.core.domain.annotation.Annotation;

/**
 * Merge two annotations in one
 *
 * Created by fnikitin on 02/08/16.
 */
public interface AnnotationMerger {

    /**
     * Update target annotation with source
     *
     * @param target the annotation to update
     * @param source the annotation to provide more informations
     */
     void update(Annotation target, Annotation source);
}
