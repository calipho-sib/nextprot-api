package org.nextprot.api.core.service.annotation.merge;

import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.List;

public interface AnnotationListMerger {

    List<Annotation> merge(List<Annotation> annotations1, List<Annotation> annotations2);
}
