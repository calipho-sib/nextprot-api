package org.nextprot.api.commons.utils;

import com.google.common.base.Preconditions;

import java.util.Comparator;

public class NullableComparator<T> extends NullableComparison<T> {

    private final Comparator<T> comparator;

    public NullableComparator(Comparator<T> comparator) {

        Preconditions.checkNotNull(comparator);
        this.comparator = comparator;
    }

    @Override
    protected int compare(T n1, T n2) {

        return comparator.compare(n1, n2);
    }
}
