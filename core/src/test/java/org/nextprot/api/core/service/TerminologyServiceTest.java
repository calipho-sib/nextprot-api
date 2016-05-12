package org.nextprot.api.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.commons.utils.Tree;
import org.nextprot.api.core.domain.Terminology;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.nextprot.api.core.utils.TerminologyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;


@ActiveProfiles({"dev"})
public class TerminologyServiceTest extends CoreUnitBaseTest {

	@Autowired private TerminologyService terminologyService;
	
	@Test
	public void testTerminologyProperties() {
		List<Terminology.TermProperty> properties = TerminologyUtils.convertToProperties("Sex of cell:=Female | Category:=Cancer cell line | Comment:=Part of: JFCR39 cancer cell lines panel. | Comment:=Part of: ENCODE project common cell types; tier 3. | Comment:=Part of: Genscript tumor cell line panel. | Comment:=Part of: NCI60 cancer cell lines panel. | Comment:=Discontinued: ICLC; HTL97004; probable. | Comment:=Omics: deep exome analysis. | Comment:=Omics: deep proteome analysis. | Comment:=Misspelling: occasionally 'OVCA3'. | Comment:=Omics: exosome analysis by proteomics. | Comment:=Omics: secretome analysis by proteomics.",(long) 99999, "CVCL_0000");
		assertEquals(properties.size(), 12);
		assertEquals(properties.get(3).gettermId(), (long)99999);
	}
		@Test
	public void shouldReturnAUniprotKeywordId() {
		Terminology term = this.terminologyService.findTerminologyByAccession("KW-0732");
	assertEquals("UniprotKeywordCv", term.getOntology());
	}
	
	@Test
	public void shouldReturnAUniprotSubcell() {
		Terminology term = this.terminologyService.findTerminologyByAccession("SL-0276");
		assertEquals("UniprotSubcellularLocationCv", term.getOntology());
	}
	
	@Test
	public void shouldReturnAUniprotDomain() {
		Terminology term = this.terminologyService.findTerminologyByAccession("DO-00031");
	assertEquals("NextprotDomainCv", term.getOntology());
	}
	
	@Test
	public void shouldReturnAGOTerm() {
		Terminology term = this.terminologyService.findTerminologyByAccession("GO:2000145");
		//System.out.println(term.toString());
		assertEquals("GoBiologicalProcessCv", term.getOntology());
		assertEquals(2, term.getSynonyms().size());
	}
	
	@Test
	public void shouldReturnACellosaurusTerm() {
		Terminology term = this.terminologyService.findTerminologyByAccession("CVCL_J530");
		//System.out.println(term.toString());
		assertEquals("CellosaurusCv", term.getOntology());
		assertEquals(5, term.getXrefs().size());
		assertEquals(1, term.getFilteredXrefs("Other").size());
		assertEquals(2, term.getFilteredXrefs("Publication databases").size());
	}
	
	@Test
	public void shouldReturnTheHierarchy() {
		Terminology term = this.terminologyService.findTerminologyByAccession("KW-0906");
		assertEquals(3, term.getAncestorAccession().size()); // Nuclear pore complex has 3 parents
	}

	@Test
	public void shouldReturnAValidCategory() {
		Terminology term = this.terminologyService.findTerminologyByAccession("DO-00861");
		String propval = "";
		for (Terminology.TermProperty property : term.getProperties()) {
			if(property.getPropertyName().equals("Feature category")) propval=property.getPropertyValue(); 
		}
		assertEquals("zinc finger", propval); 
	}

	@Test
	public void shouldReturnUniprotFamilies() {
		List<Terminology> terms = this.terminologyService.findTerminologyByOntology("UniprotFamilyCv");
		assertTrue(terms.size() > 9700);
	}
	

	@Test
	public void shoudGetAllAncestors() { 
		List<Tree<Terminology>> trees = this.terminologyService.findTerminologyTreeList(TerminologyCv.GoBiologicalProcessCv);
		assertEquals(69,this.terminologyService.getAncestorSets(trees, "GO:1902667").size());
		//assertEquals(5,TerminologyUtils.getAncestorSets(tree, "KW-0906").size());
	}
	

	@Test
	public void shoudGetAllAncestorsForNextprotDomains() { 
		List<Tree<Terminology>> trees = this.terminologyService.findTerminologyTreeList(TerminologyCv.NextprotDomainCv);
		assertEquals(4,this.terminologyService.getAncestorSets(trees, "DO-00218").size());
	}

	@Test
	public void shoudGetAllAncestorsForUnipathwayCv() { 
		List<Tree<Terminology>> trees = this.terminologyService.findTerminologyTreeList(TerminologyCv.UnipathwayCv);
		assertEquals(10,this.terminologyService.getAncestorSets(trees, "UPA00781").size());
		//assertEquals(5,TerminologyUtils.getAncestorSets(tree, "KW-0906").size());
	}
	
	
	@Test
	public void shouldReturnTerminologies() {
		for(TerminologyCv t : TerminologyCv.values()){
			if(!t.equals(TerminologyCv.CellosaurusCv)){
				this.terminologyService.findTerminologyTreeList(t);
			}
		}
	}
	
	@Test
	public void shouldReturnAllTerms()  {
		int sameascnt = 0, refcnt = 0, maxref = 0;
		List<Terminology> terms = this.terminologyService.findAllTerminology();
		assertTrue(terms.size() > 145000); 
		for(Terminology term : terms)  {
			List<String> sameas = term.getSameAs();
			if(sameas != null) {
				sameascnt++; 
			refcnt = sameas.size();
			if(refcnt > maxref) maxref = refcnt;
			}
		}
		assertTrue(sameascnt > 44000); 
		assertEquals(64,maxref);
	} 
	
}

