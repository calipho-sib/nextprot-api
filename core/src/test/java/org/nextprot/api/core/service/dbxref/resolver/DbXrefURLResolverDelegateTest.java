package org.nextprot.api.core.service.dbxref.resolver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;

import java.util.Collections;

public class DbXrefURLResolverDelegateTest {

    private DbXrefURLResolverDelegate resolver;

    @Before
    public void setup() {

        resolver = new DbXrefURLResolverDelegate();
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
    public void testResolveCellosaurus() throws Exception {

        DbXref xref = createDbXref("CVCL_7180", "Cellosaurus", "http://www.youpie.uk/whatever");

        Assert.assertEquals("https://web.expasy.org/cellosaurus/CVCL_7180", resolver.resolve(xref));
        Assert.assertEquals("https://web.expasy.org/cellosaurus/%s", xref.getLinkUrl());
    }
    
    @Test
    public void testResolveRNAct() throws Exception {

        DbXref xref = createDbXref("Q8TCH9", "RNAct", "http://www.youpie.uk/whatever");

        Assert.assertEquals("https://rnact.crg.eu/protein?query=Q8TCH9", resolver.resolve(xref));
        Assert.assertEquals("https://rnact.crg.eu/protein?query=%s", xref.getLinkUrl());
    }
    
    
    
    
    @Test
    public void testResolveExpressionAtlas() throws Exception {

        DbXref xref = createDbXref("AF009225", "ExpressionAtlas", "http://www.ebi.ac.uk/whatever");

        Assert.assertEquals("http://www.ebi.ac.uk/gxa/search?geneQuery=%09AF009225", resolver.resolve(xref));
        Assert.assertEquals("http://www.ebi.ac.uk/gxa/search?geneQuery=%09%s", xref.getLinkUrl());
    }

    @Test
    public void testResolveEmbl() throws Exception {

        DbXref xref = createDbXref("AF009225", "EMBL", "http://www.ebi.ac.uk/ena/data/view/%s");

        Assert.assertEquals("http://www.ebi.ac.uk/ena/data/view/AF009225", resolver.resolve(xref));
        Assert.assertEquals("http://www.ebi.ac.uk/ena/data/view/%s", xref.getLinkUrl());
    }
    

    
    
    @Test
    public void testResolvePsiMod() throws Exception {

        DbXref xref = createDbXref("00952", "PSI-MOD", "http://www.ebi.ac.uk/whatever/%s");

        Assert.assertEquals("https://www.ebi.ac.uk/ols/ontologies/mod/terms?iri=http://purl.obolibrary.org/obo/MOD_00952", resolver.resolve(xref));
        Assert.assertEquals("https://www.ebi.ac.uk/ols/ontologies/mod/terms?iri=http://purl.obolibrary.org/obo/MOD_%s", xref.getLinkUrl());
    }

    
    
    
    
    
    @Test
    public void testResolveECO() throws Exception {

        DbXref xref = createDbXref("ECO:0000040", "ECO", "http://www.ebi.ac.uk/whatever/%s");

        Assert.assertEquals("http://purl.obolibrary.org/obo/ECO_0000040", resolver.resolve(xref));
        Assert.assertEquals("http://www.ontobee.org/ontology/ECO", resolver.getValidXrefURL("whatever", "ECO"));
        Assert.assertEquals("http://purl.obolibrary.org/obo/%s", xref.getLinkUrl());

    }

    @Test
    public void testResolveMeSH() throws Exception {

        DbXref xref = createDbXref("D000005", "MeSH", "http://www.ebi.ac.uk/whatever/%s");
        Assert.assertEquals("https://meshb.nlm.nih.gov/record/ui?ui=D000005", resolver.resolve(xref));
        Assert.assertEquals("https://meshb.nlm.nih.gov/record/ui?ui=%s", xref.getLinkUrl());
    }

    @Test
    public void testResolveEvocDevStage() throws Exception {

        DbXref xref = createDbXref("EV:0300005", "eVOC", "http://www.ebi.ac.uk/whatever/%s");
        Assert.assertEquals("None", resolver.resolve(xref));       
        // WARNING: do not rely on getLinkURL() for eVOC, its value would be different if called before getResolvedUrl();
        Assert.assertEquals(null, xref.getLinkUrl()); 
    }

    @Test
    public void testResolveUniPathway() throws Exception {

        DbXref xref = createDbXref("UPA00125", "UniPathway", "http://www.ebi.ac.uk/whatever/%s");
        Assert.assertEquals("None", resolver.resolve(xref));      
        // WARNING: do not rely on getLinkURL() for UniPathway, its value would be different if called before getResolvedUrl();
        Assert.assertEquals(null, xref.getLinkUrl()); 
    }

    @Test
    public void testResolveUniprotControlVocabulary() throws Exception {
    	    	// DI-04168','KW-0413','SL-0002','SL-9910','SL-9902
        DbXref xref;
        
        // disease terms
        xref = createDbXref("DI-04168", "UniProt control vocabulary", "http://www.toto.ch/whatever/%s");
        Assert.assertEquals("http://www.uniprot.org/diseases/DI-04168", resolver.resolve(xref));    

        // keywords
        xref = createDbXref("KW-0413", "UniProt control vocabulary", "http://www.toto.ch/whatever/%s");
        Assert.assertEquals("http://www.uniprot.org/keywords/KW-0413", resolver.resolve(xref));    

        // subcell localizations
        xref = createDbXref("SL-0002", "UniProt control vocabulary", "http://www.toto.ch/whatever/%s");
        Assert.assertEquals("http://www.uniprot.org/locations/SL-0002", resolver.resolve(xref));    

        // orientation
        xref = createDbXref("SL-9910", "UniProt control vocabulary", "http://www.toto.ch/whatever/%s");
        Assert.assertEquals("http://www.uniprot.org/locations/SL-9910", resolver.resolve(xref));    

        // topology
        xref = createDbXref("SL-9902", "UniProt control vocabulary", "http://www.toto.ch/whatever/%s");
        Assert.assertEquals("http://www.uniprot.org/locations/SL-9902", resolver.resolve(xref));    

        // families are not resolved
        xref = createDbXref("DDX4/VASA subfamily", "UniProt control vocabulary", "http://www.toto.ch/whatever/%s");
        Assert.assertEquals("None", resolver.resolve(xref));    

        // PTMs are not resolved
        xref = createDbXref("PTM-0390", "UniProt control vocabulary", "http://www.toto.ch/whatever/%s");
        Assert.assertEquals("None", resolver.resolve(xref));    
        
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

    @Test
    public void testResolveEnsemblBadPrimaryId() throws Exception {

        DbXref xref = createDbXref("EST00000587522", "Ensembl", "whatever");

        Assert.assertEquals("https://www.ensembl.org/Multi/Search/Results?q=EST00000587522;site=ensembl", resolver.resolve(xref));
        Assert.assertEquals("https://www.ensembl.org/Multi/Search/Results?q=%s;site=ensembl", xref.getLinkUrl());
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

        Assert.assertEquals("https://www.rcsb.org/pdb/explore/explore.do?pdbId=1A7F", resolver.resolve(xref));
        Assert.assertEquals("https://www.rcsb.org/pdb/explore/explore.do?pdbId=%s", xref.getLinkUrl());
    }

    // entry/NX_Q9BXA6/xref.json
    @Test
    public void testResolveHPAGene() throws Exception {

        DbXref xref = createDbXref("ENSG00000254647", "HPA", "whatever");

        Assert.assertEquals("https://v19.proteinatlas.org/ENSG00000254647", resolver.resolve(xref));
        Assert.assertEquals("https://v19.proteinatlas.org/%s", xref.getLinkUrl());
    }

    // entry/NX_P51610/xref.json
    @Test
    public void testResolveHPASubcellular() throws Exception {

        DbXref xref = createDbXref("ENSG00000254647/subcellular", "HPA", "whatever");

        Assert.assertEquals("https://v19.proteinatlas.org/ENSG00000254647/cell", resolver.resolve(xref));
        Assert.assertEquals("https://v19.proteinatlas.org/%s", xref.getLinkUrl());
    }

    // entry/NX_P51610/xref.json
    @Test
    public void testResolveHPAAntibody() throws Exception {

        DbXref xref = createDbXref("HPA018312", "HPA", "whatever");

        Assert.assertEquals("https://v19.proteinatlas.org/search/HPA018312", resolver.resolve(xref));
        Assert.assertEquals("https://v19.proteinatlas.org/search/%s", xref.getLinkUrl());
    }


    @Test
    public void testResolveUniGene() throws Exception {

        DbXref xref = createDbXref("Hs.83634", "UniGene", "https://www.ncbi.nlm.nih.gov/UniGene/clust.cgi?ORG=%d&CID=%s");

        Assert.assertEquals("https://www.ncbi.nlm.nih.gov/UniGene/clust.cgi?ORG=Hs&CID=83634", resolver.resolve(xref));
        Assert.assertEquals("https://www.ncbi.nlm.nih.gov/UniGene/clust.cgi?ORG=%d&CID=%s", xref.getLinkUrl());
    }

    @Test
    public void testResolveUCSC() throws Exception {

        DbXref xref = createDbXrefWithEntry("NX_Q9UPW6", "uc002uuy.2", "UCSC", "http://www.some.org/this/template/should/be/overriden/%s");
        Assert.assertEquals("https://genome.ucsc.edu/cgi-bin/hgLinkIn?resource=uniprot&id=Q9UPW6", resolver.resolve(xref));
        Assert.assertEquals("https://genome.ucsc.edu/cgi-bin/hgLinkIn?resource=uniprot&id=%u", xref.getLinkUrl());
    }

    @Test
    public void testResolveIntAct() throws Exception {

        DbXref xref = createDbXref("EBI-1644164,EBI-396176", "IntAct", "whatever");

        //Assert.assertEquals("http://www.ebi.ac.uk/intact/search/do/search?binary=EBI-1644164,EBI-396176", resolver.resolve(xref));
        //Assert.assertEquals("http://www.ebi.ac.uk/intact/search/do/search?binary=%s", xref.getLinkUrl());
        Assert.assertEquals("https://www.ebi.ac.uk/intact/pages/details/details.xhtml?binary=EBI-1644164,EBI-396176", resolver.resolve(xref));
        Assert.assertEquals("https://www.ebi.ac.uk/intact/pages/details/details.xhtml?binary=%s", xref.getLinkUrl());
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

    	String dbURL = "https://bgee.org/bgee14_1/?page=gene&gene_id=%s";
    	String evidenceURL = "https://bgee.org/bgee14_1/?page=gene&gene_id=";
    	String ac = "ENSG00000124532";
    	
        DbXref xref = createDbXref(ac, "Bgee", dbURL);

        Assert.assertEquals(evidenceURL + ac, resolver.resolve(xref));
        Assert.assertEquals(evidenceURL + "%s", xref.getLinkUrl());
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

        DbXref xref = createDbXrefWithEntry("NX_Q8NBS9", "HOG000007899", "HOGENOM", "http://pbil.univ-lyon1.fr/cgi-bin/acnuc-ac2tree?query=%u&db=HOGENOM");
        Assert.assertEquals("http://pbil.univ-lyon1.fr/cgi-bin/acnuc-ac2tree?query=Q8NBS9&db=HOGENOM", resolver.resolve(xref));
        Assert.assertEquals("http://pbil.univ-lyon1.fr/cgi-bin/acnuc-ac2tree?query=%u&db=HOGENOM", xref.getLinkUrl());
    }

    @Test
    public void testResolveWithDefaultResolverBrenda() throws Exception {

        DbXref xref = createDbXrefWithEntry("NX_Q8NBS9", "2.7.11.21", "BRENDA", CvDatabasePreferredLink.BRENDA.getLink());
        Assert.assertEquals("https://www.brenda-enzymes.org/enzyme.php?ecno=2.7.11.21&UniProtAcc=Q8NBS9", resolver.resolve(xref));
        Assert.assertEquals("https://www.brenda-enzymes.org/enzyme.php?ecno=%s&UniProtAcc=%u", xref.getLinkUrl());
    }

    @Test
    public void testResolveWithDefaultResolverUniProt() throws Exception {

        DbXref xref = createDbXref("Q8ZAF0", "UniProt", "http://www.uniprot.org/uniprot/%s");
        Assert.assertEquals("http://www.uniprot.org/uniprot/Q8ZAF0", resolver.resolve(xref));
        Assert.assertEquals("http://www.uniprot.org/uniprot/%s", xref.getLinkUrl());
    }

    @Test
    public void testResolveWithAccessionRuleBase() throws Exception {

        DbXref xref = createDbXref("RU000461", "RuleBase", "http://www.uniprot.org/unirule/%s");
        Assert.assertEquals("http://www.uniprot.org/unirule/RU000461", resolver.resolve(xref));
    }


    @Test
    public void testResolveCCLE() throws Exception {

        DbXref xref = createDbXref("Q8ZAF0", "CCLE", "www.broadinstitute.org/ccle/cell%20lines/%s");
        Assert.assertEquals("http://www.broadinstitute.org/ccle/cell%20lines/Q8ZAF0", resolver.resolve(xref));
    }

    // TODO: we should not have database link with multiple occurrence of %s that are either a stamp and a value !!!!
    @Test
    public void testResolveChitars() throws Exception {

        DbXref xref = createDbXref("HIST1H3B", "ChiTaRS", "http://ww.example.com/should/be/replaced/by/preferred/%s");
        Assert.assertEquals("http://chitars.bioinfo.cnio.es/cgi-bin/search.pl?searchtype=gene_name&searchstr=HIST1H3B&human=1", resolver.resolve(xref));
    }

    
    
    
    public static DbXref createDbXrefWithEntry(String entryAccession, String accession, String dbName, String linkURL) {

        DbXref xref = createDbXref(accession, dbName, linkURL);

        xref.setProteinAccessionReferer(entryAccession);

        return xref;
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