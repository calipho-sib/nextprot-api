package org.nextprot.api.commons.utils;

public class NullableComparable<T extends Comparable<T>> extends NullableComparison<T> {

    @Override
    protected int compare(T n1, T n2) {

        return n1.compareTo(n2);
    }
}
