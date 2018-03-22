package org.nextprot.api.web.controller;
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

}

