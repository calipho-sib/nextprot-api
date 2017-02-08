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


    @Test
    public void testResolveGenevisibleBadTemplate() throws Exception {

        DbXref xref = createDbXref("P51611", "Genevisible", "whatever");

        Assert.assertEquals("https://genevisible.com/tissues/HS/UniProt/P51611", resolver.resolve(xref));
    }

    @Test
    public void testResolve2() throws Exception {

        DbXref xref = createDbXref("P51612", "Genevisible", "whatever");
        String url = DbXrefURLResolver.getInstance().resolve(xref);
        Assert.assertEquals("https://genevisible.com/tissues/HS/UniProt/P51612", url);
    }    
    
}