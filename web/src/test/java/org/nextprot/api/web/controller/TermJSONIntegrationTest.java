package org.nextprot.api.web.controller;
import org.flywaydb.core.internal.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

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
        int count = StringUtils.countOccurrencesOf(content, "accession");

        Assert.assertEquals(count, 900, 50);

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
        Assert.assertTrue(content.contains("Gestational structure"));
        Assert.assertTrue(content.contains("Fluid and secretion"));
        Assert.assertTrue(content.contains("Cell type"));
        Assert.assertTrue(content.contains("Anatomical system"));
        Assert.assertTrue(content.contains("Body part"));
        Assert.assertTrue(content.contains("Tissue"));


        //itself
        Assert.assertTrue(content.contains("Human anatomical entity"));



    }
}

