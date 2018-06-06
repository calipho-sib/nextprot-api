package org.nextprot.api.core.service.dbxref.resolver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;

import java.util.Collections;

public class HsspXrefURLResolverTest {

    private DefaultDbXrefURLResolver resolver;

    @Before
    public void setup() {

        resolver = new HsspXrefURLResolver();
    }

    @Test
    public void testResolveHSSP() throws Exception {

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("PS50853", "HSSP", "%s");

        Assert.assertEquals("http://ps50853", resolver.resolve(xref));
    }

    @Test
    public void testResolveHSSPWithPDB() throws Exception {

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("PS50853", "HSSP", "%s");
        xref.setProperties(Collections.singletonList(DbXrefURLResolverDelegateTest.createDbXrefProperty("PDB accession", "1A7F")));

        Assert.assertEquals("http://1a7f", resolver.resolve(xref));
    }
}