package org.nextprot.api.core.utils.dbxref.resolver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;

public class NihArpXrefURLResolverTest {

    private DefaultDbXrefURLResolver resolver;

    @Before
    public void setup() {

        resolver = new NihArpXrefURLResolver();
    }

    @Test
    public void testResolveNIH_ARP() throws Exception {

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("11411-223", "NIH-ARP", "https://www.aidsreagent.org/reagentdetail.cfm?t=cell_lines&id=%s");

        Assert.assertEquals("https://www.aidsreagent.org/reagentdetail.cfm?t=cell_lines&id=223", resolver.resolve(xref));
    }

    @Test (expected = UnresolvedXrefURLException.class)
    public void testResolveNIH_ARPMissingDash() throws Exception {

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("11411_223", "NIH-ARP", "https://www.aidsreagent.org/reagentdetail.cfm?t=cell_lines&id=%s");

        Assert.assertEquals("https://www.aidsreagent.org/reagentdetail.cfm?t=cell_lines&id=223", resolver.resolve(xref));
    }
}