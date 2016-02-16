package org.nextprot.api.core.utils.dbxref;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;

import static org.nextprot.api.core.utils.dbxref.DbXrefURLResolverTest.createDbXref;

public class GenevisibleXrefURLResolverTest {

    private DbXrefURLBaseResolver resolver;

    @Before
    public void setup() {

        resolver = new GenevisibleXrefURLResolver();
    }

    // entry/NX_P51610/xref.json
    @Test
    public void testResolveGenevisible() throws Exception {

        DbXref xref = createDbXref("P51610", "Genevisible", "http://genevisible.com/tissues/%s2/UniProt/%s1");

        Assert.assertEquals("http://genevisible.com/tissues/HS/UniProt/P51610", resolver.resolve(xref));
    }

    @Test (expected = UnresolvedXrefURLException.class)
    public void testResolveGenevisibleBadTemplate() throws Exception {

        DbXref xref = createDbXref("P51610", "Genevisible", "whatever");

        Assert.assertEquals("http://genevisible.com/tissues/HS/UniProt/P51610", resolver.resolve(xref));
    }
}