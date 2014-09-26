package org.nextprot.api.core.controller;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import org.hamcrest.core.StringContains;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.core.dbunit.MVCDBUnitBaseTest;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@Ignore
@DatabaseSetup(value = {"PeptideMappingControllerTest.xml"} , type = DatabaseOperation.INSERT)
public class EntryControllerTest extends MVCDBUnitBaseTest {//MVCBaseIntegrationTest {
 
  @Test
  public void testExportEntry() throws Exception {
	  this.mockMvc.perform(get("/entry/NX_P03372.xml"))
	  	.andExpect(status().isOk())
	  	.andExpect(xpath("/neXtProtExport").exists())
	  	.andExpect(xpath("/neXtProtExport/proteins/protein//@uniqueName").string("NX_P03372"))
	  	.andExpect(xpath("/neXtProtExport/proteins/protein/annotations").exists())
	  	.andExpect(xpath("/neXtProtExport/proteins/protein/keywords").exists())
	  	.andExpect(xpath("/neXtProtExport/proteins/protein/isoforms").exists())
	  	.andExpect(xpath("/neXtProtExport/proteins/protein/chromosomalLocations").exists())
	  	.andExpect(xpath("/neXtProtExport/proteins/protein/genomicMappings").exists())
	  	.andExpect(xpath("/neXtProtExport/proteins/protein/identifiers").exists())
	  	.andExpect(xpath("/neXtProtExport/proteins/protein/publications").exists())
	  	.andExpect(xpath("/neXtProtExport/proteins/protein/xrefs").exists())
	  	;
  }
  
  @Test
  public void shouldGetOverview() throws Exception {
	  this.mockMvc.perform(get("/entry/NX_P03372/overview.xml"))
	  	.andExpect(xpath("/overview").exists())
	  	.andExpect(xpath("/overview/proteinExistence").exists())
	  	.andExpect(xpath("/overview/proteinExistence//@value").string("protein level"))
	  	.andExpect(xpath("/overview/proteinNames").exists())
	  	.andExpect(xpath("/overview/proteinNames/entityName//@isMain").string("true"))
	  	.andExpect(xpath("/overview/proteinNames/entityName//@type").string("name"))
	  	.andExpect(xpath("/overview/proteinNames/entityName//@qualifier").string("full"))
	  	.andExpect(xpath("/overview/proteinNames/entityName/value").string("Estrogen receptor"))
	  	
	  	.andExpect(xpath("/overview/proteinNames/entityName/synonyms").exists())
	  	.andExpect(xpath("/overview/proteinNames/entityName/synonyms/entityName[1]//@isMain").string("false"))
	  	.andExpect(xpath("/overview/proteinNames/entityName/synonyms/entityName[1]//@type").string("name"))
	  	.andExpect(xpath("/overview/proteinNames/entityName/synonyms/entityName[1]//@qualifier").string("full"))
	  	.andExpect(xpath("/overview/proteinNames/entityName/synonyms/entityName[1]/value").exists())
	  	.andExpect(xpath("/overview/proteinNames/entityName/synonyms/entityName[2]//@qualifier").string("full"))
	  	;
	  	
  }
  
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

}

