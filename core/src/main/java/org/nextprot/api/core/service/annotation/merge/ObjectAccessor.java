package org.nextprot.api.core.service.annotation.merge;

import org.nextprot.api.core.domain.annotation.Annotation;

/**
 * Define contract to access an object contained in an annotation
 */
public interface ObjectAccessor<T> {

    /**
     * @return object accessible from annotation (can be null)
     */
    T getObject(Annotation annotation);
}
