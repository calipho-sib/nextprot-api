package org.nextprot.api.core.utils.annot.comp;

import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.Comparator;
import java.util.Map;

/**
 * A comparator of Annotations that needs an access to annotations from AnnotationHash
 *
 * Created by fnikitin on 24/08/16.
 */
public interface HashableAnnotationComparator extends Comparator<Annotation> {

    Map<String, Annotation> getHashableAnnotations();
}
