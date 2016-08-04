package org.nextprot.api.core.utils.annot.impl;

import org.nextprot.api.core.utils.annot.ObjectMatcher;

/**
 * Equals()-based implementation
 */
public class ObjectEqualMatcher<T> implements ObjectMatcher<T> {

    @Override
    public boolean match(T o1, T o2) {

        return ObjectSimilarityPredicate.equalObjects(o1, o2);
    }
}
