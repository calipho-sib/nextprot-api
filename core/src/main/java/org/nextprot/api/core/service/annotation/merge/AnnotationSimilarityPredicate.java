package org.nextprot.api.core.service.annotation.merge;

import org.nextprot.api.core.domain.annotation.Annotation;

/**
 * Defines the contract to evaluate similarity between 2 annotations
 *
 * Created by fnikitin on 02/08/16.
 */
public interface AnnotationSimilarityPredicate {

    /**
     * @return true if annotations are similar else false
     */
    boolean isSimilar(Annotation annotation1, Annotation annotation2);
}