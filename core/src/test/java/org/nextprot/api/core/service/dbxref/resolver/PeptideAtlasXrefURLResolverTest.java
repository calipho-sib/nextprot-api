package org.nextprot.api.core.service.dbxref.resolver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;

public class PeptideAtlasXrefURLResolverTest {

    private DefaultDbXrefURLResolver resolver;

    @Before
    public void setup() {

        resolver = new PeptideAtlasXrefURLResolver();
    }

    // entry/NX_P01308/xref.json
    @Test
    public void testResolvePeptideAtlasPap() throws Exception {

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("PAp00001490", "PeptideAtlas", "whatever");

        Assert.assertEquals("https://db.systemsbiology.net/sbeams/cgi/PeptideAtlas/GetPeptide?searchWithinThis=Peptide+Name&searchForThis=PAp00001490;organism_name=Human", resolver.resolve(xref));
    }

    // entry/NX_P01308/xref.json
    @Test
    public void testResolvePeptideAtlasNoPap() throws Exception {

        DbXref xref = DbXrefURLResolverDelegateTest.createDbXref("P01308", "PeptideAtlas", "whatever");

        Assert.assertEquals("https://db.systemsbiology.net/sbeams/cgi/PeptideAtlas/GetProtein?protein_name=P01308;organism_name=Human;action=GO", resolver.resolve(xref));
    }
}