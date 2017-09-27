package org.nextprot.api.commons.utils;

import java.util.Objects;

public abstract class NullableComparison<T> {

    public int compareNullables(T n1, T n2) {

        return compareNullables(n1, n2, true);
    }

    public int compareNullables(T n1, T n2, boolean asc) {

        int cmp;

        if (Objects.equals(n1, n2)) return 0;

        if (n1 == null)
            cmp = -1;
        else if (n2 == null)
            cmp = 1;
        else
            cmp = compare(n1, n2);

        return (asc) ? cmp : -cmp;
    }

    protected abstract int compare(T n1, T n2);
}
