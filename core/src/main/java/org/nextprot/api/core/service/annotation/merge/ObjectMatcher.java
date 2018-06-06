package org.nextprot.api.core.service.annotation.merge;

/**
 * Define contract to match 2 objects
 */
public interface ObjectMatcher<T> {

    /**
     * @return true if both objects matches else false
     */
    boolean match(T o1, T o2);
}
