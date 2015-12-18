package org.nextprot.api.core.utils.dbxref;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;

import static org.nextprot.api.core.utils.dbxref.DbXrefURLResolverTest.createDbXref;

public class ConstantLinkXrefURLResolverTest {

    // entry/NX_Q9BXA6/xref.json
    @Test
    public void testResolveGermOnline() throws Exception {

        DbXrefURLBaseResolver resolver = new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.GERMONLINE);

        DbXref xref = createDbXref("ENSG00000178093", "GermOnline", "whatever");

        Assert.assertEquals("http://www.germonline.org/Homo_sapiens/geneview?gene=ENSG00000178093", resolver.resolve(xref));
    }

    @Test
    public void testResolveGenevestigator() throws Exception {

        DbXrefURLBaseResolver resolver = new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.GENEVESTIGATOR);

        DbXref xref = createDbXref("P01308", "Genevestigator", "whatever");

        Assert.assertEquals("http://genevisible.com/tissues/HS/UniProt/P01308", resolver.resolve(xref));
    }

    @Test
    public void testResolveProsite() throws Exception {

        DbXrefURLBaseResolver resolver = new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.PROSITE);

        DbXref xref = createDbXref("PS50853", "PROSITE", "whatever");

        Assert.assertEquals("http://prosite.expasy.org/cgi-bin/prosite/prosite-search-ac?PS50853", resolver.resolve(xref));
    }

    // entry/NX_P01308/xref.json
    @Test
    public void testResolvePDB() throws Exception {

        DbXrefURLBaseResolver resolver = new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.PDB);

        DbXref xref = createDbXref("1A7F", "PDB", "whatever");

        Assert.assertEquals("http://www.pdb.org/pdb/explore/explore.do?pdbId=1A7F", resolver.resolve(xref));
    }
}