package org.nextprot.api.core.utils.dbxref.resolver;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;

public class ConstantLinkXrefURLResolverTest {

    // entry/NX_Q9BXA6/xref.json
    @Test
    public void testResolveGermOnline() throws Exception {

        DefaultDbXrefURLResolver resolver = new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.GERMONLINE);

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("ENSG00000178093", "GermOnline", "whatever");

        Assert.assertEquals("http://www.germonline.org/Homo_sapiens/geneview?gene=ENSG00000178093", resolver.resolve(xref));
    }

    @Test
    public void testResolveGenevestigator() throws Exception {

        DefaultDbXrefURLResolver resolver = new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.GENEVESTIGATOR);

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("P01308", "Genevestigator", "whatever");

        Assert.assertEquals("http://genevisible.com/tissues/HS/UniProt/P01308", resolver.resolve(xref));
    }

    @Test
    public void testResolveProsite() throws Exception {

        DefaultDbXrefURLResolver resolver = new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.PROSITE);

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("PS50853", "PROSITE", "whatever");

        Assert.assertEquals("http://prosite.expasy.org/cgi-bin/prosite/prosite-search-ac?PS50853", resolver.resolve(xref));
    }

    // entry/NX_P01308/xref.json
    @Test
    public void testResolvePDB() throws Exception {

        DefaultDbXrefURLResolver resolver = new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.PDB);

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("1A7F", "PDB", "whatever");

        Assert.assertEquals("http://www.pdb.org/pdb/explore/explore.do?pdbId=1A7F", resolver.resolve(xref));
    }
    
    @Test
    public void testResolveUCSC() throws Exception {

        DefaultDbXrefURLResolver resolver = new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.UCSC);

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXrefWithEntry("NX_ENTRY_AC", "UCSC_AC", "UCSC", "/some/link/to/override/");

        Assert.assertEquals("https://genome.ucsc.edu/cgi-bin/hgLinkIn?resource=uniprot&id=ENTRY_AC", resolver.resolve(xref));
    }
}