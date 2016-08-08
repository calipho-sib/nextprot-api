package org.nextprot.api.core.utils.annot;

import org.nextprot.api.core.domain.annotation.Annotation;

/**
 * Merge annotations in one
 *
 * Created by fnikitin on 02/08/16.
 */
public interface AnnotationMerger {

    Annotation merge(Annotation annotation1, Annotation annotation2, Annotation... others);
}
