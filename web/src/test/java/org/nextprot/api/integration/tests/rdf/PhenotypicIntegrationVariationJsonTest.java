package org.nextprot.api.integration.tests.rdf;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PhenotypicIntegrationVariationJsonTest extends WebIntegrationBaseTest {

	@Test
	public void shouldReturnCorrectJsonForPhenotypeViewerPageWithGeneNameInBioObject() throws Exception {

		String content = this.mockMvc.perform(get("/entry/NX_Q15858/phenotypic-variation.json")).andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn()
				.getResponse().getContentAsString();
		ObjectMapper om = new ObjectMapper();
		JsonNode actualObj = om.readTree(content);

		// Ensures that the viewer of phenotypes are not broken
		String geneName = actualObj.get("entry").get("annotationsByCategory").get("binary-interaction").get(0).get("bioObject").get("properties").get("geneName").toString();

		System.out.println("gene name " + geneName); // Should print NEDD4L
		Assert.assertFalse(geneName.isEmpty());
	}

	
	@Test
	public void shouldNotReturnSilverWhenAskingForGoldOnly() throws Exception {

		String content = this.mockMvc.perform(get("/entry/NX_Q15858/phenotypic-variation.json")).andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn()
				.getResponse().getContentAsString();
		
		Assert.assertTrue(content.toUpperCase().contains("SILVER"));
	
		//Equivalent to /entry/NX_Q15858/phenotypic-variation.json?gold
		String goldContent = this.mockMvc.perform(get("/entry/NX_Q15858/phenotypic-variation.json").param("goldOnly", "true")).andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn()
				.getResponse().getContentAsString();

		Assert.assertFalse(goldContent.toUpperCase().contains("SILVER"));

	}
}