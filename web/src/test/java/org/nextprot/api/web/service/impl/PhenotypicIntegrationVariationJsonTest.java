package org.nextprot.api.web.service.impl;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.annot.AnnotationUtils;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PhenotypicIntegrationVariationJsonTest extends WebIntegrationBaseTest {

	@Test
	public void shouldMakeit() {
	}
	
	//@Test
	public void shouldGetPhenotypicVariationAnnotationsForSCN9A() throws Exception {
		
		String content = this.mockMvc.perform(get("/entry/NX_Q15858/phenotypic-variation.json")).
		andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse().getContentAsString();
		ObjectMapper om = new ObjectMapper();
		Map m = om.readValue(content, Map.class);
		Entry e = om.readValue(new ObjectMapper().writeValueAsString(m.get("entry")), Entry.class);
		

		List<Annotation> binaryInteractionAnnotations = AnnotationUtils.filterAnnotationsByCategory(e, AnnotationCategory.BINARY_INTERACTION, true);
		boolean foundGeneName = binaryInteractionAnnotations.stream().anyMatch(f -> {
			return (f.getBioObject().getProperties().get("geneName") != null);
		});
		
		Assert.assertTrue(foundGeneName);
		
	}

}