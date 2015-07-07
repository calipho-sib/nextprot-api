package org.nextprot.api.core.service;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.nextprot.api.core.domain.TemporaryIsoformSpecificity;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({ "dev" })
public class MasterIsoformMappingServiceIntegrationTest extends CoreUnitBaseTest {

	@Autowired
	private MasterIsoformMappingService mimService;

	@Test
	public void shouldReturn2IsoformsWith2MappingPositionsEach() {
		List<TemporaryIsoformSpecificity> specs = this.mimService.findMasterIsoformMappingByEntryName("NX_P26439");
		assertTrue(specs.size()==2);
		TemporaryIsoformSpecificity spec;
		spec= specs.get(0);
		assertTrue(spec.getIsoformAc().equals("NX_P26439-1"));
		assertTrue(spec.getIsoformName().equals("Iso 1"));
		assertTrue(spec.getPositions().size()==2);
		
		spec = specs.get(1);
		assertTrue(spec.getIsoformAc().equals("NX_P26439-2"));
		assertTrue(spec.getIsoformName().equals("Iso 2"));
		assertTrue(spec.getPositions().size()==2);
		
	}

	@Test
	public void shouldReturnIsoformsInProperOrder() {
		List<TemporaryIsoformSpecificity> specs = this.mimService.findMasterIsoformMappingByEntryName("NX_Q8WZ42");
		int i=0;
		for (TemporaryIsoformSpecificity spec: specs) {
			i++;
			assertTrue(spec.getIsoformName().equals("Iso " + i)); // TITIN has Iso 1, Iso 2, ... Iso 13
			System.out.println(spec.getIsoformAc() + " - " + spec.getIsoformName() + " - " + spec.getSortableName());
		}
		assertTrue(true);
	}

	@Test
	public void shouldReturnIsoformsInProperOrder2() {
		List<TemporaryIsoformSpecificity> specs = this.mimService.findMasterIsoformMappingByEntryName("NX_P46976");
		assertTrue(specs.get(0).getIsoformName().equals("GN-1"));
		assertTrue(specs.get(1).getIsoformName().equals("GN-1L"));
		assertTrue(specs.get(2).getIsoformName().equals("GN-1S"));
	}


	
}
