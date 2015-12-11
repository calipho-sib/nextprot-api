package org.nextprot.api.core.utils.dbxref;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.DbXrefTest;

public class WebInfoXrefResolverTest {

    @Test
    public void testResolve() throws Exception {

        DbXrefURLBaseResolver resolver = new WebInfoXrefURLResolver();

        DbXref xref = DbXrefTest.createDbXref("babebibobu", "WEBINFO", "");

        Assert.assertEquals("babebibobu", resolver.resolve(xref));
    }
}