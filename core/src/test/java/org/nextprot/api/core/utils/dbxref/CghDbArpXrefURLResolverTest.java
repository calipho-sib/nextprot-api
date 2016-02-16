package org.nextprot.api.core.utils.dbxref;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;

import static org.nextprot.api.core.utils.dbxref.DbXrefURLResolverTest.createDbXref;

public class CghDbArpXrefURLResolverTest {

    private DbXrefURLBaseResolver resolver;

    @Before
    public void setup() {

        resolver = new CghDbArpXrefURLResolver();
    }

    @Test
    public void testResolveCGH_DB() throws Exception {

        DbXref xref = createDbXref("9029-4", "CGH-DB", "http://www.cghtmd.jp/CGHDatabase/mapViewer?hid=%s&aid=%t&lang=en");

        Assert.assertEquals("http://www.cghtmd.jp/CGHDatabase/mapViewer?hid=9029&aid=4&lang=en", resolver.resolve(xref));
    }

    @Test (expected = UnresolvedXrefURLException.class)
    public void testResolveCGH_DBMissingDashInAccessionNumber() throws Exception {

        DbXref xref = createDbXref("90294", "CGH-DB", "http://www.cghtmd.jp/CGHDatabase/mapViewer?hid=%s&aid=%t&lang=en");

        resolver.resolve(xref);
    }

    @Test (expected = UnresolvedXrefURLException.class)
    public void testResolveCGH_DBMissingPlaceHolder() throws Exception {

        DbXref xref = createDbXref("9029-4", "CGH-DB", "http://www.cghtmd.jp/CGHDatabase/mapViewer?hid=%s&aid=t&lang=en");

        resolver.resolve(xref);
    }
}