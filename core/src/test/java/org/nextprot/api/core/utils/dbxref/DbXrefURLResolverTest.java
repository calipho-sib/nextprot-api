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

    @Test (expected = UnresolvedXrefURLException.class)
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

        Assert.fail("not yet tested");

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

    // entry/NX_P01308/xref.json
    @Test
    public void testResolveSRMAtlas() throws Exception {

        DbXref xref = createDbXref("PAp00968082", "SRMAtlas", "https://db.systemsbiology.net/sbeams/cgi/PeptideAtlas/GetTransitions?organism_name=Human;default_search=1;peptide_sequence_constraint=%s;apply_action=QUERY");
        xref.setProperties(Collections.singletonList(createDbXrefProperty("sequence", "GFFYTPK")));

        Assert.assertEquals("https://db.systemsbiology.net/sbeams/cgi/PeptideAtlas/GetTransitions?organism_name=Human;default_search=1;peptide_sequence_constraint=GFFYTPK;apply_action=QUERY",resolver.resolve(xref));
    }

    @Test
    public void testResolveTKG() throws Exception {

        DbXref xref = createDbXref("0377", "TKG", "http://www2.idac.tohoku.ac.jp/dep/ccr/TKGdate/TKGvo10%n/%s.html");

        Assert.assertEquals("http://www2.idac.tohoku.ac.jp/dep/ccr/TKGdate/TKGvo103/0377.html", resolver.resolve(xref));
    }

    @Test(expected = UnresolvedXrefURLException.class)
    public void testResolveTKGMissingPlaceHolderN() throws Exception {

        DbXref xref = createDbXref("0377", "TKG", "http://www2.idac.tohoku.ac.jp/dep/ccr/TKGdate/TKGvo10n/%s.html");

        resolver.resolve(xref);
    }

    @Test
    public void testResolveNIH_ARP() throws Exception {

        DbXref xref = createDbXref("11411-223", "NIH-ARP", "https://www.aidsreagent.org/reagentdetail.cfm?t=cell_lines&id=%s");

        Assert.assertEquals("https://www.aidsreagent.org/reagentdetail.cfm?t=cell_lines&id=223", resolver.resolve(xref));
    }

    @Test (expected = UnresolvedXrefURLException.class)
    public void testResolveNIH_ARPMissingDash() throws Exception {

        DbXref xref = createDbXref("11411_223", "NIH-ARP", "https://www.aidsreagent.org/reagentdetail.cfm?t=cell_lines&id=%s");

        resolver.resolve(xref);
    }

    @Test
    public void testResolveCGH_DB() throws Exception {

        DbXref xref = createDbXref("9029-4", "CGH-DB", "http://www.cghtmd.jp/CGHDatabase/mapViewer?hid=%s&aid=%t&lang=en");

        Assert.assertEquals("http://www.cghtmd.jp/CGHDatabase/mapViewer?hid=9029&aid=4&lang=en", resolver.resolve(xref));
    }

    @Test (expected = UnresolvedXrefURLException.class)
    public void testResolveCGH_DBMissingDashInAccessionNumber() throws Exception {

        DbXref xref = createDbXref("90294", "CGH-DB", "http://www.cghtmd.jp/CGHDatabase/mapViewer?hid=%s&aid=%t&lang=en");

        resolver.resolve(xref);
    }

    @Test (expected = UnresolvedXrefURLException.class)
    public void testResolveCGH_DBMissingPlaceHolder() throws Exception {

        DbXref xref = createDbXref("9029-4", "CGH-DB", "http://www.cghtmd.jp/CGHDatabase/mapViewer?hid=%s&aid=t&lang=en");

        resolver.resolve(xref);
    }

    @Test
    public void testResolveIFO() throws Exception {

        DbXref xref = createDbXref("IFO1234", "IFO", "whatever/%s");
        Assert.assertEquals("http://whatever/ifo1234", resolver.resolve(xref));
    }

    @Test
    public void testResolveJCRB() throws Exception {

        DbXref xref = createDbXref("JCRB1234", "JCRB", "whatever/%s");
        Assert.assertEquals("http://whatever/jcrb1234", resolver.resolve(xref));
    }

    @Test
    public void testResolvePRO() throws Exception {

        DbXref xref = createDbXref("PR:000028527", "PRO", "http://purl.obolibrary.org/obo/PR_%u");

        Assert.assertEquals("http://purl.obolibrary.org/obo/PR_000028527", resolver.resolve(xref));
    }

    @Test
    public void testResolveCLO() throws Exception {

        DbXref xref = createDbXref("CLO:0000031", "CLO", "purl.obolibrary.org/obo/%s");
        Assert.assertEquals("http://purl.obolibrary.org/obo/CLO_0000031", resolver.resolve(xref));
    }

    @Test
    public void testResolveFMA() throws Exception {

        DbXref xref = createDbXref("FMA:62955", "FMA", "http://purl.obolibrary.org/obo/%s");
        Assert.assertEquals("http://purl.obolibrary.org/obo/FMA_62955", resolver.resolve(xref));
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