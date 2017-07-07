package org.nextprot.api.web.xml.integration;
import org.junit.Test;
import org.mockito.internal.matchers.GreaterThan;
import org.mockito.internal.matchers.LessThan;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

public class EntryXMLIntegrationTest extends WebIntegrationBaseTest {
	
  @Test
  public void shouldContainIsoform() throws Exception {
	  this.mockMvc.perform(get("/entry/NX_P06213/isoform.xml")).andExpect(status().isOk())
	  	.andExpect(xpath("/entry/isoform-sequence-list/isoform-sequence").nodeCount(2));
  }
  
  @Test
  public void shouldContainOverview() throws Exception {
	  this.mockMvc.perform(get("/entry/NX_P03372/overview.xml")).andExpect(xpath("entry/overview").exists());
  }

  @Test
  public void shouldContainIdentifier() throws Exception {
	  this.mockMvc.perform(get("/entry/NX_P03372/identifier.xml")).andExpect(xpath("entry/identifier-list").exists());
  }

  @Test
  public void shouldContainChromosomalLocation() throws Exception {
	  this.mockMvc.perform(get("/entry/NX_P03372/chromosomal-location.xml")).andExpect(xpath("entry/chromosomal-location-list").exists());
  }


  @Test
  public void shouldContainGenomicMapping() throws Exception {
	  this.mockMvc.perform(get("/entry/NX_P03372/genomic-mapping.xml")).andExpect(xpath("entry/genomic-mapping-list").exists());
  }

  @Test
  public void shouldContainPublications() throws Exception {
	  this.mockMvc.perform(get("/entry/NX_P03372/publication.xml")).andExpect(xpath("entry/publication-list").exists());
  }

  @Test
  public void shouldContainXref() throws Exception {
	  this.mockMvc.perform(get("/entry/NX_P03372/xref.xml")).andExpect(xpath("entry/xref-list").exists());
  }

  @Test
  public void shouldContainBinaryInteractionAsAnnotation() throws Exception {
	  this.mockMvc.perform(
			  get("/entry/NX_P03372/interaction.xml")).
			  andExpect(xpath("entry/annotation-list/annotation-category[@category='binary-interaction']").exists());
  }

  @Test
  public void shouldContainSmallMoleculeInteraction() throws Exception {
	  this.mockMvc.perform(
			  get("/entry/NX_P03372/interaction.xml")).
			  andExpect(xpath("entry/annotation-list/annotation-category[@category='small-molecule-interaction']").exists());
  }

  @Test
  public void shouldContainAnnotation() throws Exception {
	  this.mockMvc.perform(get("/entry/NX_P03372/annotation.xml")).andExpect(xpath("entry/annotation-list").exists());
  }


  @Test
  public void shouldContainSecondaryStructuresAndRelatedReferences() throws Exception {
	  ResultActions r = this.mockMvc.perform(get("/entry/NX_P03372/secondary-structure.xml"));
	  r.andExpect(xpath("entry/annotation-list//@category").nodeCount(new GreaterThan<Integer>(1)));
	  r.andExpect(xpath("entry/annotation-list//@category").nodeCount(new LessThan<Integer>(5)));
  }

  @Test
  public void shouldContainHelixesAndRelatedReferences() throws Exception {
	  ResultActions r =  this.mockMvc.perform(get("/entry/NX_P03372/helix.xml"));
	  r.andExpect(xpath("entry/annotation-list//@category").string("helix"));
	  r.andExpect(xpath("entry/xref-list").exists());
  }

  @Test
  public void shouldContainExperimentalContext() throws Exception {
	  this.mockMvc.perform(get("/entry/NX_P03372/experimental-context.xml")).andExpect(xpath("entry/experimental-context-list").exists());
  }

  @Test
  public void shouldContainAntibody() throws Exception {
	  ResultActions r = this.mockMvc.perform(get("/entry/NX_P03372/antibody-mapping.xml"));
	  r.andExpect(xpath("entry/annotation-list/annotation-category/@category").string("antibody-mapping"));
      r.andExpect(xpath("entry/xref-list/xref//@database").nodeCount(2));
  }
  
  @Test
  public void shouldContainPeptide() throws Exception {
	  ResultActions r = this.mockMvc.perform(get("/entry/NX_P03372/peptide-mapping.xml"));
	  r.andExpect(xpath("entry/annotation-list/annotation-category/@category").string("peptide-mapping"));
  }

  @Test
  public void shouldContainSrmPeptide() throws Exception {
	  ResultActions r = this.mockMvc.perform(get("/entry/NX_P03372/srm-peptide-mapping.xml"));
	  r.andExpect(xpath("entry/annotation-list/annotation-category/@category").string("srm-peptide-mapping"));
  }

  /*

  @Test
  public void shouldGetChromosomalLocation() throws Exception {
    this.mockMvc.perform(get("/entry/NX_P03372/chromosomal-locations.xml"))
        .andExpect(xpath("/chromosomalLocations/chromosomalLocation//@chromosome").string("6")) 
        .andExpect(xpath("/chromosomalLocations/chromosomalLocation//@band").string("q25.1")) 
        .andExpect(xpath("/chromosomalLocations/chromosomalLocation//@strand").string("1")) 
        .andExpect(xpath("/chromosomalLocations/chromosomalLocation//@accession").string("ENSG00000091831")) 
        .andExpect(xpath("/chromosomalLocations/chromosomalLocation//@firstPosition").string("151977826")) 
        .andExpect(xpath("/chromosomalLocations/chromosomalLocation//@lastPosition").string("152450754"));        
  }
  
  @Test
  public void shouldGetIsoforms() throws Exception {
    this.mockMvc.perform(get("/entry/NX_P03372/isoforms.xml"))
        .andExpect(xpath("/isoforms/isoform//@uniqueName").string("NX_P03372-1")) 
        .andExpect(xpath("/isoforms/isoform//@swissprotDislayedIsoform").string("true")) 
        .andExpect(xpath("/isoforms/isoform/entityName//@isMain").string("true")) 
        .andExpect(xpath("/isoforms/isoform/entityName//@type").string("name")) 
        .andExpect(xpath("/isoforms/isoform/entityName//@qualifier").string("")) 
        .andExpect(xpath("/isoforms/isoform/entityName/value").string("1")) 
        .andExpect(xpath("/isoforms/isoform/entityName/synonyms/entityName//@isMain").string("false")) 
        .andExpect(xpath("/isoforms/isoform/entityName/synonyms/entityName[2]/value").string("hER-alpha66")) 
        .andExpect(xpath("/isoforms/isoform[2]//@swissprotDislayedIsoform").string("")); //should not get any swissprot display for the second isoform
  }
  
  @Test
  public void shouldGetKeywords() throws Exception {
//	  <keyword accession="KW-0479" kwName="Metal-binding" />
	  this.mockMvc.perform(get("/entry/NX_P03372/keywords.xml"))
	  	.andExpect(status().isOk())
	  	.andExpect(xpath("/keywords").exists())
	  	.andExpect(xpath("/keywords/keyword[1]//@accession").string("KW-0479"))
	  	.andExpect(xpath("/keywords/keyword[1]//@kwName").string("Metal-binding"));
  }
  
  @Test
  public void shouldGetAntibodies() throws Exception {
	  this.mockMvc.perform(get("/entry/NX_P03372/antibody.xml"))
	  	.andExpect(status().isOk())
	  	.andExpect(xpath("/antibodyList").exists())
	  	.andExpect(xpath("/antibodyList/antibody[1]//@uniqueName").string("NX_HPA000449"))
	  	.andExpect(xpath("/antibodyList/antibody[1]/url").exists())
	  	.andExpect(xpath("/antibodyList/antibody[1]/url").string(new StringContains("proteinatlas.org/search/HPA000449")))
	  	.andExpect(xpath("/antibodyList/antibody[1]/isoformSpecificity").exists())
	  	.andExpect(xpath("/antibodyList/antibody[1]/isoformSpecificity/isoformAnti[1]//@isoformRef").string("NX_P03372-2"))
	  	.andExpect(xpath("/antibodyList/antibody[1]/isoformSpecificity/isoformAnti[1]/positions").exists())
	  	.andExpect(xpath("/antibodyList/antibody[1]/isoformSpecificity/isoformAnti[1]/positions/position//@first").string("88"))
	  	.andExpect(xpath("/antibodyList/antibody[1]/isoformSpecificity/isoformAnti[1]/positions/position//@last").string("230"));
  }
  
  @Test
  public void shouldNotGetAntibodies() throws Exception {
	  this.mockMvc.perform(get("/entry/NX_P08519/antibody.xml"))
	  	.andExpect(status().isOk())
	  	.andExpect(xpath("/antibodyList").exists())
	  	.andExpect(xpath("/antibodyList/antibody[1]").doesNotExist());
  }
  
  @Test
  public void shouldGetPeptides() throws Exception {
//	  this.mockMvc.perform(get("/entry/NX_P08519/peptide.xml"))
//	  .andExpect(status().isOk())
//	  .andExpect(xpath("/peptideList").exists())
//	  .andExpect(xpath("/peptideList/peptide[1]//@uniqueName").string("NX_PEPT00262849"))
//	  .andExpect(xpath("/peptideList/peptide[1]/evidences").exists())
//	  .andExpect(xpath("/peptideList/peptide[1]/evidences/evidence[1]//@accession").string("PAp01512986"))
//	  .andExpect(xpath("/peptideList/peptide[1]/evidences/evidence[1]//@database").string("PeptideAtlas"))
//	  .andExpect(xpath("/peptideList/peptide[1]/evidences/evidence[1]//@assignedBy").string("PeptideAtlas human plasma"))
//	  .andExpect(xpath("/peptideList/peptide[1]/isoformSpecificity").exists())
//	  .andExpect(xpath("/peptideList/peptide[1]/isoformSpecificity/isoformPep[1]//@isoformRef").string("NX_P08519-1"))
//	  .andExpect(xpath("/peptideList/peptide[1]/isoformSpecificity/isoformPep[1]/positions").exists())
//	  .andExpect(xpath("/peptideList/peptide[1]/isoformSpecificity/isoformPep[1]/positions//position[1]//@first").string("1104"))
//	  .andExpect(xpath("/peptideList/peptide[1]/isoformSpecificity/isoformPep[1]/positions//position[1]//@last").string("1118"))
//	  ;
	  
	  this.mockMvc.perform(get("/entry/NX_P12345/peptide.xml"))
	  	.andExpect(status().isOk())
	  	.andExpect(xpath("/peptideList").exists())
	  	.andExpect(xpath("/peptideList/peptide[1]//@uniqueName").string("NX_PEPT12345678"))
	  	.andExpect(xpath("/peptideList/peptide[1]/evidencesPep").exists())
	  	.andExpect(xpath("/peptideList/peptide[1]/evidencesPep/evidencePep[1]//@accession").string("789654"))
	  	.andExpect(xpath("/peptideList/peptide[1]/evidencesPep/evidencePep[1]//@database").string("PubMed"))
	  	.andExpect(xpath("/peptideList/peptide[1]/evidencesPep/evidencePep[1]//@assignedBy").string("NextProt"))
	  	.andExpect(xpath("/peptideList/peptide[1]/isoformSpecificityPep").exists())
	  	.andExpect(xpath("/peptideList/peptide[1]/isoformSpecificityPep/isoformPep[1]//@isoformRef").string("NX_P12345-1"))
	  	.andExpect(xpath("/peptideList/peptide[1]/isoformSpecificity/isoformPep[1]/positions").exists())
//	  	.andExpect(xpath("/peptideList/peptide[1]/isoformSpecificity/isoformPep[1]/positions//position[1]//@first").string("1"))
//	  	.andExpect(xpath("/peptideList/peptide[1]/isoformSpecificity/isoformPep[1]/positions//position[1]//@last").string("1000"))
	  	;
  }
  
  @Test
  @Ignore
  public void shouldNotGetPeptides() throws Exception {
	  this.mockMvc.perform(get("/entry/NX_P12346/peptide.xml"))
	  	.andExpect(status().isOk())
	  	.andExpect(xpath("/peptideList").exists())
	  	.andExpect(xpath("/peptideList/peptide[1]").doesNotExist());
  }
  
  @Test
  public void shouldGetOnePositionPerPeptide() throws Exception {
	  this.mockMvc.perform(get("/entry/NX_P12345/peptide.xml"))
	  	.andExpect(status().isOk())
	  	.andExpect(xpath("/peptideList").exists())
	  	.andExpect(xpath("/peptideList/peptide[1]").exists())
	  	.andExpect(xpath("/peptideList/peptide[1]/isoformSpecificityPep").exists())
	  	.andExpect(xpath("/peptideList/peptide[1]/isoformSpecificityPep/isoformPep[1]/positions").exists())
	  	.andExpect(xpath("/peptideList/peptide[1]/isoformSpecificityPep/isoformPep[1]/positions").nodeCount(1))
	  	;
	  
	  
  }
  */

}

