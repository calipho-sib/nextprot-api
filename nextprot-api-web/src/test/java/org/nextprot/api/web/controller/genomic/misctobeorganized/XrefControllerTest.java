package org.nextprot.api.web.controller.genomic.misctobeorganized;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.MVCDBUnitBaseTest;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@DatabaseSetup(value = "DbXrefControllerTest.xml", type = DatabaseOperation.INSERT)
public class XrefControllerTest extends MVCDBUnitBaseTest {

	@Test
	public void testXref() throws Exception {
		this.mockMvc.perform(get("/entry/NX_P12345/xref.xml"))
			.andExpect(status().isOk())
			.andExpect(xpath("/xref-list").exists())
			.andExpect(xpath("/xref-list/xref//@database").exists())
			.andExpect(xpath("/xref-list/xref//@accession").exists())
			.andExpect(xpath("/xref-list/xref/properties/property").exists())
			.andExpect(xpath("/xref-list/xref/properties/property//@propertyName").string("type"))
			.andExpect(xpath("/xref-list/xref/properties/property//@value").string("whatever id"))
			.andExpect(xpath("/xref-list/xref/url").exists());
	}
}
