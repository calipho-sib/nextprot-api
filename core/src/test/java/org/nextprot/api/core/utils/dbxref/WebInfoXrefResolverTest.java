package org.nextprot.api.core.utils.dbxref;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;

import static org.nextprot.api.core.utils.dbxref.DbXrefURLResolverTest.createDbXref;

public class WebInfoXrefResolverTest {

    @Test
    public void testResolve() throws Exception {

        DbXrefURLBaseResolver resolver = new WebInfoXrefURLResolver();

        DbXref xref = createDbXref("babebibobu", "WEBINFO", "");

        Assert.assertEquals("babebibobu", resolver.resolve(xref));
    }
}