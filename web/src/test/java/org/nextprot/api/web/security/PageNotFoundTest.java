package org.nextprot.api.web.security;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.MVCBaseSecurityTest;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


public class PageNotFoundTest extends MVCBaseSecurityTest {

	@Test
	public void shouldGetA404ForAPageThatDoesNotExist() throws Exception {
		this.mockMvc.perform(get("/somethingthatnotexist").accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
	}

}
