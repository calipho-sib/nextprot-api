package org.nextprot.api.web.controller;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.MVCDBUnitBaseTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

// TODO understand why it never finishes (semms to build the cache of all entries)
@Ignore
@ActiveProfiles({ "dev","cache" })
public class DbXrefWebTest extends MVCDBUnitBaseTest {

	@Test
	public void shouldResolvedURLWithBothXRefAndEntryAccessions() throws Exception {

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
				.get("/entry/NX_A6NCN8/xref.json").accept(MediaType.APPLICATION_JSON))
				.andReturn();

		String content = result.getResponse().getContentAsString();

		Assert.assertTrue(content.contains("http://www.brenda-enzymes.org/enzyme.php?ecno=2.7.11.1&UniProtAcc=Q8WZ42"));
	}
}
