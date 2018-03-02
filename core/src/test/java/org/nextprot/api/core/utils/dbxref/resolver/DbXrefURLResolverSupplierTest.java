package org.nextprot.api.core.utils.dbxref.resolver;

import org.junit.Assert;
import org.junit.Test;

public class DbXrefURLResolverSupplierTest {

    @Test
    public void testValueOfDbName() {

        for (DbXrefURLResolverSupplier dbXrefURLResolverSupplier : DbXrefURLResolverSupplier.values()) {

            Assert.assertTrue(DbXrefURLResolverSupplier.fromDbName(dbXrefURLResolverSupplier.getXrefDatabase().getName()) != null);
        }
        Assert.assertNull(DbXrefURLResolverSupplier.fromDbName("koekdkeo"));
    }

    @Test
    public void testOptionalValueOfDbName() {

        for (DbXrefURLResolverSupplier dbXrefURLResolverSupplier : DbXrefURLResolverSupplier.values()) {

            Assert.assertTrue(DbXrefURLResolverSupplier.fromDbName(dbXrefURLResolverSupplier.getXrefDatabase().getName()).isPresent());
        }
        Assert.assertTrue(!DbXrefURLResolverSupplier.fromDbName("dedoekode").isPresent());
    }
}