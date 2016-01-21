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
        Assert.assertEquals("", xref.getLinkUrl());
    }

    @Test (expected = UnresolvedXrefURLException.class)
    public void testUnknownDbNameAndEmptyURL() throws Exception {

        DbXref xref = createDbXref("babebibobu", "unknownDb", "");

        resolver.resolve(xref);
    }

    @Test
    public void testResolveEmbl() throws Exception {

        DbXref xref = createDbXref("AF009225", "EMBL", "http://www.ebi.ac.uk/ena/data/view/%s");

        Assert.assertEquals("http://www.ebi.ac.uk/ena/data/view/AF009225", resolver.resolve(xref));
        Assert.assertEquals("http://www.ebi.ac.uk/ena/data/view/%s", xref.getLinkUrl());
    }

    @Test
    public void testResolveEmblWithDotAccession() throws Exception {

        DbXref xref = createDbXref("CAH72401.1", "EMBL", "http://www.ebi.ac.uk/ena/data/view/%s");

        Assert.assertEquals("http://www.ebi.ac.uk/ena/data/view/CAH72401", resolver.resolve(xref));
        Assert.assertEquals("http://www.ebi.ac.uk/ena/data/view/%s", xref.getLinkUrl());
    }

    // entry/NX_Q9BXA6/xref.json
    @Test
    public void testResolveEnsemblENSG() throws Exception {

        DbXref xref = createDbXref("ENSG00000178093", "Ensembl", "whatever");

        Assert.assertEquals("http://www.ensembl.org/Homo_sapiens/Gene/Summary?db=core;g=ENSG00000178093", resolver.resolve(xref));
        Assert.assertEquals("http://www.ensembl.org/Homo_sapiens/Gene/Summary?db=core;g=%s", xref.getLinkUrl());
    }

    // entry/NX_Q9BXA6/xref.json
    @Test
    public void testResolveEnsemblENSP() throws Exception {

        DbXref xref = createDbXref("ENSP00000466056", "Ensembl", "whatever");

        Assert.assertEquals("http://www.ensembl.org/Homo_sapiens/Transcript/ProteinSummary?db=core;p=ENSP00000466056", resolver.resolve(xref));
        Assert.assertEquals("http://www.ensembl.org/Homo_sapiens/Transcript/ProteinSummary?db=core;p=%s", xref.getLinkUrl());
    }

    // entry/NX_Q9BXA6/xref.json
    @Test
    public void testResolveEnsemblENST() throws Exception {

        DbXref xref = createDbXref("ENST00000587522", "Ensembl", "whatever");

        Assert.assertEquals("http://www.ensembl.org/Homo_sapiens/Transcript/Summary?db=core;t=ENST00000587522", resolver.resolve(xref));
        Assert.assertEquals("http://www.ensembl.org/Homo_sapiens/Transcript/Summary?db=core;t=%s", xref.getLinkUrl());
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
        Assert.assertEquals("http://cancer.sanger.ac.uk/cosmic/mutation/overview?id=%s", xref.getLinkUrl());
    }

    // entry/NX_?????/xref.json
    @Test
    public void testResolveCosmicCOSS() throws Exception {

        DbXref xref = createDbXref("COSS1172604", "Cosmic", "whatever");

        Assert.assertEquals("http://cancer.sanger.ac.uk/cosmic/sample/overview?id=1172604", resolver.resolve(xref));
        Assert.assertEquals("http://cancer.sanger.ac.uk/cosmic/sample/overview?id=%s", xref.getLinkUrl());
    }

    @Test
    public void testResolveCosmicOthers() throws Exception {

        DbXref xref = createDbXref("HCFC1", "Cosmic", "whatever");

        Assert.assertEquals("http://cancer.sanger.ac.uk/cosmic/gene/overview?ln=HCFC1", resolver.resolve(xref));
        Assert.assertEquals("http://cancer.sanger.ac.uk/cosmic/gene/overview?ln=%s", xref.getLinkUrl());
    }

    @Test
    public void testResolveClinvar() throws Exception {

        DbXref xref = createDbXref("HCFC1", "Clinvar", "url.whatever");

        Assert.assertEquals("http://www.ncbi.nlm.nih.gov/clinvar/?term=HCFC1", resolver.resolve(xref));
        Assert.assertEquals("http://www.ncbi.nlm.nih.gov/clinvar/?term=%s", xref.getLinkUrl());
    }

    // entry/NX_P51610/xref.json
    @Test
    public void testResolvePIR() throws Exception {

        DbXref xref = createDbXref("babebibobu", "PIR", "http://pir.georgetown.edu/cgi-bin/nbrfget?uid=%s");

        xref.setProperties(Collections.singletonList(createDbXrefProperty("entry name", "A40718")));

        Assert.assertEquals("http://pir.georgetown.edu/cgi-bin/nbrfget?uid=A40718", resolver.resolve(xref));
        Assert.assertEquals("http://pir.georgetown.edu/cgi-bin/nbrfget?uid=%s", xref.getLinkUrl());
    }

    // entry/NX_P51610/xref.json
    @Test
    public void testResolvePIRWithoutURLProtocol() throws Exception {

        DbXref xref = createDbXref("babebibobu", "PIR", "pir.georgetown.edu/cgi-bin/nbrfget?uid=%s");

        xref.setProperties(Collections.singletonList(createDbXrefProperty("entry name", "A40718")));

        Assert.assertEquals("http://pir.georgetown.edu/cgi-bin/nbrfget?uid=A40718", resolver.resolve(xref));
        Assert.assertEquals("http://pir.georgetown.edu/cgi-bin/nbrfget?uid=%s", xref.getLinkUrl());
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
        Assert.assertEquals("http://www.germonline.org/Homo_sapiens/geneview?gene=%s", xref.getLinkUrl());
    }

    @Test
    public void testResolveGenevestigator() throws Exception {

        DbXref xref = createDbXref("P01308", "Genevestigator", "whatever");

        Assert.assertEquals("http://genevisible.com/tissues/HS/UniProt/P01308", resolver.resolve(xref));
        Assert.assertEquals("http://genevisible.com/tissues/HS/UniProt/%s", xref.getLinkUrl());
    }

    @Test
    public void testResolveProsite() throws Exception {

        DbXref xref = createDbXref("PS50853", "PROSITE", "whatever");

        Assert.assertEquals("http://prosite.expasy.org/cgi-bin/prosite/prosite-search-ac?PS50853", resolver.resolve(xref));
        Assert.assertEquals("http://prosite.expasy.org/cgi-bin/prosite/prosite-search-ac?%s", xref.getLinkUrl());
    }

    // entry/NX_P01308/xref.json
    @Test
    public void testResolvePDB() throws Exception {

        DbXref xref = createDbXref("1A7F", "PDB", "whatever");

        Assert.assertEquals("http://www.pdb.org/pdb/explore/explore.do?pdbId=1A7F", resolver.resolve(xref));
        Assert.assertEquals("http://www.pdb.org/pdb/explore/explore.do?pdbId=%s", xref.getLinkUrl());
    }

    // entry/NX_Q9BXA6/xref.json
    @Test
    public void testResolveHPAGene() throws Exception {

        DbXref xref = createDbXref("ENSG00000254647", "HPA", "whatever");

        Assert.assertEquals("http://www.proteinatlas.org/ENSG00000254647", resolver.resolve(xref));
        Assert.assertEquals("http://www.proteinatlas.org/%s", xref.getLinkUrl());
    }

    // entry/NX_P51610/xref.json
    @Test
    public void testResolveHPASubcellular() throws Exception {

        DbXref xref = createDbXref("ENSG00000254647/subcellular", "HPA", "whatever");

        Assert.assertEquals("http://www.proteinatlas.org/ENSG00000254647/subcellular", resolver.resolve(xref));
        Assert.assertEquals("http://www.proteinatlas.org/%s", xref.getLinkUrl());
    }

    // entry/NX_P51610/xref.json
    @Test
    public void testResolveHPAAntibody() throws Exception {

        DbXref xref = createDbXref("HPA018312", "HPA", "whatever");

        Assert.assertEquals("http://www.proteinatlas.org/search/HPA018312", resolver.resolve(xref));
        Assert.assertEquals("http://www.proteinatlas.org/search/%s", xref.getLinkUrl());
    }

    // entry/NX_P51610/xref.json
    @Test
    public void testResolveGenevisible() throws Exception {

        DbXref xref = createDbXref("P51610", "Genevisible", "http://genevisible.com/tissues/%s2/UniProt/%s1");

        Assert.assertEquals("http://genevisible.com/tissues/HS/UniProt/P51610", resolver.resolve(xref));
        Assert.assertEquals("http://genevisible.com/tissues/%s2/UniProt/%s1", xref.getLinkUrl());
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
        Assert.assertEquals("http://www.ncbi.nlm.nih.gov/UniGene/clust.cgi?ORG=%s1&CID=%s2", xref.getLinkUrl());
    }

    @Test
    public void testResolveUCSC() throws Exception {

        DbXref xref = createDbXref("uc004fjp.3", "UCSC", "http://genome.ucsc.edu/cgi-bin/hgGene?hgg_gene=%s1&org=%s2");

        Assert.assertEquals("http://genome.ucsc.edu/cgi-bin/hgGene?hgg_gene=uc004fjp.3&org=human", resolver.resolve(xref));
        Assert.assertEquals("http://genome.ucsc.edu/cgi-bin/hgGene?hgg_gene=%s1&org=%s2", xref.getLinkUrl());
    }

    @Test
    public void testResolveIntAct() throws Exception {

        DbXref xref = createDbXref("EBI-1644164,EBI-396176", "IntAct", "whatever");

        Assert.assertEquals("http://www.ebi.ac.uk/intact/search/do/search?binary=EBI-1644164,EBI-396176", resolver.resolve(xref));
        Assert.assertEquals("http://www.ebi.ac.uk/intact/search/do/search?binary=%s", xref.getLinkUrl());
    }

    @Test
    public void testResolveIntActNoEBIId() throws Exception {

        DbXref xref = createDbXref("P51610", "IntAct", "http://www.ebi.ac.uk/intact/pages/interactions/interactions.xhtml?query=%s");

        Assert.assertEquals("http://www.ebi.ac.uk/intact/pages/interactions/interactions.xhtml?query=P51610", resolver.resolve(xref));
        Assert.assertEquals("http://www.ebi.ac.uk/intact/pages/interactions/interactions.xhtml?query=%s", xref.getLinkUrl());
    }

    @Test
    public void testResolveHSSP() throws Exception {

        DbXref xref = createDbXref("PS50853", "HSSP", "%s");

        Assert.assertEquals("http://ps50853", resolver.resolve(xref));
        Assert.assertEquals("http://%s", xref.getLinkUrl());
    }

    @Test
    public void testResolveHSSPWithPDB() throws Exception {

        DbXref xref = createDbXref("PS50853", "HSSP", "%s");
        xref.setProperties(Collections.singletonList(createDbXrefProperty("PDB accession", "1A7F")));

        Assert.assertEquals("http://1a7f", resolver.resolve(xref));
        Assert.assertEquals("http://%s", xref.getLinkUrl());
    }

    // entry/P51610/xref.json
    @Test
    public void testResolveBgeeENSG() throws Exception {

        DbXref xref = createDbXref("ENSG00000172534", "Bgee", "http://bgee.unil.ch/bgee/bgee?uniprot_id=%s");

        Assert.assertEquals("http://bgee.unil.ch/bgee/bgee?page=expression&action=data&ENSG00000172534", resolver.resolve(xref));
        Assert.assertEquals("http://bgee.unil.ch/bgee/bgee?page=expression&action=data&%s", xref.getLinkUrl());
    }

    // entry/P51610/xref.json
    @Test
    public void testResolveBgeeNoENSG() throws Exception {

        DbXref xref = createDbXref("P51610", "Bgee", "http://bgee.unil.ch/bgee/bgee?uniprot_id=%s");

        Assert.assertEquals("http://bgee.unil.ch/bgee/bgee?uniprot_id=P51610", resolver.resolve(xref));
        Assert.assertEquals("http://bgee.unil.ch/bgee/bgee?uniprot_id=%s", xref.getLinkUrl());
    }

    // entry/NX_P01308/xref.json
    @Test
    public void testResolvePeptideAtlasPap() throws Exception {

        DbXref xref = createDbXref("PAp00001490", "PeptideAtlas", "whatever");

        Assert.assertEquals("https://db.systemsbiology.net/sbeams/cgi/PeptideAtlas/GetPeptide?searchWithinThis=Peptide+Name&searchForThis=PAp00001490;organism_name=Human", resolver.resolve(xref));
        Assert.assertEquals("https://db.systemsbiology.net/sbeams/cgi/PeptideAtlas/GetPeptide?searchWithinThis=Peptide+Name&searchForThis=%s;organism_name=Human", xref.getLinkUrl());
    }

    // entry/NX_P01308/xref.json
    @Test
    public void testResolvePeptideAtlasNoPap() throws Exception {

        DbXref xref = createDbXref("P01308", "PeptideAtlas", "whatever");

        Assert.assertEquals("https://db.systemsbiology.net/sbeams/cgi/PeptideAtlas/GetProtein?protein_name=P01308;organism_name=Human;action=GO", resolver.resolve(xref));
        Assert.assertEquals("https://db.systemsbiology.net/sbeams/cgi/PeptideAtlas/GetProtein?protein_name=%s;organism_name=Human;action=GO", xref.getLinkUrl());
    }

    // entry/NX_P01308/xref.json
    @Test
    public void testResolveSRMAtlas() throws Exception {

        DbXref xref = createDbXref("PAp00968082", "SRMAtlas", "https://db.systemsbiology.net/sbeams/cgi/PeptideAtlas/GetTransitions?organism_name=Human;default_search=1;peptide_sequence_constraint=%s;apply_action=QUERY");
        xref.setProperties(Collections.singletonList(createDbXrefProperty("sequence", "GFFYTPK")));

        Assert.assertEquals("https://db.systemsbiology.net/sbeams/cgi/PeptideAtlas/GetTransitions?organism_name=Human;default_search=1;peptide_sequence_constraint=GFFYTPK;apply_action=QUERY",resolver.resolve(xref));
        Assert.assertEquals("https://db.systemsbiology.net/sbeams/cgi/PeptideAtlas/GetTransitions?organism_name=Human;default_search=1;peptide_sequence_constraint=%s;apply_action=QUERY", xref.getLinkUrl());
    }

    @Test
    public void testResolveTKG() throws Exception {

        DbXref xref = createDbXref("0377", "TKG", "http://www2.idac.tohoku.ac.jp/dep/ccr/TKGdate/TKGvo10%n/%s.html");

        Assert.assertEquals("http://www2.idac.tohoku.ac.jp/dep/ccr/TKGdate/TKGvo103/0377.html", resolver.resolve(xref));
        Assert.assertEquals("http://www2.idac.tohoku.ac.jp/dep/ccr/TKGdate/TKGvo10%n/%s.html", xref.getLinkUrl());
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
        Assert.assertEquals("https://www.aidsreagent.org/reagentdetail.cfm?t=cell_lines&id=%s", xref.getLinkUrl());
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
        Assert.assertEquals("http://www.cghtmd.jp/CGHDatabase/mapViewer?hid=%s&aid=4&lang=en", xref.getLinkUrl());
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
        Assert.assertEquals("http://cellbank.nibio.go.jp/~cellbank/cgi-bin/search_res_det.cgi?RNO=ifo1234", resolver.resolve(xref));
        Assert.assertEquals("http://cellbank.nibio.go.jp/~cellbank/cgi-bin/search_res_det.cgi?RNO=%s", xref.getLinkUrl());
    }

    @Test
    public void testResolveJCRB() throws Exception {

        DbXref xref = createDbXref("JCRB1234", "JCRB", "whatever/%s");
        Assert.assertEquals("http://cellbank.nibio.go.jp/~cellbank/en/search_res_list.cgi?KEYWOD=jcrb1234", resolver.resolve(xref));
        Assert.assertEquals("http://cellbank.nibio.go.jp/~cellbank/en/search_res_list.cgi?KEYWOD=%s", xref.getLinkUrl());
    }

    @Test
    public void testResolvePRO() throws Exception {

        DbXref xref = createDbXref("PR:000028527", "PRO", "http://purl.obolibrary.org/obo/PR_%u");

        Assert.assertEquals("http://purl.obolibrary.org/obo/PR_000028527", resolver.resolve(xref));
        Assert.assertEquals("http://purl.obolibrary.org/obo/%s", xref.getLinkUrl());
    }

    @Test
    public void testResolveCLO() throws Exception {

        DbXref xref = createDbXref("CLO:0000031", "CLO", "purl.obolibrary.org/obo/%s");
        Assert.assertEquals("http://purl.obolibrary.org/obo/CLO_0000031", resolver.resolve(xref));
        Assert.assertEquals("http://purl.obolibrary.org/obo/%s", xref.getLinkUrl());
    }

    @Test
    public void testResolveFMA() throws Exception {

        DbXref xref = createDbXref("FMA:62955", "FMA", "http://purl.obolibrary.org/obo/%s");
        Assert.assertEquals("http://purl.obolibrary.org/obo/FMA_62955", resolver.resolve(xref));
        Assert.assertEquals("http://purl.obolibrary.org/obo/%s", xref.getLinkUrl());
    }

    @Test
    public void testResolveHogenom() throws Exception {

        DbXref xref = createDbXref("HOG000007899", "HOGENOM", "http://pbil.univ-lyon1.fr/cgi-bin/acnuc-ac2tree?query=%u&db=HOGENOM");
        Assert.assertEquals("http://pbil.univ-lyon1.fr/cgi-bin/acnuc-ac2tree?query=Q8NBS9&db=HOGENOM", resolver.resolveWithAccession(xref, "NX_Q8NBS9"));
        Assert.assertEquals("http://pbil.univ-lyon1.fr/cgi-bin/acnuc-ac2tree?query=%u&db=HOGENOM", xref.getLinkUrl());
    }

    @Test
    public void testResolveWithDefaultResolverBrenda() throws Exception {

        DbXref xref = createDbXref("2.7.11.21", "BRENDA", "http://www.brenda-enzymes.org/enzyme.php?ecno=%s");
        Assert.assertEquals("http://www.brenda-enzymes.org/enzyme.php?ecno=2.7.11.21", resolver.resolve(xref));
        Assert.assertEquals("http://www.brenda-enzymes.org/enzyme.php?ecno=%s", xref.getLinkUrl());
    }

    @Test
    public void testResolveWithDefaultResolverUniProt() throws Exception {

        DbXref xref = createDbXref("Q8ZAF0", "UniProt", "http://www.uniprot.org/uniprot/%s");
        Assert.assertEquals("http://www.uniprot.org/uniprot/Q8ZAF0", resolver.resolve(xref));
        Assert.assertEquals("http://www.uniprot.org/uniprot/%s", xref.getLinkUrl());
    }

    @Test
    public void testResolveWithAccessionRuleBase() throws Exception {

        DbXref xref = createDbXref("RU003306", "RuleBase", "http://services.uniprot.org/supplement/%u/%s");
        Assert.assertEquals("http://services.uniprot.org/supplement/Q8NCW0/RU003306", resolver.resolveWithAccession(xref, "NX_Q8NCW0"));
    }

    @Test
    public void testResolveWithAccessionUniPathway() throws Exception {

        DbXref xref = createDbXref("UPA00223", "UniPathway", "http://www.unipathway.org?upid=%s&entryac=%u");
        Assert.assertEquals("http://www.unipathway.org?upid=UPA00223&entryac=Q96I99", resolver.resolveWithAccession(xref, "NX_Q96I99"));
    }

    @Test(expected = UnresolvedXrefURLException.class)
    public void testResolveWithAccessionUniPathwayMissingStampW() throws Exception {

        DbXref xref = createDbXref("UPA00223", "UniPathway", "http://www.unipathway.org?upid=%s&entryac=%w");
        resolver.resolveWithAccession(xref, "NX_Q96I99");
    }

    @Test
    public void testResolveWithUrlEncodingShouldNotThrowUnresolvedXrefURLException() throws Exception {

        DbXref xref = createDbXref("Thymosin_%CE%B11", "UniPathway", "http://en.wikipedia.org/wiki/%s");
        resolver.resolveWithAccession(xref, "http://en.wikipedia.org/wiki/Thymosin_%CE%B11");
    }

    @Test
    public void testResolveCCLE() throws Exception {

        DbXref xref = createDbXref("Q8ZAF0", "CCLE", "www.broadinstitute.org/ccle/cell%20lines/%s");
        Assert.assertEquals("http://www.broadinstitute.org/ccle/cell%20lines/Q8ZAF0", resolver.resolve(xref));
    }

    // TODO: we should not have database link with multiple occurrence of %s that are either a stamp and a value !!!!
    @Test
    public void testResolveChitars() throws Exception {

        DbXref xref = createDbXref("ESR1", "ChiTaRS", "http://chitars.bioinfo.cnio.es/cgi-bin/search.pl?searchtype=gene_name&searchstr=%s&%s=1");
        Assert.assertEquals("http://chitars.bioinfo.cnio.es/cgi-bin/search.pl?searchtype=gene_name&searchstr=ESR1&ESR1=1", resolver.resolve(xref));
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