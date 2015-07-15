package org.nextprot.api.web.xml.integration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.springframework.test.web.servlet.ResultActions;

public class EntryOverviewXMLIntegrationTest extends WebIntegrationBaseTest {
	
  @Test
  public void shouldContainOverviewWithGeneNameList() throws Exception {

	  //Tests issue CALIPHOMISC-330 https://issues.isb-sib.ch/browse/CALIPHOMISC-330
	  
	  ResultActions actions = this.mockMvc.perform(get("/entry/NX_P38398/overview.xml"));
	  actions.andExpect(xpath("entry/overview/gene-list/gene/gene-name[@type='primary']").exists());
	  actions.andExpect(xpath("entry/overview/gene-list/gene/gene-name[@type='primary']").string("BRCA1"));
	  actions.andExpect(xpath("entry/overview/gene-list/gene/gene-name[@type='synonym']").exists());
	  actions.andExpect(xpath("entry/overview/gene-list/gene/gene-name[@type='synonym']").string("RNF53"));
  }
  
  
	
  @Test
  public void shouldContainOverviewWithGeneNameIncludingORFName() throws Exception {

	  //Tests issue CALIPHOMISC-330 https://issues.isb-sib.ch/browse/CALIPHOMISC-330
	  ResultActions actions = this.mockMvc.perform(get("/entry/NX_Q3L8U1/overview.xml"));
	  actions.andExpect(xpath("entry/overview/gene-list/gene/gene-name[@type='ORF']").exists());
  }
  
  
  @Test
  public void shouldContainOverviewWithChainsAndAlternativeNames() throws Exception {

	  //Tests issue CALIPHOMISC-322 https://issues.isb-sib.ch/browse/CALIPHOMISC-322
	  ResultActions actions = this.mockMvc.perform(get("/entry/NX_P05067/overview.xml"));
	  actions.andExpect(xpath("entry/overview/chain-list/chain/alternative-name-list/alternative-name/chain-name").exists());
  }
  
  
  @Test
  public void shouldContainOverviewWithRegionsAndAlternativeNames() throws Exception {

	  //Tests issue CALIPHOMISC-322 https://issues.isb-sib.ch/browse/CALIPHOMISC-322
	  ResultActions actions = this.mockMvc.perform(get("/entry/NX_O60513/overview.xml"));
	  actions.andExpect(xpath("entry/overview/region-list/region/alternative-name-list/alternative-name/region-name").exists());
  }



}

