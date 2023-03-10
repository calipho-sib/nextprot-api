package org.nextprot.api.web.controller;
import org.flywaydb.core.internal.util.StringUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class TermJSONIntegrationTest extends WebIntegrationBaseTest {

    @Test
    public void shouldAllowToQueryTheApiWithEnzymeFormat() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/term/1.1.1.1").accept(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Assert.assertTrue(content.contains("\"description\" : \"Alcohol dehydrogenase\""));
        Assert.assertTrue(content.contains("\"ontologyDisplayName\" : \"Enzyme classification\""));

    }

    @Test
    public void shouldAllowToQueryTheApiWithEnzymeFormatAndJSONExtensions() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/term/1.1.1.1.json").accept(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Assert.assertTrue(content.contains("\"description\" : \"Alcohol dehydrogenase\""));
    }

    @Autowired
    TerminologyService terminologyService;

    @Test
    @Ignore
    public void shouldAllowToQueryEvEvenIfNotTrimmedInDatabase() throws Exception {

        //This EV was not trimmed in the database (either space before or after...)
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/term/EV:0300156").accept(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Assert.assertTrue(content.contains("\"accession\" : \"EV:0300156\""));
    }

    @Test
    public void shouldReturnA404WhenTermIsNotFoundInDatabase() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/term/whatever").accept(MediaType.APPLICATION_JSON))
                .andReturn();

        Assert.assertEquals(404, result.getResponse().getStatus());

    }

    @Test
    public void shouldAllowToQueryTissues() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/term/TS-0001").accept(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Assert.assertTrue(content.contains("\"accession\" : \"TS-0001\""));
    }


    @Test
    public void shouldGetDescendantByDepth() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/term/TS-2178/descendant-graph.json?includeRelevantFor=true&depth=1").accept(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();

        int count = StringUtils.countOccurrencesOf(content, "accession");

        Assert.assertEquals(count, 7); //6 children + itself

        //children
        Assert.assertTrue(content.contains("Gestational structure"));
        Assert.assertTrue(content.contains("Fluid and secretion"));
        Assert.assertTrue(content.contains("Cell type"));
        Assert.assertTrue(content.contains("Anatomical system"));
        Assert.assertTrue(content.contains("Body part"));
        Assert.assertTrue(content.contains("Tissue"));


        //itself
        Assert.assertTrue(content.contains("Human anatomical entity"));


    }

    @Test
    public void shouldGetDescendantDefault() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/term/TS-2178/descendant-graph").accept(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        int childCount = StringUtils.countOccurrencesOf(content, "accession"); 
        int delta = 50; // error margin
        Assert.assertEquals(1450, childCount, delta);

    }

    @Test
    public void shouldGetAscendent() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/term/TS-0079/ancestor-graph").accept(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        int count = StringUtils.countOccurrencesOf(content, "accession");

        Assert.assertEquals(count, 7); //6 children + itself

        //children
        Assert.assertTrue(content.contains("Blood"));
        Assert.assertTrue(content.contains("Cardiovascular system"));
        Assert.assertTrue(content.contains("Hematopoietic and immune systems"));
        Assert.assertTrue(content.contains("Fluid and secretion"));
        Assert.assertTrue(content.contains("Human anatomical entity"));
        Assert.assertTrue(content.contains("Anatomical system"));
        Assert.assertTrue(content.contains("Hemolymphoid and immune system"));

    }
}

