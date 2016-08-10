package org.nextprot.api.core.utils.annot.merge;

import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.Collection;

public interface Finder<T> {

    T find(Annotation searchedAnnotation, Collection<T> annotations);
}
