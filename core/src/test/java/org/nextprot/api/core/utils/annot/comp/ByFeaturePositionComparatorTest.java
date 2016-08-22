package org.nextprot.api.core.utils.annot.comp;

import org.junit.Assert;
import org.junit.Test;

public class ByFeaturePositionComparatorTest {

    @Test
    public void testComparePositionAsc() throws Exception {

        int cmp = ByFeaturePositionComparator.compareNullablePositions(2, 25, true);

        Assert.assertTrue(cmp < 0);
    }

    @Test
    public void testComparePositionDesc() throws Exception {

        int cmp = ByFeaturePositionComparator.compareNullablePositions(2, 25, false);

        Assert.assertTrue(cmp > 0);
    }

    @Test
    public void testComparePositionWithBeginNull() throws Exception {

        int cmp = ByFeaturePositionComparator.compareNullablePositions(null, 25, true);

        Assert.assertTrue(cmp < 0);
    }

    @Test
    public void testComparePositionWithBeginNull2() throws Exception {

        int cmp = ByFeaturePositionComparator.compareNullablePositions(19, null, true);

        Assert.assertTrue(cmp > 0);
    }

    @Test
    public void testComparePositionBothNull() throws Exception {

        int cmp = ByFeaturePositionComparator.compareNullablePositions(null, null, true);

        Assert.assertTrue(cmp == 0);
    }

    @Test
    public void testCompareDifferentEnds() throws Exception {

        int cmp = ByFeaturePositionComparator.compareAnnotByNullablePosition(2, 1012, 2, 1042);

        Assert.assertTrue(cmp > 0);
    }
}