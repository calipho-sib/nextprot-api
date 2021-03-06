package org.nextprot.api.core.service.dbxref.resolver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;

public class JcrbXrefURLResolverTest {

    private DefaultDbXrefURLResolver resolver;

    @Before
    public void setup() {

        resolver = new JcrbXrefURLResolver();
    }

    @Test
    public void testResolveIFO() throws Exception {

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("IFO1234", "IFO", "whatever/%s");
        Assert.assertEquals("http://cellbank.nibio.go.jp/~cellbank/cgi-bin/search_res_det.cgi?RNO=ifo1234", resolver.resolve(xref));
    }

    @Test
    public void testResolveJCRB() throws Exception {

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("JCRB1234", "JCRB", "whatever/%s");
        Assert.assertEquals("http://cellbank.nibio.go.jp/~cellbank/en/search_res_list.cgi?KEYWOD=jcrb1234", resolver.resolve(xref));
    }
}