package org.nextprot.api.web.controller;

import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.MVCDBUnitBaseTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static com.sun.org.apache.xerces.internal.util.PropertyState.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author dteixeira
 */

public class SparqlProxyIntegrationTest extends MVCDBUnitBaseTest {


	@Test
	public void shouldGetSPARQLWelcomePage() throws Exception {

		ResultActions result = this.mockMvc.perform(get("/sparql"));
		
		result.andExpect(status().isOk());
        result.andExpect(content().contentType(MediaType.TEXT_HTML));
        result.andExpect(content().string(containsString("UA-17852148-1")));

	}

	// TODO: FIXME
	@Test
	public void shouldRunASPAQRQLQueryIfQueryIsSet() throws Exception {

		// SELECT (COUNT(*) AS ?no) where {
		//  ?s ?p ?o
		//}

		ResultActions result = this.mockMvc.perform(
				get("/sparql?query=SELECT%20(COUNT(*)%20AS%20%3Fno)%20%0Awhere%20%7B%20%3Fs%20%3Fp%20%3Fo%20%20%7D")
						.contentType(MediaType.APPLICATION_JSON_UTF8));

		result.andDo(print());
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.head.vars[0]", is("no")).exists());
		result.andExpect(jsonPath("$.results.bindings[0].no.datatype").exists());
		result.andExpect(jsonPath("$.results.bindings[0].no.value").exists());
	}
}
