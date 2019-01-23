package org.nextprot.api.web.controller;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.MVCDBUnitBaseTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.ResultActions;

import java.net.URI;
import java.util.Collections;

import static com.sun.org.apache.xerces.internal.util.PropertyState.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author dteixeira
 */
@ActiveProfiles({"unit",  "unit-schema-user", "security"})
public class SparqlProxyIntegrationTest extends MVCDBUnitBaseTest {


	@Test
	public void shouldGetSPARQLWelcomePage() throws Exception {

		ResultActions result = this.mockMvc.perform(get("/sparql"));
		
		result.andExpect(status().isOk());
        result.andExpect(content().contentType(MediaType.TEXT_HTML));
        result.andExpect(content().string(containsString("UA-17852148-1")));

	}

	@Test
	public void shouldRunASPAQRQLQueryIfQueryIsSet() throws Exception {

		String payload = EntityUtils.toString(new UrlEncodedFormEntity(Collections.singletonList(
				new BasicNameValuePair("query", "SELECT (COUNT(*) AS ?no) where {?s ?p ?o }")
		)));

		ResultActions result = this.mockMvc.perform(post(new URI("/sparql?"+payload))
				.accept("application/sparql-results+json"));

		result.andDo(print());
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.head.vars[0]", is("no")).exists());
		result.andExpect(jsonPath("$.results.bindings[0].no.datatype").exists());
		result.andExpect(jsonPath("$.results.bindings[0].no.value").exists());
	}
}
