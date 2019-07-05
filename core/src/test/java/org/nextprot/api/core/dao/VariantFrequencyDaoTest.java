package org.nextprot.api.core.dao;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.VariantFrequency;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;

public class VariantFrequencyDaoTest extends CoreUnitBaseTest {

    private VariantFrequencyDao variantFrequencyDao;

    @Test
    public void findVariantFrequencyTest() {
        VariantFrequency variantFrequency = variantFrequencyDao.findVariantFrequency("");
        Assert.assertEquals(variantFrequency.getAlleleCount(), 1);
    }
}
