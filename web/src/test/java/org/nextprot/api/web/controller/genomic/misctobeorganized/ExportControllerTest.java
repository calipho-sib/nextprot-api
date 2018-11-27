package org.nextprot.api.web.controller.genomic.misctobeorganized;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.MVCBaseIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


public class ExportControllerTest extends MVCBaseIntegrationTest {

	@Autowired
	private WebApplicationContext wacAppConfiguration;

	private MockMvc mockMvc;
	
	@Before
	public void setup() {
		this.mockMvc = webAppContextSetup(this.wacAppConfiguration).build();
	}
	
	@Test
	public void testXMLExport() throws Exception {

		this.mockMvc.perform(get("/export/entries.xml?query=id:NX_Q9BZJ3"))
				.andExpect(status().isOk())
				.andExpect(xpath("/nextprot-export").exists())
				.andExpect(xpath("//annotation").exists())
				.andExpect(xpath("//publication").exists());
	}

	@Test
	public void testXMLExportAccession() throws Exception {

		this.mockMvc.perform(get("/export/entries/accession.xml?query=krypton"))
				.andExpect(status().isOk())
				.andExpect(xpath("/nextprot-export").exists())
				.andExpect(xpath("//entry//@accession").string("NX_O75951"));
	}

	@Test
	public void testFastaExport() throws Exception {

		String content = this.mockMvc.perform(get("/export/entries.fasta?query=id:NX_Q9BZJ3"))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		Assert.assertTrue(content.startsWith(">nxp|NX_Q9BZJ3-1|TPSD1|Tryptase delta|Iso 1"));
	}

	@Test
	public void testTXTExport() throws Exception {

		String content = this.mockMvc.perform(get("/export/entries.txt?query=id:NX_Q9BZJ3"))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		Assert.assertTrue(content.contains("NX_Q9BZJ3"));
	}

	@Test
	public void testXLSExport() throws Exception {

		this.mockMvc.perform(get("/export/entries.xls?query=id:NX_Q9BZJ3"))
				.andExpect(status().isOk());
	}
}
