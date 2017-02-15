package org.nextprot.api.core.utils.dbxref.resolver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.utils.dbxref.DbXrefURLBaseResolver;
import org.nextprot.api.core.utils.dbxref.DbXrefURLResolverTest;

public class OboLibraryXrefURLResolverTest {

    private DbXrefURLBaseResolver resolver;

    @Before
    public void setup() {

        resolver = new OboLibraryXrefURLResolver();
    }

    @Test
    public void testResolvePRO() throws Exception {

        DbXref xref = DbXrefURLResolverTest.createDbXref("PR:000028527", "PRO", "http://purl.obolibrary.org/obo/PR_%u");

        Assert.assertEquals("http://purl.obolibrary.org/obo/PR_000028527", resolver.resolve(xref));
    }

    @Test
    public void testResolveCLO() throws Exception {

        DbXref xref = DbXrefURLResolverTest.createDbXref("CLO:0000031", "CLO", "purl.obolibrary.org/obo/%s");
        Assert.assertEquals("http://purl.obolibrary.org/obo/CLO_0000031", resolver.resolve(xref));
    }

    @Test
    public void testResolveFMA() throws Exception {

        DbXref xref = DbXrefURLResolverTest.createDbXref("FMA:62955", "FMA", "http://purl.obolibrary.org/obo/%s");
        Assert.assertEquals("http://purl.obolibrary.org/obo/FMA_62955", resolver.resolve(xref));
    }
}