package org.nextprot.api.core.service.annotation.merge;

import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.List;

/**
 * Reduce n annotations in 1
 *
 * Created by fnikitin on 02/08/16.
 */
public interface AnnotationListReduction {

    List<Annotation> getOriginalAnnotations();

    Annotation reduce();
}
