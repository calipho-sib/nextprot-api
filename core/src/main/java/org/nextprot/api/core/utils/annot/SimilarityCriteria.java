package org.nextprot.api.core.utils.annot;

import org.nextprot.api.core.domain.annotation.Annotation;

/**
 * Defines the contract to evaluate similarity of 2 annotations
 *
 * Created by fnikitin on 02/08/16.
 */
public interface SimilarityCriteria {

    /** @return true if annotations are similar else false */
    boolean isSimilar(Annotation annotation1, Annotation annotation2);
}