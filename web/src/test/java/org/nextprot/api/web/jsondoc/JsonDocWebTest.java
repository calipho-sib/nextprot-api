package org.nextprot.api.web.jsondoc;

import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.MVCDBUnitBaseTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author dteixeira
 */

public class JsonDocWebTest extends MVCDBUnitBaseTest {

	@Test
	public void shouldGetJsonDocumentation() throws Exception {

		ResultActions result = this.mockMvc.perform(get("/jsondoc"));
		
		//System.out.println(result.andReturn().getResponse().getContentAsString());
		result.andExpect(status().isOk());
        result.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
		//result.andExpect(jsonPath("version").exists());

	}

}
