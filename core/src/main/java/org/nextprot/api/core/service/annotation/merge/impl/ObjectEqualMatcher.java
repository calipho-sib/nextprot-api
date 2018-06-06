package org.nextprot.api.core.service.annotation.merge.impl;

import com.google.common.base.Objects;
import org.nextprot.api.core.service.annotation.merge.ObjectMatcher;

/**
 * Equals()-based implementation
 */
public class ObjectEqualMatcher<T> implements ObjectMatcher<T> {

    @Override
    public boolean match(T o1, T o2) {

        return Objects.equal(o1, o2);
    }
}
