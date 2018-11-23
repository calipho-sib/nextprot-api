package org.nextprot.api.web.controller.genomic;

import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.MVCBaseIntegrationTest;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

/**
 * @author dteixeira
 */
@Ignore
public class ThingsToBeTestedGenomicMappingWebTest extends MVCBaseIntegrationTest {

	@Test
	public void shouldGetGenomicMapping() throws Exception {

		ResultActions result = this.mockMvc.perform(get("/entry/NX_A6NC05/genomic-mappings.xml"));

		String s = result.andReturn().getResponse().getContentAsString();
		//System.out.println(s);

		result.andExpect(xpath("genomicMappings/genomicMapping/isoformMappings/isoformMapping").nodeCount(2));

	}

	@Test
	public void shouldGetGenomicMappingToo() throws Exception {

		//Case where we have exon_gene mapping
		ResultActions result = this.mockMvc.perform(get("/entry/NX_O00168/genomic-mappings.xml"));

		String s = result.andReturn().getResponse().getContentAsString();
		System.out.println(s);

		result.andExpect(xpath("genomicMappings/genomicMapping/isoformMappings").nodeCount(2));

	}

	@Test
	public void shouldGetGenomicMappingTooAlso() throws Exception {

		//Case where we have exon_gene mapping
		ResultActions result = this.mockMvc.perform(get("/entry/NX_P20366/genomic-mappings.xml"));

		String s = result.andReturn().getResponse().getContentAsString();

		result.andExpect(xpath("genomicMappings/genomicMapping/isoformMappings").nodeCount(2));

	}
	
	
}
