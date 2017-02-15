package org.nextprot.api.core.utils.dbxref.resolver;

import org.junit.Assert;
import org.junit.Test;

public class XRefDatabaseTest {

    @Test
    public void testValueOfDbName() {

        for (XRefDatabase xRefDatabase : XRefDatabase.values()) {

            Assert.assertTrue(XRefDatabase.valueOfDbName(xRefDatabase.getName()) != null);
        }
        Assert.assertNull(XRefDatabase.valueOfDbName("koekdkeo"));
    }

    @Test
    public void testOptionalValueOfDbName() {

        for (XRefDatabase xRefDatabase : XRefDatabase.values()) {

            Assert.assertTrue(XRefDatabase.optionalValueOfDbName(xRefDatabase.getName()).isPresent());
        }
        Assert.assertTrue(!XRefDatabase.optionalValueOfDbName("dedoekode").isPresent());
    }
}