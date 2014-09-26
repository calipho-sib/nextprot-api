package org.nextprot.api.core.controller.annotation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import org.hamcrest.core.StringContains;
import org.junit.Test;
import org.nextprot.api.core.dbunit.MVCDBUnitBaseTest;
import org.springframework.test.web.servlet.ResultActions;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

/**
 * @author dteixeira
 */

@DatabaseSetup(value = "AnnotationMVCTest.xml", type = DatabaseOperation.INSERT)
public class AnnotationWebTest extends MVCDBUnitBaseTest {

	@Test
	public void shouldGetXML() throws Exception {

		ResultActions result = this.mockMvc.perform(get("/entry/NX_P12345/annotation.xml"));
		result.andExpect(xpath("annotation-list/annotation-to-be-renamed").nodeCount(2));
		result.andExpect(xpath("annotation-list/annotation-to-be-renamed[@category='go biological process']/annotation").nodeCount(1));
		result.andExpect(xpath("annotation-list/annotation-to-be-renamed[@category='go molecular function']/annotation").nodeCount(4));
		result.andExpect(xpath("annotation-list/annotation-to-be-renamed[@category='go biological process']/annotation/description").string(new StringContains("Binds to AB1, AB5 and AB4")));

	}

}
