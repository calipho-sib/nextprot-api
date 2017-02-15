package org.nextprot.api.core.utils.dbxref.resolver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;

public class TkgXrefURLResolverTest {

    private DefaultDbXrefURLResolver resolver;

    @Before
    public void setup() {

        resolver = new TkgXrefURLResolver();
    }

    @Test
    public void testResolveTKG() throws Exception {

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("0377", "TKG", "http://www2.idac.tohoku.ac.jp/dep/ccr/TKGdate/TKGvo10%n/%s.html");

        Assert.assertEquals("http://www2.idac.tohoku.ac.jp/dep/ccr/TKGdate/TKGvo103/0377.html", resolver.resolve(xref));
    }

    @Test(expected = UnresolvedXrefURLException.class)
    public void testResolveTKGMissingPlaceHolderN() throws Exception {

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("0377", "TKG", "http://www2.idac.tohoku.ac.jp/dep/ccr/TKGdate/TKGvo10n/%s.html");

        Assert.assertEquals("http://www2.idac.tohoku.ac.jp/dep/ccr/TKGdate/TKGvo103/0377.html", resolver.resolve(xref));
    }
}