package org.nextprot.api.commons.utils;

import java.util.Objects;

abstract class NullableComparison<T> {

    /**
     * Compare nullables objects (null comes first)
     * @param n1 first nullable object
     * @param n2 second nullable object
     * @return 0 if the same, -1 if n1 comes first else +1
     */
    public int compareNullables(T n1, T n2) {

        return compareNullables(n1, n2, true);
    }

    /**
     * Compare nullables objects
     * @param n1 first nullable object
     * @param n2 second nullable object
     * @param nullComeFirst null comes first if true
     * @return 0 if the same, -1 if n1 comes first else +1
     */
    public int compareNullables(T n1, T n2, boolean nullComeFirst) {

        if (Objects.equals(n1, n2)) return 0;

        if (n1 == null)
            return (nullComeFirst) ? -1 : 1;
        else if (n2 == null)
            return (nullComeFirst) ? 1 : -1;
        return compare(n1, n2);
    }

    protected abstract int compare(T n1, T n2);
}
