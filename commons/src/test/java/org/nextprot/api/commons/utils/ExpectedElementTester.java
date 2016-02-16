package org.nextprot.api.commons.utils;

import com.google.common.base.Function;

import java.util.Collection;
import java.util.Map;

/**
 * This object is used in tests to check that a collection contains
 * an expected element with expected values
 *
 * Created by fnikitin on 22/01/16.
 */
public abstract class ExpectedElementTester<T, K> {

    private final Function<T, K> elementToKeyFunc;

    public ExpectedElementTester() {

        elementToKeyFunc = createElementToKeyFunc();
    }

    /**
     * @return true if expected element was found
     * @param coll the collection to fetch expected element
     * @param expectedElementKey the expected element key
     * @param expectedElementValues the element values
     */
    public boolean testElement(Collection<T> coll, K expectedElementKey, Map<String, Object> expectedElementValues) {

        T foundElement = getElementFromKey(coll, expectedElementKey);

        return foundElement != null && isValidContent(foundElement, expectedElementValues);
    }

    /**
     * @return element with given key from a collection of T objects
     * @param coll the collection to fetch expected element
     * @param expectedElementKey the expected element key
     */
    private T getElementFromKey(Collection<T> coll, K expectedElementKey) {

        for (T element : coll) {

            if (expectedElementKey.equals(elementToKeyFunc.apply(element))) {

                return element;
            }
        }

        return null;
    }

    protected abstract Function<T, K> createElementToKeyFunc();

    protected abstract boolean isValidContent(T element, Map<String, Object> expectedElementValues);
}
