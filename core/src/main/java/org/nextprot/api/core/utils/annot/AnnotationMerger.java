package org.nextprot.api.core.utils.annot;

import org.nextprot.api.core.domain.annotation.Annotation;

/**
 * Merge annotations in one
 *
 * Created by fnikitin on 02/08/16.
 */
public interface AnnotationMerger {

    /**
     * Update target annotation with source
     *
     * @param target the annotation to merge
     * @param source the annotation source
     * @param otherSources the other annotation sources
     */
    Annotation merge(Annotation target, Annotation source, Annotation... otherSources);
}
