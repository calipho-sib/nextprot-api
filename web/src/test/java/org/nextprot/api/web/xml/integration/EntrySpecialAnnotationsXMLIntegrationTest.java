package org.nextprot.api.web.xml.integration;
import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.nextprot.api.web.utils.XMLUnitUtils;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

public class EntrySpecialAnnotationsXMLIntegrationTest extends WebIntegrationBaseTest {
	
	  @Test
	  public void shouldWorkForDiseaseAnnotation() throws Exception {

		  //Tests issue CALIPHOMISC-277 https://issues.isb-sib.ch/browse/CALIPHOMISC-277
		  
	      // the disease annotation selected in this test comes with a "relative" evidence 
		  // which should be replaced with "alternative disease term" property 
		  // pointing to an xref defining the term

		  ResultActions actions = this.mockMvc.perform(get("/entry/NX_Q9NVV9/disease.xml"));
		  String xml = actions.andReturn().getResponse().getContentAsString();
		  String annotXpath = "entry//annotation-category[@category='disease']/annotation/cv-term[@accession='DI-00416']/..";
		  actions.andExpect(xpath(annotXpath).exists());
	      
		  // testing description content
		  String descr = XMLUnitUtils.getMatchingNodes(xml, annotXpath + "/description").item(0).getTextContent();
	      assertEquals(true, descr.contains("Dystonia 6"));
	      
	      // testing that any "relative" evidence was removed
	      actions.andExpect(xpath(annotXpath + "/evidence-list/evidence[@resource-assoc-type='relative']").doesNotExist());
	      
	      // testing existence of property that should replace the "relative" evidence
	      String propXpath = annotXpath + "/property-list/property[@name='alternative disease description']";
		  actions.andExpect(xpath(propXpath).exists());
		  String value = XMLUnitUtils.getMatchingNodes(xml, propXpath).item(0).getAttributes().getNamedItem("value").getNodeValue();
		  
		  // testing that the xref pointed by the property is included in the xml
		  String xrefPath = "entry/xref-list/xref[@internal-id='"+ value + "']";
		  actions.andExpect(xpath(xrefPath).exists());
	  }
	  
	  @Test
	  public void shouldWorkForCofactorAnnotation() throws Exception {

		  //Tests issue CALIPHOMISC-277 https://issues.isb-sib.ch/browse/CALIPHOMISC-277
		  
	      // the cofactor annotation selected in this test comes with a "relative" evidence 
		  // which should be replaced with "cofactor" property 
		  // pointing to an xref defining the term

		  ResultActions actions = this.mockMvc.perform(get("/entry/NX_Q9GZT9/cofactor.xml"));
		  String xml = actions.andReturn().getResponse().getContentAsString();
		  String propXpath = "entry//annotation-category[@category='cofactor']/annotation/property-list/property[@name='cofactor' and @accession='CHEBI:29033']";
		  actions.andExpect(xpath(propXpath).exists());
	      	      
	      // testing that any "relative" evidence was removed
	      actions.andExpect(xpath(propXpath + "/../../evidence-list/evidence[@resource-assoc-type='relative']").doesNotExist());
	      
	      // getting the value of the "cofactor" property
		  String value = XMLUnitUtils.getMatchingNodes(xml, propXpath).item(0).getAttributes().getNamedItem("value").getNodeValue();
		 
		  // testing that the xref pointed by the property is included in the xml
		  String xrefPath = "entry/xref-list/xref[@internal-id='"+ value + "']";
		  actions.andExpect(xpath(xrefPath).exists());
		  
	  }
	  
	  @Test
	  public void shouldWorkForSequenceCautionAnnotation() throws Exception {

		  //Tests issue CALIPHOMISC-277 https://issues.isb-sib.ch/browse/CALIPHOMISC-277
		  
	      // the "sequence caution" annotation selected in this test comes with a "relative" evidence 
		  // which should be replaced with "differing sequence" property 
		  // pointing to an xref defining the sequence

		  ResultActions actions = this.mockMvc.perform(get("/entry/NX_P38398/sequence-caution.xml"));
		  String xml = actions.andReturn().getResponse().getContentAsString();
		  String propXpath = "entry//annotation-category[@category='sequence-caution']/annotation/property-list/property[@name='differing sequence' and @accession='AAB61673']";
		  actions.andExpect(xpath(propXpath).exists());
	      	      
		  // testing the content of the description which should contain the accession declared in the property
		  String descr = XMLUnitUtils.getMatchingNodes(xml, propXpath + "/../../description").item(0).getTextContent();
	      assertEquals(true, descr.contains("AAB61673"));
		  
	      // testing that any "relative" evidence was removed
	      actions.andExpect(xpath(propXpath + "/../../evidence-list/evidence[@resource-assoc-type='relative']").doesNotExist());
	      
	      // getting the value of the "differing sequence" property
		  String value = XMLUnitUtils.getMatchingNodes(xml, propXpath).item(0).getAttributes().getNamedItem("value").getNodeValue();
		 
		  // testing that the xref pointed by the property is included in the xml
		  String xrefPath = "entry/xref-list/xref[@internal-id='"+ value + "']";
		  actions.andExpect(xpath(xrefPath).exists());
		  
	  }
	  
	  @Test
	  public void shouldWorkForBinaryInteractionAnnotation() throws Exception {

		  //Tests issue CALIPHOMISC-302 https://issues.isb-sib.ch/browse/CALIPHOMISC-302
		  
	      // the "binary-interaction" annotation selected in this test 
		  // should contain an "interactant" property
		  // pointing to an xref defining the interactant (interactant is not an antry nor an isoform contained in nextprot)

		  ResultActions actions = this.mockMvc.perform(get("/entry/NX_P03372/binary-interaction.xml"));
		  String xml = actions.andReturn().getResponse().getContentAsString();
		  String propXpath = "entry//annotation-category[@category='binary-interaction']/annotation/property-list/property[@name='interactant' and @value-type='resource-internal-ref' and @accession='Q81LD0']";
		  actions.andExpect(xpath(propXpath).exists());
	      	      	      
	      // getting the value of the "interactant" property
		  String value = XMLUnitUtils.getMatchingNodes(xml, propXpath).item(0).getAttributes().getNamedItem("value").getNodeValue();
		 
		  // testing that the xref pointed by the property is included in the xml
		  String xrefPath = "entry/xref-list/xref[@internal-id='"+ value + "']";
		  actions.andExpect(xpath(xrefPath).exists());
		  
	  }
	  
  
	


}

