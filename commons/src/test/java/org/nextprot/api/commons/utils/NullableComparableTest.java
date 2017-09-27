package org.nextprot.api.commons.utils;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class NullableComparableTest {

    private final NullableComparable<Integer> nullableComparable = new NullableComparable<>();

    @Test
    public void testComparePositionAsc() throws Exception {

        int cmp = nullableComparable.compareNullables(2, 25, true);

        Assert.assertTrue(cmp < 0);
    }

    @Test
    public void testComparePositionDesc() throws Exception {

        int cmp = nullableComparable.compareNullables(2, 25, false);

        Assert.assertTrue(cmp > 0);
    }

    @Test
    public void testComparePositionWithBeginNull() throws Exception {

        int cmp = nullableComparable.compareNullables(null, 25);

        Assert.assertTrue(cmp < 0);
    }

    @Test
    public void testComparePositionWithBeginNull2() throws Exception {

        int cmp = nullableComparable.compareNullables(19, null);

        Assert.assertTrue(cmp > 0);
    }

    @Test
    public void testComparePositionBothNull() throws Exception {

        int cmp = nullableComparable.compareNullables(null, null);

        Assert.assertTrue(cmp == 0);
    }
}