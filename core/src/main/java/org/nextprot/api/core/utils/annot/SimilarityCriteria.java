package org.nextprot.api.core.utils.annot;

import org.nextprot.api.core.domain.annotation.Annotation;

/**
 *
 * Created by fnikitin on 02/08/16.
 */
public interface SimilarityCriteria {

    boolean isSimilar(Annotation annotation1, Annotation annotation2);
}