package org.nextprot.api.integration.tests.rdf;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class PepXIntegrationTest extends WebIntegrationBaseTest {

	//That's how it is used by uniqueness checker tool
	
	@Test
	public void shouldReturnSomePeptidesForUniquenessCheckerTool() throws Exception {

		String content = this.mockMvc.perform(get("/entries/search/peptide")
						.param("peptide", "CLLCALK")
						.param("modeIL", "true"))
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8)).andReturn()
				.getResponse().getContentAsString();
		ObjectMapper om = new ObjectMapper();
		JsonNode actualObj = om.readTree(content);

		// Ensures that the viewer of phenotypes are not broken
		String peptideName = actualObj.get(0).get("annotationsByCategory").get("pepx-virtual-annotation").get(0).get("cvTermName").toString();

		Assert.assertTrue(peptideName.contains("\"CLLCALK\""));
	}

	@Test
	public void shouldReturnSomePeptidesForProteinDigestionTool() throws Exception {

		String content = this.mockMvc.perform(post("/entries/search/peptide-list")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content("[\"NDVVPTMAQGVLEYK\",\"TKMGLYYSYFK\"]"))
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8)).andReturn()
				.getResponse().getContentAsString();
		ObjectMapper om = new ObjectMapper();
		JsonNode actualObj = om.readTree(content);

		Assert.assertEquals(6, actualObj.size());
	}
}