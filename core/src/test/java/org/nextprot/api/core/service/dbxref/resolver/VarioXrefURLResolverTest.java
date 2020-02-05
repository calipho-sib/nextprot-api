package org.nextprot.api.core.service.dbxref.resolver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;

public class VarioXrefURLResolverTest {

    private DefaultDbXrefURLResolver resolver;

    @Before
    public void setup() {

        resolver = new VariOXrefURLResolver();
    }

    @Test
    public void testResolve() throws Exception {

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("VariO:0052", "VariO", "whatever");
        Assert.assertEquals("http://purl.obolibrary.org/obo/VariO_0052", resolver.resolve(xref));
    }


}