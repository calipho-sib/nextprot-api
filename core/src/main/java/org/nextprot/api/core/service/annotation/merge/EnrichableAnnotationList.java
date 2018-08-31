package org.nextprot.api.core.service.annotation.merge;

import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.List;

/**
 * Instance of this interface should be able to handle more annotations and merge similar ones
 */
public interface EnrichableAnnotationList {

    List<Annotation> getAnnotations();

    boolean merge(List<Annotation> moreAnnotations);
}
