package org.nextprot.api.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
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
	public void shouldReturnUniprotFamilies() {
		List<Terminology> terms = this.terminologyService.findTerminologyByOntology("UniprotFamilyCv");
		assertTrue(terms.size() > 9700);
	}
	
	@Test
	public void shouldReturnAllTerms()  {
		int cnt = 0, refcnt = 0, maxref = 0;
		List<Terminology> terms = this.terminologyService.findAllTerminology();
		assertTrue(terms.size() > 145000); 
		for(Terminology term : terms)  {
			List<String> sameas = term.getSameAs();
			if(sameas != null) {
			cnt++;
			refcnt = sameas.size();
			if(refcnt > maxref) maxref = refcnt;
			}
		}
		assertEquals(44021,cnt);
		assertEquals(64,maxref);
	} 
	
}

