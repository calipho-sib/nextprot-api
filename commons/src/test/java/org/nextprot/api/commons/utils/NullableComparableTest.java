package org.nextprot.api.commons.utils;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class NullableComparableTest {

    private final NullableComparable<Integer> nullableComparable = new NullableComparable<>();

    @Test
    public void testComparePositionNullComesFirst() throws Exception {

        int cmp = nullableComparable.compareNullables(null, 25);

        Assert.assertTrue(cmp < 0);
    }

    @Test
    public void testComparePositionNullComesLast() throws Exception {

        int cmp = nullableComparable.compareNullables(null, 25, false);

        Assert.assertTrue(cmp > 0);
    }

    @Test
    public void testComparePositionNullComesFirst2() throws Exception {

        int cmp = nullableComparable.compareNullables(19, null);

        Assert.assertTrue(cmp > 0);
    }

    @Test
    public void testComparePositionBothNull() throws Exception {

        int cmp = nullableComparable.compareNullables(null, null);

        Assert.assertTrue(cmp == 0);
    }
}