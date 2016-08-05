package org.nextprot.api.core.utils.annot;

import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.Collection;

public interface Finder<T> {

    T find(Annotation searchedAnnotation, Collection<T> annotations);
}
