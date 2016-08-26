package org.nextprot.api.core.utils.annot.comp;

import org.junit.Assert;
import org.junit.Test;

public class AnnotationComparatorsTest {

    @Test
    public void testComparePositionAsc() throws Exception {

        int cmp = AnnotationComparators.compareNullableComparableObject(2, 25, true);

        Assert.assertTrue(cmp < 0);
    }

    @Test
    public void testComparePositionDesc() throws Exception {

        int cmp = AnnotationComparators.compareNullableComparableObject(2, 25, false);

        Assert.assertTrue(cmp > 0);
    }

    @Test
    public void testComparePositionWithBeginNull() throws Exception {

        int cmp = AnnotationComparators.compareNullableComparableObject(null, 25);

        Assert.assertTrue(cmp < 0);
    }

    @Test
    public void testComparePositionWithBeginNull2() throws Exception {

        int cmp = AnnotationComparators.compareNullableComparableObject(19, null);

        Assert.assertTrue(cmp > 0);
    }

    @Test
    public void testComparePositionBothNull() throws Exception {

        int cmp = AnnotationComparators.compareNullableComparableObject(null, null);

        Assert.assertTrue(cmp == 0);
    }
}