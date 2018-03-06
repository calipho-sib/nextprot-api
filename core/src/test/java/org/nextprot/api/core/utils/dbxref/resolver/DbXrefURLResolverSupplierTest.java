package org.nextprot.api.core.utils.dbxref.resolver;

import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class DbXrefURLResolverSupplierTest {

    @Test
    public void testValueOfDbName() {

        for (DbXrefURLResolverSupplier dbXrefURLResolverSupplier : DbXrefURLResolverSupplier.values()) {

            Assert.assertTrue(DbXrefURLResolverSupplier.fromDbName(dbXrefURLResolverSupplier.getXrefDatabase().getName()) != null);
        }
        Assert.assertEquals(Optional.empty(), DbXrefURLResolverSupplier.fromDbName("koekdkeo"));
    }

    @Test
    public void testOptionalValueOfDbName() {

        for (DbXrefURLResolverSupplier dbXrefURLResolverSupplier : DbXrefURLResolverSupplier.values()) {

            Assert.assertTrue(DbXrefURLResolverSupplier.fromDbName(dbXrefURLResolverSupplier.getXrefDatabase().getName()).isPresent());
        }
        Assert.assertTrue(!DbXrefURLResolverSupplier.fromDbName("dedoekode").isPresent());
    }
}