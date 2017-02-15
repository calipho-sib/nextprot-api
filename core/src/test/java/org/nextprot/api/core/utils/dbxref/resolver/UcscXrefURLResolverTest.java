package org.nextprot.api.core.utils.dbxref.resolver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.utils.dbxref.DbXrefURLBaseResolver;
import org.nextprot.api.core.utils.dbxref.DbXrefURLResolverTest;

public class UcscXrefURLResolverTest {

    private DbXrefURLBaseResolver resolver;

    @Before
    public void setup() {

        resolver = new UcscXrefURLResolver();
    }

    @Test
    public void testResolveUCSC() throws Exception {

        DbXref xref = DbXrefURLResolverTest.createDbXref("uc004fjp.3", "UCSC", "http://genome.ucsc.edu/cgi-bin/hgGene?hgg_gene=%s1&org=%s2");

        Assert.assertEquals("http://genome.ucsc.edu/cgi-bin/hgGene?hgg_gene=uc004fjp.3&org=human", resolver.resolve(xref));
    }

    @Test (expected = UnresolvedXrefURLException.class)
    public void testResolveUCSCMissingPlaceHolder1() throws Exception {

        DbXref xref = DbXrefURLResolverTest.createDbXref("uc004fjp.3", "UCSC", "http://genome.ucsc.edu/cgi-bin/hgGene?hgg_gene=%s1");

        resolver.resolve(xref);
    }

    @Test (expected = UnresolvedXrefURLException.class)
    public void testResolveUCSCMissingPlaceHolder2() throws Exception {

        DbXref xref = DbXrefURLResolverTest.createDbXref("uc004fjp.3", "UCSC", "http://genome.ucsc.edu/cgi-bin/hgGene?org=%s2");

        resolver.resolve(xref);
    }
}