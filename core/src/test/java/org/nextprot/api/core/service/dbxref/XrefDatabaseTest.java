package org.nextprot.api.core.service.dbxref;

import org.junit.Assert;
import org.junit.Test;

public class XrefDatabaseTest {

    @Test
    public void testEmptyParamConstr() {

        Assert.assertEquals("BRENDA", XrefDatabase.BRENDA.getName());
    }

    @Test
    public void testOneParamConstr() {

        Assert.assertEquals("ChiTaRS", XrefDatabase.CHITARS.getName());
    }

    @Test
    public void testHasKeyViaEnumConstantDictionary() {

        Assert.assertTrue(XrefDatabase.CHITARS.getEnumConstantDictionary().haskey("ChiTaRS"));
    }

    @Test
    public void testValueOfKeyViaEnumConstantDictionary() {

        Assert.assertEquals(XrefDatabase.CHITARS, XrefDatabase.CHITARS.getEnumConstantDictionary().valueOfKey("ChiTaRS"));
    }

    @Test
    public void testStaticValueOfName() {

        Assert.assertEquals(XrefDatabase.CHITARS, XrefDatabase.valueOfName("ChiTaRS"));
    }
}