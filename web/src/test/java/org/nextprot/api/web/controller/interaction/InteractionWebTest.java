package org.nextprot.api.web.controller.interaction;

import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.MVCDBUnitBaseTest;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

/**
 * Testing the template for interactions
 * @author dteixeira
 */
@Ignore //TOOD create a mock object
public class InteractionWebTest extends MVCDBUnitBaseTest {

	@Test
	public void shouldGetNormalXMLForInteraction() throws Exception {

		ResultActions result = this.mockMvc.perform(get("/entry/NX_P51813/interaction.xml"));
//
//		String s = result.andReturn().getResponse().getContentAsString();
//		System.out.println(s);

		result.andExpect(xpath("interaction-list/interaction[@qualityQualifier='SILVER']").nodeCount(4));
		result.andExpect(xpath("interaction-list/interaction[@qualityQualifier='GOLD']").nodeCount(1));
		result.andExpect(xpath("interaction-list/interaction[@qualityQualifier='GOLD']/interactionXref/url").string("http://www.ebi.ac.uk/intact/search/do/search?binary=EBI-696621,EBI-696657"));
		result.andExpect(xpath("interaction-list/interaction[@qualityQualifier='GOLD']/interactionXref/@database").string("IntAct"));
		result.andExpect(xpath("interaction-list/interaction[@qualityQualifier='GOLD']/interactionXref/@accession").string("EBI-696621,EBI-696657"));
		result.andExpect(xpath("interaction-list/interaction[@qualityQualifier='GOLD']/interactant/@uniqueName").string("NX_P51813"));
		result.andExpect(xpath("interaction-list/interaction[@qualityQualifier='GOLD']/interactant").nodeCount(2));
		result.andExpect(xpath("interaction-list/interaction[@qualityQualifier='GOLD']/interactant/@uniqueName['NX_P51813']").exists());
		result.andExpect(xpath("interaction-list/interaction[@qualityQualifier='GOLD']/interactant/@uniqueName['NX_P11309']").exists());
		result.andExpect(xpath("interaction-list/interaction[@qualityQualifier='GOLD']/experiment-count").string("4"));
		//Test case where there is a self interaction
	}
	
	

	@Test
	public void shouldGetXMLForSelfInteraction() throws Exception {

		ResultActions result = this.mockMvc.perform(get("/entry/NX_Q9NRR5/interaction.xml"));

		String s = result.andReturn().getResponse().getContentAsString();

		//Verify the ac is correct
		result.andExpect(xpath("interaction-list/interaction[@qualityQualifier='GOLD']/interactionXref/@accession['EBI-711226,EBI-711226']").exists());
		//When it is a self interactant the link is a bit different
		result.andExpect(xpath("interaction-list/interaction/interactionXref/url").string("http://www.ebi.ac.uk/intact/pages/interactions/interactions.xhtml?query=idA:Q9NRR5%20AND%20idB:Q9NRR5"));
	
		//Theres should be only one interactant (self interactant)
		result.andExpect(xpath("interaction-list/interaction/interactant").nodeCount(1));
	}

}
