package org.nextprot.api.core.domain.publication;

import org.junit.Assert;
import org.junit.Test;

public class PublicationDirectLinkTest  {

	/*
	 * Remarks:
	 *
	 * MINT seems to have no data for the examples used
	 * GAD site dead, only home page exists => link to home page
	 *
	 */

    boolean checkLinks = true;

    @Test
    public void testPDBDirectLink() {
        String propertyValue = "[PDB:3DXD] [Structure]";
        String expectedLabel = "[Structure]";

        PublicationDirectLink l = new PublicationDirectLink(1, "comment", propertyValue);
        Assert.assertEquals(1,  l.getPublicationId());
        Assert.assertEquals("PIR", l.getDatasource());
        Assert.assertEquals("PDB", l.getDatabase());
        Assert.assertEquals("3DXD", l.getAccession());
        Assert.assertEquals(expectedLabel, l.getLabel());
        if (checkLinks) Assert.assertEquals("https://www.rcsb.org/pdb/explore/explore.do?pdbId=3DXD", l.getLink());
    }

    @Test
    public void testGeneRifDirectLink() {

        String propertyValue = "[GeneRif:1499] Data indicate that Dickkopf-related protein 3 (DKK3) improved gemcitabine therapeutic effect through inducing apoptosis and regulating beta-catenin/epithelial-mesenchymal transition (EMT) signalling in pancreatic cancer cell.";
        String expectedLabel = "Data indicate that Dickkopf-related protein 3 (DKK3) improved gemcitabine therapeutic effect through inducing apoptosis and regulating beta-catenin/epithelial-mesenchymal transition (EMT) signalling in pancreatic cancer cell.";

        PublicationDirectLink l = new PublicationDirectLink(1, "comment", propertyValue);
        Assert.assertEquals(1,  l.getPublicationId());
        Assert.assertEquals("PIR", l.getDatasource());
        Assert.assertEquals("GeneRif", l.getDatabase());
        Assert.assertEquals("1499", l.getAccession());
        Assert.assertEquals(expectedLabel, l.getLabel());
        if (checkLinks) Assert.assertEquals("http://www.ncbi.nlm.nih.gov/sites/entrez?db=gene&report=GeneRif&term=1499", l.getLink());
    }

    @Test
    public void testIntActDirectLink() {

        String propertyValue = "[IntAct:P30304] [Interaction]";
        String expectedLabel = "[Interaction]";

        PublicationDirectLink l = new PublicationDirectLink(1, "comment", propertyValue);
        Assert.assertEquals(1,  l.getPublicationId());
        Assert.assertEquals("PIR", l.getDatasource());
        Assert.assertEquals("IntAct", l.getDatabase());
        Assert.assertEquals("P30304", l.getAccession());
        Assert.assertEquals(expectedLabel, l.getLabel());
        if (checkLinks) Assert.assertEquals("https://www.ebi.ac.uk/intact/query/P30304*", l.getLink());
    }

    @Test
    public void testiPTMnetDirectLink() {

        String propertyValue = "[iPTMnet:Q99607] [PTM/processing]Phosphorylation";
        String expectedLabel = "[PTM/processing]Phosphorylation";

        PublicationDirectLink l = new PublicationDirectLink(1, "comment", propertyValue);
        Assert.assertEquals(1,  l.getPublicationId());
        Assert.assertEquals("PIR", l.getDatasource());
        Assert.assertEquals("iPTMnet", l.getDatabase());
        Assert.assertEquals("Q99607", l.getAccession());
        Assert.assertEquals(expectedLabel, l.getLabel());
        if (checkLinks) Assert.assertEquals("http://research.bioinformatics.udel.edu/iptmnet/entry/Q99607", l.getLink());
    }

    @Test
    public void testGADDirectLink() {

        String propertyValue = "[GAD:135499] [Pathology & Biotech]Associated with DEVELOPMENTAL: hypospadias.";
        String expectedLabel = "[Pathology & Biotech]Associated with DEVELOPMENTAL: hypospadias.";

        PublicationDirectLink l = new PublicationDirectLink(1, "comment", propertyValue);
        Assert.assertEquals(1,  l.getPublicationId());
        Assert.assertEquals("PIR", l.getDatasource());
        Assert.assertEquals("GAD", l.getDatabase());
        Assert.assertEquals("135499", l.getAccession());
        Assert.assertEquals(expectedLabel, l.getLabel());
        if (checkLinks) Assert.assertEquals("https://geneticassociationdb.nih.gov/?id=135499", l.getLink());
	    //System.out.println(l.getLink());

    }

    @Test
    public void testPhosphoSitePlusDirectLink() {

        String propertyValue = "[PhosphoSitePlus:P68431] [PTM/Processing]";
        String expectedLabel = "[PTM/Processing]";

        PublicationDirectLink l = new PublicationDirectLink(1, "comment", propertyValue);
        Assert.assertEquals(1,  l.getPublicationId());
        Assert.assertEquals("PIR", l.getDatasource());
        Assert.assertEquals("PhosphoSitePlus", l.getDatabase());
        Assert.assertEquals("P68431", l.getAccession());
        Assert.assertEquals(expectedLabel, l.getLabel());
        if (checkLinks) Assert.assertEquals("https://www.phosphosite.org/uniprotAccAction?id=P68431", l.getLink());
    }

    @Test
    public void testReactomeDirectLink() {

        // Examples: NX_Q0IIM8

        String propertyValue = "[Reactome:REACT_11123]";
        String expectedLabel = "";

        PublicationDirectLink l = new PublicationDirectLink(1, "comment", propertyValue);
        Assert.assertEquals(1,  l.getPublicationId());
        Assert.assertEquals("PIR", l.getDatasource());
        Assert.assertEquals("Reactome", l.getDatabase());
        Assert.assertEquals("REACT_11123", l.getAccession());
        Assert.assertEquals(expectedLabel, l.getLabel());
        if (checkLinks) Assert.assertEquals("http://www.reactome.org/PathwayBrowser/#REACT_11123", l.getLink());
    }

    @Test
    public void testPubTatorDirectLink() {

        // Examples: NX_P06858

        String propertyValue = "[PubTator:11897170] [Pathology & Biotech]Variant:p.Ser447Ter, Disease mentioned:Alzheimer Disease [MeSH:D000544]";
        String expectedLabel = "[Pathology & Biotech]Variant:p.Ser447Ter, Disease mentioned:Alzheimer Disease [MeSH:D000544]";

        PublicationDirectLink l = new PublicationDirectLink(1, "comment", propertyValue);
        Assert.assertEquals(1,  l.getPublicationId());
        Assert.assertEquals("PIR", l.getDatasource());
        Assert.assertEquals("PubTator", l.getDatabase());
        Assert.assertEquals("11897170", l.getAccession());
        Assert.assertEquals(expectedLabel, l.getLabel());
        if (checkLinks) Assert.assertEquals("https://www.ncbi.nlm.nih.gov/CBBresearch/Lu/Demo/PubTator/index.cgi?searchtype=PubMed_Search&query=11897170", l.getLink());
    }

    @Test
    public void testPRODirectLink() {

        // Examples: NX_P16401

        String propertyValue = "[PRO:PR:000044882] [PTM/processing]";
        String expectedLabel = "[PTM/processing]";

        PublicationDirectLink l = new PublicationDirectLink(1, "comment", propertyValue);
        Assert.assertEquals(1,  l.getPublicationId());
        Assert.assertEquals("PIR", l.getDatasource());
        Assert.assertEquals("PRO", l.getDatabase());
        Assert.assertEquals("PR:000044882", l.getAccession());
        Assert.assertEquals(expectedLabel, l.getLabel());
        if (checkLinks) Assert.assertEquals("http://research.bioinformatics.udel.edu/pro/entry/PR:000044882/", l.getLink());
        //System.out.println(l.getLink());
    }

    @Test
    public void testMINTDirectLink() {

        // Examples: NX_Q8N8V2

        String propertyValue = "[MINT:MINT-1402058] [Interaction]MINT-65099. TFG (uniprotkb:Q92734) physically interacts (MI:0915) with GBP7 (uniprotkb:Q8N8V2) by two hybrid (MI:0018). From mint";
        String expectedLabel = "[Interaction]MINT-65099. TFG (uniprotkb:Q92734) physically interacts (MI:0915) with GBP7 (uniprotkb:Q8N8V2) by two hybrid (MI:0018). From mint";

        PublicationDirectLink l = new PublicationDirectLink(1, "comment", propertyValue);
        Assert.assertEquals(1,  l.getPublicationId());
        Assert.assertEquals("PIR", l.getDatasource());
        Assert.assertEquals("MINT", l.getDatabase());
        Assert.assertEquals("MINT-1402058", l.getAccession());
        Assert.assertEquals(expectedLabel, l.getLabel());
        if (checkLinks) Assert.assertEquals("http://mint.bio.uniroma2.it/mint/search/search.do?queryType=protein&interactorAc=MINT-1402058", l.getLink());
    }


    @Test
    public void testBioCycDirectLink() {

        // Examples: NX_O75911, NX_P08686, NX_P01106

        String propertyValue = "[BioCyc:MetaCyc:ENSG00000162496-MONOMER] [Function]";
        String expectedLabel = "[Function]";

        PublicationDirectLink l = new PublicationDirectLink(1, "comment", propertyValue);
        Assert.assertEquals(1,  l.getPublicationId());
        Assert.assertEquals("PIR", l.getDatasource());
        Assert.assertEquals("BioCyc", l.getDatabase());
        Assert.assertEquals("MetaCyc:ENSG00000162496-MONOMER", l.getAccession());
        Assert.assertEquals(expectedLabel, l.getLabel());
        if (checkLinks) Assert.assertEquals("http://biocyc.org/getid?id=MetaCyc:ENSG00000162496-MONOMER", l.getLink());
    }

    @Test
    public void testMeropsDirectLink() {

        // Examples: NX_P09958, NX_P00742

        String propertyValue = "[MEROPS:S08.071] [Function]";
        String expectedLabel = "[Function]";

        PublicationDirectLink l = new PublicationDirectLink(1, "comment", propertyValue);
        Assert.assertEquals(1,  l.getPublicationId());
        Assert.assertEquals("PIR", l.getDatasource());
        Assert.assertEquals("MEROPS", l.getDatabase());
        Assert.assertEquals("S08.071", l.getAccession());
        Assert.assertEquals(expectedLabel, l.getLabel());
        if (checkLinks) Assert.assertEquals("https://www.ebi.ac.uk/merops/cgi-bin/pepsum?mid=S08.071", l.getLink());
    }

    @Test
    public void testUniProtDirectLink() {

        String propertyValue = "NUCLEOTIDE SEQUENCE [MRNA] OF 22-260 (ISOFORM 1)";
        String expectedLabel = propertyValue;

        PublicationDirectLink l = new PublicationDirectLink(1, "scope", propertyValue);
        Assert.assertEquals(1,  l.getPublicationId());
        Assert.assertEquals("Uniprot", l.getDatasource());
        Assert.assertEquals("UniProtKB", l.getDatabase());
        Assert.assertEquals(null, l.getAccession());
        Assert.assertEquals(expectedLabel, l.getLabel());
        if (checkLinks) Assert.assertEquals(null, l.getLink());
    }

    @Test
    public void directLinkWithoutLabelShouldBeEmpty() {

        String propertyValue = "[PDB:3DXD]";

        PublicationDirectLink l = new PublicationDirectLink(1, "comment", propertyValue);
        Assert.assertTrue(l.getLabel().isEmpty());
        Assert.assertEquals("PDB", l.getDatabase());
        Assert.assertEquals("3DXD", l.getAccession());
    }

    @Test(expected = IllegalArgumentException.class)
    public void colonShouldExistAsDelimitorInNextprotDatabase() {

        new PublicationDirectLink(1, "comment", "[PDB 3DXD]");
    }

    @Test
    public void directLinkWithoutDatabaseNorAccession() {

        PublicationDirectLink l = new PublicationDirectLink(1, "comment",
                "CFC1 mutations in patients with transposition of the great arteries and double-outlet right ventricle");

        Assert.assertEquals(1,  l.getPublicationId());
        Assert.assertEquals("PIR", l.getDatasource());
        Assert.assertNull(l.getDatabase());
        Assert.assertNull(l.getAccession());
        Assert.assertEquals("CFC1 mutations in patients with transposition of the great arteries and double-outlet right ventricle", l.getLabel());
    }
}