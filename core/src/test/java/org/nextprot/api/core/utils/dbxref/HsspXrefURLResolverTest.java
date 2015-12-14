package org.nextprot.api.core.utils.dbxref;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;

import static org.nextprot.api.core.utils.dbxref.DbXrefURLResolverTest.createDbXref;

public class HsspXrefURLResolverTest {

    private DbXrefURLBaseResolver resolver;

    @Before
    public void setup() {

        resolver = new HsspXrefURLResolver();
    }

    @Test
    public void testResolveHSSP() throws Exception {

        DbXref xref = createDbXref("PS50853", "HSSP", "whatever");

        Assert.assertEquals("http://prosite.expasy.org/cgi-bin/prosite/prosite-search-ac?PS50853", resolver.resolve(xref));
    }

    @Test
    public void testResolveHSSP2() throws Exception {

        Assert.fail();

        DbXref xref = createDbXref("PS50853", "HSSP", "whatever");

        Assert.assertEquals("http://prosite.expasy.org/cgi-bin/prosite/prosite-search-ac?PS50853", resolver.resolve(xref));
    }
}