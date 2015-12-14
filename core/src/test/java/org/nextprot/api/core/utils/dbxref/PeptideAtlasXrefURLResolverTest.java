package org.nextprot.api.core.utils.dbxref;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;

import static org.nextprot.api.core.utils.dbxref.DbXrefURLResolverTest.createDbXref;

public class PeptideAtlasXrefURLResolverTest {

    private DbXrefURLBaseResolver resolver;

    @Before
    public void setup() {

        resolver = new PeptideAtlasXrefURLResolver();
    }

    // entry/NX_P01308/xref.json
    @Test
    public void testResolvePeptideAtlasPap() throws Exception {

        DbXref xref = createDbXref("PAp00001490", "PeptideAtlas", "whatever");

        Assert.assertEquals("https://db.systemsbiology.net/sbeams/cgi/PeptideAtlas/GetPeptide?searchWithinThis=Peptide+Name&searchForThis=PAp00001490;organism_name=Human", resolver.resolve(xref));
    }

    // entry/NX_P01308/xref.json
    @Test
    public void testResolvePeptideAtlasNoPap() throws Exception {

        DbXref xref = createDbXref("P01308", "PeptideAtlas", "whatever");

        Assert.assertEquals("https://db.systemsbiology.net/sbeams/cgi/PeptideAtlas/GetProtein?protein_name=P01308;organism_name=Human;action=GO", resolver.resolve(xref));
    }
}