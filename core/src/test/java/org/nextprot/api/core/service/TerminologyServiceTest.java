package org.nextprot.api.core.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.nextprot.api.core.domain.Terminology;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"dev"})
public class TerminologyServiceTest extends CoreUnitBaseTest {

	@Autowired private TerminologyService terminologyService;
	
	@Test
	public void shouldReturnAUniprotKeyword() {
		Terminology term = this.terminologyService.findTerminologyByAccession("KW-0732");
		assertEquals(term.getOntology(), "UniprotKeywordCv");
	}
	
	@Test
	public void shouldReturnTheHierarchy() {
		Terminology term = this.terminologyService.findTerminologyByAccession("KW-0906");
		for(String ancestor : term.getAncestorAccession()){
			System.out.println(ancestor);
		}
	}
}

