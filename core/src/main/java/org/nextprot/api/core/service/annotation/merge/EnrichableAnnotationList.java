package org.nextprot.api.core.service.annotation.merge;

import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.List;

/**
 * Instance of this interface should be able to handle more annotations and merge similar ones
 */
public interface EnrichableAnnotationList {

    /**
     * @return the merged annotations
     */
    List<Annotation> getMergedAnnotations();

    /**
     * Merge given annotations to internal annotations
     * @return true if merge has been correctly done
     **/
    boolean merge(List<Annotation> moreAnnotations);
}
