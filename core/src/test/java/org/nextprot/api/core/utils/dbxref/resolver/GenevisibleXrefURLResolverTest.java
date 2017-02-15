package org.nextprot.api.core.utils.dbxref.resolver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;

public class GenevisibleXrefURLResolverTest {

    private DefaultDbXrefURLResolver resolver;

    @Before
    public void setup() {

        resolver = new GenevisibleXrefURLResolver();
    }


    @Test
    public void testResolveGenevisibleBadTemplate() throws Exception {

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("P51611", "Genevisible", "whatever");

        Assert.assertEquals("https://genevisible.com/tissues/HS/UniProt/P51611", resolver.resolve(xref));
    }

    @Test
    public void testResolve2() throws Exception {

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("P51612", "Genevisible", "whatever");
        String url = new DbXrefURLResolverDelegate().resolve(xref);
        Assert.assertEquals("https://genevisible.com/tissues/HS/UniProt/P51612", url);
    }    
    
}