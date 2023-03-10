package org.nextprot.api.web.controller;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.MVCDBUnitBaseTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;

@ActiveProfiles({ "dev", "cache" })
public class EntryControllerTest extends MVCDBUnitBaseTest {

    @Test
    public void shouldExportJsonEntry() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/entry/NX_P01112.json").accept(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        Assert.assertTrue(content.contains("\"uniqueName\" : \"NX_P01112\""));
        Assert.assertTrue(!content.contains("peffByIsoform"));
    }

    @Test
    public void shouldExportPeffEntry() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/export/entry/NX_P01308.peff"))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        Assert.assertTrue(content.contains(">nxp:NX_P01308-1"));
        Assert.assertTrue(content.contains("MALWMRLLPLLALLALWGPDPAAAFVNQHLCGSHLVEALYLVCGERGFFYTPKTRREAED"));
        Assert.assertTrue(content.contains("LQVGQVELGGGPGAGSLQPLALEGSLQKRGIVEQCCTSICSLYQLENYCN"));
    }

    @Test
    public void shouldExportFastaEntry() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/export/entry/NX_P01308.fasta"))
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
                "\"geneEndPosition\" : \"2161221\"",
                "\"codingStrand\" : \"reverse\"",
                "\"entryAccession\" : \"NX_P01308\"",
                "\"proteinExistence\" : \"protein level\"",
                "\"proteomics\" : true",
                "\"antibody\" : true",
                "\"3D\" : true",
                "\"disease\" : true",
                "\"isoforms\" : 1",
                "\"variants\" : ",
                "\"ptms\" : ",
                "\"curatedPublicationCount\" : ",
                "\"additionalPublicationCount\" : ",
                "\"patentCount\" : ",
                "\"submissionCount\" : ",
                "\"webResourceCount\" : ",
                "\"entryDescription\" : \"Insulin\""
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
                "\"Localization\" : true",
                "\"Peptides\" : true",
                "\"Expression\" : true",
                "\"Exons\" : false",
                "\"Proteomics\" : true",
                "\"Medical\" : true",
                "\"Structures\" : true",
                "\"Interactions\" : true",
                "\"Sequence\" : true",
                "\"Identifiers\" : true",
                "\"Gene Identifiers\" : true"
        ).forEach(expectedContent -> Assert.assertTrue(content+": content not as expected:"+expectedContent, content.contains(expectedContent)));
    }

    @Test
    public void countAnnotation() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/entry/NX_P01308/annotation-count").accept(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Assert.assertTrue(content.matches("\\d+"));
    }

    @Test
    public void checkNX_P01574IsMutagenesis() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/entry/NX_P01574/stats").accept(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Assert.assertTrue(content.contains("\"mutagenesis\" : true"));
    }

    @Test
    public void checkNX_O15498IsMutagenesis() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/entry/NX_O15498/stats").accept(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Assert.assertTrue(content.contains("\"mutagenesis\" : true"));
    }

    @Test
    public void checkNX_Q9UBP0IsMutagenesis() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/entry/NX_Q9UBP0/stats").accept(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Assert.assertTrue(content.contains("\"mutagenesis\" : true"));
    }

    @Test
    public void checkNX_Q9UJT9IsNotMutagenesis() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/entry/NX_Q9UJT9/stats").accept(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Assert.assertTrue(content.contains("\"mutagenesis\" : false"));
    }
}