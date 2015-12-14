package org.nextprot.api.core.utils.dbxref;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;

import java.util.Collections;

public class DbXrefURLResolverTest {

    private DbXrefURLResolver resolver;

    @Before
    public void setup() {

        resolver = DbXrefURLResolver.getInstance();
    }

    @Test
    public void testResolveWEBINFO() throws Exception {

        DbXref xref = createDbXref("babebibobu", "WEBINFO", "");

        Assert.assertEquals("babebibobu", resolver.resolveUrl(xref));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testUnknownDbNameAndEmptyURL() throws Exception {

        DbXref xref = createDbXref("babebibobu", "unknownDb", "");

        Assert.assertEquals("", resolver.resolveUrl(xref));
    }

    @Test
    public void testResolveEmbl() throws Exception {

        DbXref xref = createDbXref("AF009225", "EMBL", "http://www.ebi.ac.uk/ena/data/view/%s");

        Assert.assertEquals("http://www.ebi.ac.uk/ena/data/view/AF009225", resolver.resolveUrl(xref));
    }

    @Test
    public void testResolveEmblWithDotAccession() throws Exception {

        DbXref xref = createDbXref("CAH72401.1", "EMBL", "whatever");

        Assert.assertEquals("http://www.ebi.ac.uk/cgi-bin/dbfetch?db=emblcds&id=CAH72401", resolver.resolveUrl(xref));
    }

    // entry/NX_Q9BXA6/xref.json
    @Test
    public void testResolveEnsemblENSG() throws Exception {

        DbXref xref = createDbXref("ENSG00000178093", "Ensembl", "whatever");

        Assert.assertEquals("http://www.ensembl.org/Homo_sapiens/Gene/Summary?db=core;g=ENSG00000178093", resolver.resolveUrl(xref));
    }

    // entry/NX_Q9BXA6/xref.json
    @Test
    public void testResolveEnsemblENSP() throws Exception {

        DbXref xref = createDbXref("ENSP00000466056", "Ensembl", "whatever");

        Assert.assertEquals("http://www.ensembl.org/Homo_sapiens/Transcript/ProteinSummary?db=core;p=ENSP00000466056", resolver.resolveUrl(xref));
    }

    // entry/NX_Q9BXA6/xref.json
    @Test
    public void testResolveEnsemblENST() throws Exception {

        DbXref xref = createDbXref("ENST00000587522", "Ensembl", "whatever");

        Assert.assertEquals("http://www.ensembl.org/Homo_sapiens/Transcript/Summary?db=core;t=ENST00000587522", resolver.resolveUrl(xref));
    }

    @Test (expected = UnresolvedXrefURLException.class)
    public void testResolveEnsemblBadPrimaryId() throws Exception {

        DbXref xref = createDbXref("EST00000587522", "Ensembl", "whatever");

        Assert.assertNull(resolver.resolveUrl(xref));
    }

    // entry/NX_Q9BXA6/xref.json
    @Test
    public void testResolveCosmicCOSM() throws Exception {

        DbXref xref = createDbXref("COSM1172604", "Cosmic", "whatever");

        Assert.assertEquals("http://cancer.sanger.ac.uk/cosmic/mutation/overview?id=1172604", resolver.resolveUrl(xref));
    }

    // entry/NX_?????/xref.json
    @Test
    public void testResolveCosmicCOSS() throws Exception {

        DbXref xref = createDbXref("COSS1172604", "Cosmic", "whatever");

        Assert.assertEquals("http://cancer.sanger.ac.uk/cosmic/sample/overview?id=1172604", resolver.resolveUrl(xref));
    }

    @Test
    public void testResolveCosmicBadPrimaryId() throws Exception {

        DbXref xref = createDbXref("HCFC1", "Cosmic", "whatever");

        Assert.assertEquals("http://cancer.sanger.ac.uk/cosmic/gene/overview?ln=HCFC1", resolver.resolveUrl(xref));
    }

    @Test
    public void testResolveClinvar() throws Exception {

        Assert.fail("not yet tested");

        DbXref xref = createDbXref("HCFC1", "Clinvar", "url.whatever");

        Assert.assertEquals("http://www.ncbi.nlm.nih.gov/clinvar/", resolver.resolveUrl(xref));
    }

    // entry/NX_P51610/xref.json
    @Test
    public void testResolvePIR() throws Exception {

        DbXref xref = createDbXref("babebibobu", "PIR", "http://pir.georgetown.edu/cgi-bin/nbrfget?uid=%s");

        xref.setProperties(Collections.singletonList(createDbXrefProperty("entry name", "A40718")));

        Assert.assertEquals("http://pir.georgetown.edu/cgi-bin/nbrfget?uid=A40718", resolver.resolveUrl(xref));
    }

    // entry/NX_P51610/xref.json
    @Test
    public void testResolvePIRWithoutURLProtocol() throws Exception {

        DbXref xref = createDbXref("babebibobu", "PIR", "pir.georgetown.edu/cgi-bin/nbrfget?uid=%s");

        xref.setProperties(Collections.singletonList(createDbXrefProperty("entry name", "A40718")));

        Assert.assertEquals("http://pir.georgetown.edu/cgi-bin/nbrfget?uid=A40718", resolver.resolveUrl(xref));
    }

    @Test (expected = UnresolvedXrefURLException.class)
    public void testResolvePIRShouldThrowsException() throws Exception {

        DbXref xref = createDbXref("babebibobu", "PIR", "pir.georgetown.edu/cgi-bin/nbrfget?uid=%s");

        Assert.assertEquals("http://pir.georgetown.edu/cgi-bin/nbrfget?uid=A40718", resolver.resolveUrl(xref));
    }

    public static DbXref createDbXref(String accession, String dbName, String linkURL) {

        DbXref xref = new DbXref();

        xref.setAccession(accession);
        xref.setDatabaseName(dbName);
        xref.setLinkUrl(linkURL);

        return xref;
    }

    public static DbXref.DbXrefProperty createDbXrefProperty(String name, String value) {

        DbXref.DbXrefProperty prop = new DbXref.DbXrefProperty();

        prop.setName(name);
        prop.setValue(value);

        return prop;
    }
}