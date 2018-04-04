package org.nextprot.api.core.service.annotation.comp;

import org.junit.Assert;
import org.junit.Test;

public class ByFeaturePositionComparatorTest {

    @Test
    public void testCompareDifferentEnds() throws Exception {

        int cmp = ByFeaturePositionComparator.compareAnnotByNullablePosition(2, 1012, 2, 1042);

        Assert.assertTrue(cmp > 0);
    }
}