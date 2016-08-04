package org.nextprot.api.core.utils.annot;

/**
 * Define contract to match 2 objects
 */
public interface ObjectMatcher {

    /**
     * @return true if both objects matches else false
     */
    boolean match(Object o1, Object o2);
}
