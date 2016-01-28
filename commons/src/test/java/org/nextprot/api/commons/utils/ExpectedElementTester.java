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
    private final Collection<T> observedCollection;

    protected ExpectedElementTester(Collection<T> observedCollection) {

        elementToKeyFunc = createElementToKeyFunc();
        this.observedCollection = observedCollection;
    }

    /**
     * @return true if expected element was found with expected content
     * @param expectedElementKey the expected element key
     * @param expectedElementValues the element values
     */
    public boolean containsWithExpectedContent(K expectedElementKey, Map<String, Object> expectedElementValues) {

        T foundElement = getElementFromKey(expectedElementKey);

        return foundElement != null && hasExpectedContent(foundElement, expectedElementValues);
    }

    /**
     * @return element with given key from a collection of T objects
     * @param expectedElementKey the expected element key
     */
    private T getElementFromKey(K expectedElementKey) {

        for (T element : observedCollection) {

            if (expectedElementKey.equals(elementToKeyFunc.apply(element))) {

                return element;
            }
        }

        return null;
    }

    /**
     * @return a function that extract key from an element
     */
    protected abstract Function<T, K> createElementToKeyFunc();

    /**
     * @return true if object contains given expected values
     * @param object the object to test content
     * @param expectedValues the object's expected values
     */
    protected abstract boolean hasExpectedContent(T object, Map<String, Object> expectedValues);
}
