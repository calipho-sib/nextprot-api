package org.nextprot.api.core.utils.annot;

import org.nextprot.api.core.domain.annotation.Annotation;

/**
 * Define contract to access an object contained in an annotation
 */
public interface ObjectAccessor {

    /**
     * @return object accessible from annotation (can be null)
     */
    Object getObject(Annotation annotation);
}
