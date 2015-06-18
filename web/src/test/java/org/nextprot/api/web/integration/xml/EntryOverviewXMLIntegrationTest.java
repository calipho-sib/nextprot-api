package org.nextprot.api.web.integration.xml;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.ResultActions;

@ActiveProfiles("dev")
public class EntryOverviewXMLIntegrationTest extends WebIntegrationBaseTest {
	
  @Test
  public void shouldContainOverviewWithGeneNameList() throws Exception {

	  //Tests issue CALIPHOMISC-330 https://issues.isb-sib.ch/browse/CALIPHOMISC-330
	  
	  ResultActions actions = this.mockMvc.perform(get("/entry/NX_P38398/overview.xml"));
	  actions.andExpect(xpath("entry/overview/gene-name-list/gene-name[@type='recommended']").exists());
	  actions.andExpect(xpath("entry/overview/gene-name-list/gene-name[@type='recommended']").string("BRCA1"));
	  actions.andExpect(xpath("entry/overview/gene-name-list/gene-name[@type='alternative']").exists());
	  actions.andExpect(xpath("entry/overview/gene-name-list/gene-name[@type='alternative']").string("RNF53"));
  }


}

