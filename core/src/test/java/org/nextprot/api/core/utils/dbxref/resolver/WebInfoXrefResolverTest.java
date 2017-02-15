package org.nextprot.api.core.utils.dbxref.resolver;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.utils.dbxref.DbXrefURLBaseResolver;
import org.nextprot.api.core.utils.dbxref.DbXrefURLResolverTest;

public class WebInfoXrefResolverTest {

    @Test
    public void testResolve() throws Exception {

        DbXrefURLBaseResolver resolver = new WebInfoXrefURLResolver();

        DbXref xref = DbXrefURLResolverTest.createDbXref("babebibobu", "WEBINFO", "");

        Assert.assertEquals("babebibobu", resolver.resolve(xref));
    }
}