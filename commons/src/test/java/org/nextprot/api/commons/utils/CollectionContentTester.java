package org.nextprot.api.commons.utils;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

import java.util.Collection;
import java.util.Map;

/**
 * This object is used in tests to check that a collection contains
 * an expected element with expected values
 *
 * Created by fnikitin on 22/01/16.
 */
public abstract class CollectionContentTester<T, K> {

    private final Function<T, K> elementToKeyFunc;
    private final Collection<T> collection;

    protected CollectionContentTester(Collection<T> collection) {

        Preconditions.checkNotNull(collection);

        elementToKeyFunc = createElementToKeyFunc();
        this.collection = collection;
    }

    /**
     * @return true if expected element was found in collection with expected content
     * @param expectedElementKey the expected element key
     * @param expectedElementValues the element values
     */
    public boolean hasElementWithContent(K expectedElementKey, Map<String, Object> expectedElementValues) {

        T foundElement = getElementFromKey(expectedElementKey);

        return foundElement != null && hasExpectedContent(foundElement, expectedElementValues);
    }

    /**
     * Extract element from the collection with the given key
     * @param elementKey the element key
     * @return element with given key from the collection of T objects
     */
    private T getElementFromKey(K elementKey) {

        for (T element : collection) {

            if (elementKey.equals(elementToKeyFunc.apply(element))) {

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
