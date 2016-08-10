package org.nextprot.api.core.service;

import org.junit.Test;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.commons.utils.Tree;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.Terminology;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.nextprot.api.core.utils.TerminologyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@ActiveProfiles({"dev"})
public class TerminologyServiceTest extends CoreUnitBaseTest {

	@Autowired private TerminologyService terminologyService;
	
	@Test
	public void testTerminologyProperties() {
		List<CvTerm.TermProperty> properties = TerminologyUtils.convertToProperties("Sex of cell:=Female | Category:=Cancer cell line | Comment:=Part of: JFCR39 cancer cell lines panel. | Comment:=Part of: ENCODE project common cell types; tier 3. | Comment:=Part of: Genscript tumor cell line panel. | Comment:=Part of: NCI60 cancer cell lines panel. | Comment:=Discontinued: ICLC; HTL97004; probable. | Comment:=Omics: deep exome analysis. | Comment:=Omics: deep proteome analysis. | Comment:=Misspelling: occasionally 'OVCA3'. | Comment:=Omics: exosome analysis by proteomics. | Comment:=Omics: secretome analysis by proteomics.",(long) 99999, "CVCL_0000");
		assertEquals(properties.size(), 12);
		assertEquals(properties.get(3).gettermId(), (long)99999);
	}
		@Test
	public void shouldReturnAUniprotKeywordId() {
		CvTerm term = this.terminologyService.findCvTermByAccession("KW-0732");
	assertEquals("UniprotKeywordCv", term.getOntology());
	}
	
	@Test
	public void shouldReturnAUniprotSubcell() {
		CvTerm term = this.terminologyService.findCvTermByAccession("SL-0276");
		assertEquals("UniprotSubcellularLocationCv", term.getOntology());
	}
	
	@Test
	public void shouldReturnAUniprotDomain() {
		CvTerm term = this.terminologyService.findCvTermByAccession("DO-00031");
	assertEquals("NextprotDomainCv", term.getOntology());
	}
	
	@Test
	public void shouldReturnAGOTerm() {
		CvTerm term = this.terminologyService.findCvTermByAccession("GO:2000145");
		//System.out.println(term.toString());
		assertEquals("GoBiologicalProcessCv", term.getOntology());
		assertEquals(2, term.getSynonyms().size());
	}
	
	@Test
	public void shouldReturnACellosaurusTerm() {
		CvTerm term = this.terminologyService.findCvTermByAccession("CVCL_J530");
		assertEquals("NextprotCellosaurusCv", term.getOntology());
		assertEquals(5, term.getXrefs().size());
		assertEquals(1, term.getFilteredXrefs("Other").size());
		assertEquals(2, term.getFilteredXrefs("Publication databases").size());
	}
	
	@Test
	public void shouldReturnTheHierarchy() {
		CvTerm term = this.terminologyService.findCvTermByAccession("KW-0906");
		assertEquals(3, term.getAncestorAccession().size()); // Nuclear pore complex has 3 parents
	}

	@Test
	public void shouldReturnAValidCategory() {
		CvTerm term = this.terminologyService.findCvTermByAccession("DO-00861");
		String propval = "";
		for (CvTerm.TermProperty property : term.getProperties()) {
			if(property.getPropertyName().equals("Feature category")) propval=property.getPropertyValue(); 
		}
		assertEquals("zinc finger", propval); 
	}

	@Test
	public void shouldReturnUniprotFamilies() {
		List<CvTerm> terms = this.terminologyService.findCvTermsByOntology("UniprotFamilyCv");
		assertTrue(terms.size() > 9700);
	}
	

	@Test
	public void shoudGetAllAncestors() { 
		List<Tree<CvTerm>> trees = this.terminologyService.findTerminology(TerminologyCv.GoBiologicalProcessCv);
		assertEquals(67,this.terminologyService.getAncestorSets(trees, "GO:1902667").size());
		//assertEquals(5,TerminologyUtils.getAncestorSets(tree, "KW-0906").size());
	}
	

	@Test
	public void shouldNotGetAnyAncestorForNextprotDomain() { // This is a particular case, because nextprot domains are attached to annotation cv ontology 
		Terminology terminology = this.terminologyService.findTerminology(TerminologyCv.NextprotDomainCv);
		assertTrue(terminology.getRootsCount() > 800); // all domains are roots (no hierarchy) and the super parent is the annotation CVAN_0106
		assertEquals(0,this.terminologyService.getAncestorSets(terminology, "DO-00218").size());
	}
	
	
	@Test
	public void shoudGetAllAncestorsForAChildOfCvan() { 
		Terminology terminology = this.terminologyService.findTerminology(TerminologyCv.NextprotAnnotationCv);
		assertEquals(3,this.terminologyService.getAncestorSets(terminology, "CVAN_0106").size());
	}

	@Test
	public void shoudGetAllAncestorsForUnipathwayCv() { 
		List<Tree<CvTerm>> trees = this.terminologyService.findTerminology(TerminologyCv.UnipathwayCv);
		assertEquals(10,this.terminologyService.getAncestorSets(trees, "UPA00781").size());
		//assertEquals(5,TerminologyUtils.getAncestorSets(tree, "KW-0906").size());
	}
	
	
	@Test
	public void shouldReturnTerminologies() {
		for(TerminologyCv t : TerminologyCv.values()) {
			if(!t.equals(TerminologyCv.NextprotCellosaurusCv)) {
				this.terminologyService.findTerminology(t);
			}
		}
	}

	//@Test
	public void shouldReturnAllTerms()  {
		int sameascnt = 0, refcnt = 0, maxref = 0;
		List<CvTerm> terms = this.terminologyService.findAllCVTerms();
		assertTrue(terms.size() > 145000); 
		for(CvTerm term : terms)  {
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

