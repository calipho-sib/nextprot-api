package org.nextprot.api.commons.utils;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

import java.util.Collection;

/**
 * This object is used in tests to check that a collection contains
 * expected elements with expected values
 *
 * Created by fnikitin on 22/01/16.
 */
public abstract class CollectionTester<E, K> {

    private final Function<E, K> elementToKeyFunc;
    private final Collection<E> collectionToTest;

    protected CollectionTester(Collection<E> collectionToTest) {

        Preconditions.checkNotNull(collectionToTest);
        Preconditions.checkNotNull(createElementToKeyFunc());

        elementToKeyFunc = createElementToKeyFunc();
        this.collectionToTest = collectionToTest;
    }

    /**
     * @return true if expected element was found in collection with expected content
     * @param expectedElement the expected element
     */
    public boolean contains(E expectedElement) {

        E foundElement = getElementFromKey(elementToKeyFunc.apply(expectedElement));

        return foundElement != null && isEquals(foundElement, expectedElement);
    }

    /**
     * @return true if all expected elements were found in collection with expected content
     * @param expectedElements the expected elements
     */
    public boolean contains(Collection<E> expectedElements) {

        if (expectedElements.size() != collectionToTest.size())
            return false;

        for (E expectedElement : expectedElements) {

            if (!contains(expectedElement)) return false;
        }

        return true;
    }

    /**
     * Extract element from the collection with the given key
     * @param elementKey the element key
     * @return element with given key from the collection of T objects
     */
    private E getElementFromKey(K elementKey) {

        for (E element : collectionToTest) {

            if (elementKey.equals(elementToKeyFunc.apply(element))) {

                return element;
            }
        }
        return null;
    }

    /**
     * @return a function that extract key from an element
     */
    protected abstract Function<E, K> createElementToKeyFunc();

    /**
     * @return true if object contains given expected values
     * @param element the element to test content
     * @param expected the expected object
     */
    protected abstract boolean isEquals(E element, E expected);
}
