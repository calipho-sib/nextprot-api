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

        Assert.assertEquals("babebibobu", resolver.resolve(xref));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testUnknownDbNameAndEmptyURL() throws Exception {

        DbXref xref = createDbXref("babebibobu", "unknownDb", "");

        Assert.assertEquals("", resolver.resolve(xref));
    }

    @Test
    public void testResolveEmbl() throws Exception {

        DbXref xref = createDbXref("AF009225", "EMBL", "http://www.ebi.ac.uk/ena/data/view/%s");

        Assert.assertEquals("http://www.ebi.ac.uk/ena/data/view/AF009225", resolver.resolve(xref));
    }

    @Test
    public void testResolveEmblWithDotAccession() throws Exception {

        DbXref xref = createDbXref("CAH72401.1", "EMBL", "whatever");

        Assert.assertEquals("http://www.ebi.ac.uk/cgi-bin/dbfetch?db=emblcds&id=CAH72401", resolver.resolve(xref));
    }

    // entry/NX_Q9BXA6/xref.json
    @Test
    public void testResolveEnsemblENSG() throws Exception {

        DbXref xref = createDbXref("ENSG00000178093", "Ensembl", "whatever");

        Assert.assertEquals("http://www.ensembl.org/Homo_sapiens/Gene/Summary?db=core;g=ENSG00000178093", resolver.resolve(xref));
    }

    // entry/NX_Q9BXA6/xref.json
    @Test
    public void testResolveEnsemblENSP() throws Exception {

        DbXref xref = createDbXref("ENSP00000466056", "Ensembl", "whatever");

        Assert.assertEquals("http://www.ensembl.org/Homo_sapiens/Transcript/ProteinSummary?db=core;p=ENSP00000466056", resolver.resolve(xref));
    }

    // entry/NX_Q9BXA6/xref.json
    @Test
    public void testResolveEnsemblENST() throws Exception {

        DbXref xref = createDbXref("ENST00000587522", "Ensembl", "whatever");

        Assert.assertEquals("http://www.ensembl.org/Homo_sapiens/Transcript/Summary?db=core;t=ENST00000587522", resolver.resolve(xref));
    }

    @Test (expected = UnresolvedXrefURLException.class)
    public void testResolveEnsemblBadPrimaryId() throws Exception {

        DbXref xref = createDbXref("EST00000587522", "Ensembl", "whatever");

        Assert.assertNull(resolver.resolve(xref));
    }

    // entry/NX_Q9BXA6/xref.json
    @Test
    public void testResolveCosmicCOSM() throws Exception {

        DbXref xref = createDbXref("COSM1172604", "Cosmic", "whatever");

        Assert.assertEquals("http://cancer.sanger.ac.uk/cosmic/mutation/overview?id=1172604", resolver.resolve(xref));
    }

    // entry/NX_?????/xref.json
    @Test
    public void testResolveCosmicCOSS() throws Exception {

        DbXref xref = createDbXref("COSS1172604", "Cosmic", "whatever");

        Assert.assertEquals("http://cancer.sanger.ac.uk/cosmic/sample/overview?id=1172604", resolver.resolve(xref));
    }

    @Test
    public void testResolveCosmicBadPrimaryId() throws Exception {

        DbXref xref = createDbXref("HCFC1", "Cosmic", "whatever");

        Assert.assertEquals("http://cancer.sanger.ac.uk/cosmic/gene/overview?ln=HCFC1", resolver.resolve(xref));
    }

    @Test
    public void testResolveClinvar() throws Exception {

        Assert.fail("not yet tested");

        DbXref xref = createDbXref("HCFC1", "Clinvar", "url.whatever");

        Assert.assertEquals("http://www.ncbi.nlm.nih.gov/clinvar/", resolver.resolve(xref));
    }

    // entry/NX_P51610/xref.json
    @Test
    public void testResolvePIR() throws Exception {

        DbXref xref = createDbXref("babebibobu", "PIR", "http://pir.georgetown.edu/cgi-bin/nbrfget?uid=%s");

        xref.setProperties(Collections.singletonList(createDbXrefProperty("entry name", "A40718")));

        Assert.assertEquals("http://pir.georgetown.edu/cgi-bin/nbrfget?uid=A40718", resolver.resolve(xref));
    }

    // entry/NX_P51610/xref.json
    @Test
    public void testResolvePIRWithoutURLProtocol() throws Exception {

        DbXref xref = createDbXref("babebibobu", "PIR", "pir.georgetown.edu/cgi-bin/nbrfget?uid=%s");

        xref.setProperties(Collections.singletonList(createDbXrefProperty("entry name", "A40718")));

        Assert.assertEquals("http://pir.georgetown.edu/cgi-bin/nbrfget?uid=A40718", resolver.resolve(xref));
    }

    @Test (expected = UnresolvedXrefURLException.class)
    public void testResolvePIRShouldThrowsException() throws Exception {

        DbXref xref = createDbXref("babebibobu", "PIR", "pir.georgetown.edu/cgi-bin/nbrfget?uid=%s");

        Assert.assertEquals("http://pir.georgetown.edu/cgi-bin/nbrfget?uid=A40718", resolver.resolve(xref));
    }

    // entry/NX_Q9BXA6/xref.json
    @Test
    public void testResolveGermOnline() throws Exception {

        DbXref xref = createDbXref("ENSG00000178093", "GermOnline", "whatever");

        Assert.assertEquals("http://www.germonline.org/Homo_sapiens/geneview?gene=ENSG00000178093", resolver.resolve(xref));
    }

    @Test
    public void testResolveGenevestigator() throws Exception {

        Assert.fail("not yet tested");

        DbXref xref = createDbXref("", "Genevestigator", "whatever");

        Assert.assertEquals("", resolver.resolve(xref));
    }

    @Test
    public void testResolveProsite() throws Exception {

        DbXref xref = createDbXref("PS50853", "PROSITE", "whatever");

        Assert.assertEquals("http://prosite.expasy.org/cgi-bin/prosite/prosite-search-ac?PS50853", resolver.resolve(xref));
    }

    // entry/NX_P01308/xref.json
    @Test
    public void testResolvePDB() throws Exception {

        DbXref xref = createDbXref("1A7F", "PDB", "whatever");

        Assert.assertEquals("http://www.pdb.org/pdb/explore/explore.do?pdbId=1A7F", resolver.resolve(xref));
    }

    // entry/NX_Q9BXA6/xref.json
    @Test
    public void testResolveHPAGene() throws Exception {

        DbXref xref = createDbXref("ENSG00000254647", "HPA", "whatever");

        Assert.assertEquals("http://www.proteinatlas.org/ENSG00000254647", resolver.resolve(xref));
    }

    // entry/NX_P51610/xref.json
    @Test
    public void testResolveHPASubcellular() throws Exception {

        DbXref xref = createDbXref("ENSG00000254647/subcellular", "HPA", "whatever");

        Assert.assertEquals("http://www.proteinatlas.org/ENSG00000254647/subcellular", resolver.resolve(xref));
    }

    // entry/NX_P51610/xref.json
    @Test
    public void testResolveHPAAntibody() throws Exception {

        DbXref xref = createDbXref("HPA018312", "HPA", "whatever");

        Assert.assertEquals("http://www.proteinatlas.org/search/HPA018312", resolver.resolve(xref));
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

    @Test
    public void testResolveUniGene() throws Exception {

        DbXref xref = createDbXref("Hs.83634", "UniGene", "http://www.ncbi.nlm.nih.gov/UniGene/clust.cgi?ORG=%s1&CID=%s2");

        Assert.assertEquals("http://www.ncbi.nlm.nih.gov/UniGene/clust.cgi?ORG=Hs&CID=83634", resolver.resolve(xref));
    }

    @Test
    public void testResolveUCSC() throws Exception {

        DbXref xref = createDbXref("uc004fjp.3", "UCSC", "http://genome.ucsc.edu/cgi-bin/hgGene?hgg_gene=%s1&org=%s2");

        Assert.assertEquals("http://genome.ucsc.edu/cgi-bin/hgGene?hgg_gene=uc004fjp.3&org=human", resolver.resolve(xref));
    }

    @Test
    public void testResolveIntAct() throws Exception {

        DbXref xref = createDbXref("EBI-1644164,EBI-396176", "IntAct", "whatever");

        Assert.assertEquals("http://www.ebi.ac.uk/intact/search/do/search?binary=EBI-1644164,EBI-396176", resolver.resolve(xref));
    }

    @Test
    public void testResolveIntActNoEBIId() throws Exception {

        DbXref xref = createDbXref("P51610", "IntAct", "http://www.ebi.ac.uk/intact/pages/interactions/interactions.xhtml?query=%s");

        Assert.assertEquals("http://www.ebi.ac.uk/intact/pages/interactions/interactions.xhtml?query=P51610", resolver.resolve(xref));
    }

    @Test
    public void testResolveHSSP() throws Exception {

        Assert.fail();

        DbXref xref = createDbXref("PS50853", "HSSP", "whatever");

        Assert.assertEquals("http://prosite.expasy.org/cgi-bin/prosite/prosite-search-ac?PS50853", resolver.resolve(xref));
    }

    // entry/P51610/xref.json
    @Test
    public void testResolveBgeeENSG() throws Exception {

        DbXref xref = createDbXref("ENSG00000172534", "Bgee", "http://bgee.unil.ch/bgee/bgee?uniprot_id=%s");

        Assert.assertEquals("http://bgee.unil.ch/bgee/bgee?page=expression&action=data&ENSG00000172534", resolver.resolve(xref));
    }

    // entry/P51610/xref.json
    @Test
    public void testResolveBgeeNoENSG() throws Exception {

        DbXref xref = createDbXref("P51610", "Bgee", "http://bgee.unil.ch/bgee/bgee?uniprot_id=%s");

        Assert.assertEquals("http://bgee.unil.ch/bgee/bgee?uniprot_id=P51610", resolver.resolve(xref));
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