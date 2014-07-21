package org.nextprot.api.utils;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.nextprot.utils.FileUtils;
import org.springframework.test.web.servlet.MockMvc;

public abstract class MockMVCUtils {

	public static void assertWebContent(MockMvc mockMvc, String url, String file) throws Exception {

		String regex = "[\n\t'\" ]";
		String actual = mockMvc.perform(get(url)).andReturn().getResponse().getContentAsString().replaceAll(regex, "");
		String expected = FileUtils.readFileAsString(file).replaceAll(regex, "").replace("<?xmlversion=1.0encoding=UTF-8?>", "");
		assertEquals(expected, actual);

	}

}
