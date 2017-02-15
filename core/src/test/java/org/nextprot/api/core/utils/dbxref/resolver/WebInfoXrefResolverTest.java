package org.nextprot.api.core.utils.dbxref.resolver;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;

public class WebInfoXrefResolverTest {

    @Test
    public void testResolve() throws Exception {

        DefaultDbXrefURLResolver resolver = new WebInfoXrefURLResolver();

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("babebibobu", "WEBINFO", "");

        Assert.assertEquals("babebibobu", resolver.resolve(xref));
    }
}