package org.nextprot.api.core.controller;

import static org.nextprot.api.commons.utils.MockMVCUtils.assertWebContent;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.core.dbunit.MVCBaseIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


/**
 * Class used for testing Genomic Mapping controller
 * 
 * @author dteixeira
 */
public class GenomicMappingControllerTest extends MVCBaseIntegrationTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = webAppContextSetup(this.wac).build();
	}

	@Test
	public void shouldGetAMonoExon() throws Exception {
		assertWebContent(mockMvc, "/entry/NX_P41134/genomic/genomic-mapping.xml", "src/test/resources/output-xml/NX_P41134_genomic-mapping.xml");
	}

	@Test
	public void shouldGetAStopOnlyExon() throws Exception {
		assertWebContent(mockMvc, "/entry/NX_Q96M20/genomic/genomic-mapping.xml", "src/test/resources/output-xml/NX_Q96M20_genomic-mapping.xml");
	}

	@Test
	public void shouldGetCorrectAAsForExons() throws Exception {
		assertWebContent(mockMvc, "/entry/NX_P59103/genomic/genomic-mapping.xml", "src/test/resources/output-xml/NX_P59103_genomic-mapping.xml");
	}

	@Test
	public void shouldGetDifferentIsoforMainmName() throws Exception {
		assertWebContent(mockMvc, "/entry/NX_P31994/genomic/genomic-mapping.xml", "src/test/resources/output-xml/NX_P31994_genomic-mapping.xml");
	}

	@Test
	public void shouldCountMultipleGenesIsoformsAndTranscripts() throws Exception {
		assertWebContent(mockMvc, "/entry/NX_Q8NHW4/genomic/genomic-mapping.xml", "src/test/resources/output-xml/NX_Q8NHW4_genomic-mapping.xml");
	}

}
