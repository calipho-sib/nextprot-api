package org.nextprot.api.web.utils;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.io.File;

import org.springframework.test.web.servlet.MockMvc;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public abstract class MockMVCUtils {

	public static void assertWebContent(MockMvc mockMvc, String url, String file) throws Exception {

		String regex = "[\n\t'\" ]";
		String actual = mockMvc.perform(get(url)).andReturn().getResponse().getContentAsString().replaceAll(regex, "");
		String expected = Resources.toString(new File(file).toURI().toURL(), Charsets.UTF_8).replaceAll(regex, "").replace("<?xmlversion=1.0encoding=UTF-8?>", "");
		assertEquals(expected, actual);

	}

}
