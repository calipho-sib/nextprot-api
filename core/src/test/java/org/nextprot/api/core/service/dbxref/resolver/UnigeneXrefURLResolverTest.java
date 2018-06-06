package org.nextprot.api.core.service.dbxref.resolver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;

public class UnigeneXrefURLResolverTest {

    private DefaultDbXrefURLResolver resolver;

    @Before
    public void setup() {

        resolver = new UnigeneXrefURLResolver();
    }

    @Test
    public void testResolveUniGene() throws Exception {

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("Hs.83634", "UniGene", "http://www.ncbi.nlm.nih.gov/UniGene/clust.cgi?ORG=%d&CID=%s");

        Assert.assertEquals("http://www.ncbi.nlm.nih.gov/UniGene/clust.cgi?ORG=Hs&CID=83634", resolver.resolve(xref));
    }
}