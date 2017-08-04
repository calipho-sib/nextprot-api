package org.nextprot.api.web.controller;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.service.export.format.NextprotMediaType;
import org.nextprot.api.web.dbunit.base.mvc.MVCDBUnitBaseTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@ActiveProfiles({ "dev" })
public class EntryControllerTest extends MVCDBUnitBaseTest {

    @Test
    public void shouldExportJsonEntry() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/entry/NX_P01308.json").accept(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        Assert.assertTrue(content.contains("\"uniqueName\" : \"NX_P01308\""));
        Assert.assertTrue(!content.contains("peffByIsoform"));
    }

    @Test
    public void shouldExportPeffEntry() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/entry/NX_P01308.peff"))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        Assert.assertTrue(content.contains(">nxp:NX_P01308-1 \\DbUniqueId=NX_P01308-1 \\PName=Insulin isoform Iso 1 \\GName=INS \\NcbiTaxId=9606 \\TaxName=Homo Sapiens \\Length=110 \\SV=1 \\EV=224 \\PE=1 \\ModRes=(31||Disulfide)(96||Disulfide)(43||Disulfide)(109||Disulfide)(95||Disulfide)(100||Disulfide) \\VariantSimple=(2|T)(6|C)(6|G)(6|H)(8|Q)(12|V)(18|R)(21|L)(22|V)(23|S)(23|T)(24|D)(24|V)(29|D)(29|P)(32|R)(32|S)(34|D)(35|P)(38|V)(43|G)(44|R)(45|K)(46|Q)(47|V)(48|C)(48|S)(49|L)(51|I)(52|R)(53|E)(53|T)(55|C)(55|H)(56|W)(58|V)(63|A)(63|L)(64|W)(65|L)(68|M)(70|R)(71|V)(73|C)(75|D)(76|N)(76|R)(79|L)(81|V)(83|K)(84|R)(85|Y)(89|C)(89|H)(89|L)(89|P)(90|C)(90|D)(92|L)(93|K)(94|K)(96|S)(96|Y)(98|R)(101|C)(103|C)(106|D)(108|C) \\Processed=(1|24|signal peptide)(25|54|mature protein)(57|87|maturation peptide)(90|110|mature protein)"));
        Assert.assertTrue(content.contains("MALWMRLLPLLALLALWGPDPAAAFVNQHLCGSHLVEALYLVCGERGFFYTPKTRREAED"));
        Assert.assertTrue(content.contains("LQVGQVELGGGPGAGSLQPLALEGSLQKRGIVEQCCTSICSLYQLENYCN"));
    }

    @Test
    public void shouldExportFastaEntry() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/entry/NX_P01308.fasta"))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        Assert.assertTrue(content.contains(">nxp|NX_P01308-1|INS|Insulin|Iso 1"));
        Assert.assertTrue(content.contains("MALWMRLLPLLALLALWGPDPAAAFVNQHLCGSHLVEALYLVCGERGFFYTPKTRREAED"));
        Assert.assertTrue(content.contains("LQVGQVELGGGPGAGSLQPLALEGSLQKRGIVEQCCTSICSLYQLENYCN"));
    }

    @Test
    public void getSubPart() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/entry/NX_P01308/accession"))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        Assert.assertTrue(content.contains("\"uniqueName\" : \"NX_P01308\""));
        Assert.assertTrue(content.contains("\"uniprotName\" : \"P01308\""));
    }

    @Test
    public void getEntryReport() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/entry/NX_P01308/report"))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        Arrays.asList(
                "\"geneName\" : \"INS\"",
                "\"chromosomalLocation\" : \"11p15.5\"",
                "\"geneStartPosition\" : \"2159779\"",
                "\"geneEndPosition\" : \"2161341\"",
                "\"codingStrand\" : \"reverse\"",
                "\"entryAccession\" : \"NX_P01308\"",
                "\"proteinExistence\" : \"protein level\"",
                "\"proteomics\" : true",
                "\"antibody\" : true",
                "\"3D\" : true",
                "\"disease\" : true",
                "\"isoforms\" : 1",
                "\"variants\" : 68",
                "\"ptms\" : 3",
                "\"entryDescription\" : \"Insulin\""
        ).forEach(expectedContent -> Assert.assertTrue(content.contains(expectedContent)));
    }

    @Test
    public void getIsoformSequenceInfos() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/isoform/NX_P01308-1/peff").accept(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        Arrays.asList(
            "\"isoformAccession\" : \"NX_P01308-1\"",
            "\"isoformAccessionFormat\" : \"\\\\DbUniqueId=NX_P01308-1\"",
            "\"proteinNameFormat\" : \"\\\\PName=Insulin isoform Iso 1\"",
            "\"geneNameFormat\" : \"\\\\GName=INS\"",
            "\"ncbiTaxonomyIdentifierFormat\" : \"\\\\NcbiTaxId=9606\"",
            "\"taxonomyNameFormat\" : \"\\\\TaxName=Homo Sapiens\"",
            "\"sequenceLengthFormat\" : \"\\\\Length=110\"",
            "\"sequenceVersionFormat\" : \"\\\\SV=1\"",
            "\"entryVersionFormat\" : \"\\\\EV=224\"",
            "\"proteinEvidenceFormat\" : \"\\\\PE=1\"",
            "\"variantSimpleFormat\" : \"\\\\VariantSimple=(2|T)(6|C)(6|G)(6|H)(8|Q)(12|V)(18|R)(21|L)(22|V)(23|S)(23|T)(24|D)(24|V)(29|D)(29|P)(32|R)(32|S)(34|D)(35|P)(38|V)(43|G)(44|R)(45|K)(46|Q)(47|V)(48|C)(48|S)(49|L)(51|I)(52|R)(53|E)(53|T)(55|C)(55|H)(56|W)(58|V)(63|A)(63|L)(64|W)(65|L)(68|M)(70|R)(71|V)(73|C)(75|D)(76|N)(76|R)(79|L)(81|V)(83|K)(84|R)(85|Y)(89|C)(89|H)(89|L)(89|P)(90|C)(90|D)(92|L)(93|K)(94|K)(96|S)(96|Y)(98|R)(101|C)(103|C)(106|D)(108|C)",
            "\"variantComplexFormat\" : \"\"",
            "\"modResPsiFormat\" : \"\"",
            "\"modResFormat\" : \"\\\\ModRes=(31||Disulfide)(96||Disulfide)(43||Disulfide)(109||Disulfide)(95||Disulfide)(100||Disulfide)\"",
            "\"processedMoleculeFormat\" : \"\\\\Processed=(1|24|signal peptide)(25|54|mature protein)(57|87|maturation peptide)(90|110|mature protein)\""
        ).forEach(expectedContent -> Assert.assertTrue(content.contains(expectedContent)));
    }

    @Test
    public void getIsoformsMappings() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/entry/NX_P52701/isoform/mapping").accept(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        Arrays.asList(
                "\"isoformAc\" : \"NX_P52701-1\"",
                "\"isoformAc\" : \"NX_P52701-2\"",
                "\"isoformAc\" : \"NX_P52701-3\"",
                "\"isoformAc\" : \"NX_P52701-4\""
        ).forEach(expectedContent -> Assert.assertTrue(content.contains(expectedContent)));
    }

    @Test
    public void testPageDisplay() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/entry/NX_P01308/page-display").accept(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        Arrays.asList(
                "\"Function\" : true",
                "\"Phenotypes\" : false",
                "\"Localisation\" : true",
                "\"Peptides\" : true",
                "\"Expression\" : true",
                "\"Exons\" : false",
                "\"Proteomics\" : true",
                "\"Medical\" : true",
                "\"Structures\" : true",
                "\"Interactions\" : true",
                "\"Sequence\" : true",
                "\"Identifiers\" : true"
        ).forEach(expectedContent -> Assert.assertTrue(content.contains(expectedContent)));
    }

    @Test
    public void countAnnotation() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/entry/NX_P01308/annotation-count").accept(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        Assert.assertTrue(content.matches("\\d+"));
    }
}