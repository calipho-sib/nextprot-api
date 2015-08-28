package org.nextprot.api.core.service;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.nextprot.api.core.domain.IsoformSpecificity;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({ "dev" })
public class MasterIsoformMappingServiceIntegrationTest extends CoreUnitBaseTest {

	@Autowired
	private MasterIsoformMappingService mimService;

	@Test
	public void shouldReturn2IsoformsWith2MappingPositionsEach() {
		List<IsoformSpecificity> specs = this.mimService.findMasterIsoformMappingByEntryName("NX_P26439");
		assertTrue(specs.size()==2);
		IsoformSpecificity spec;
		spec= specs.get(0);
		assertTrue(spec.getIsoformAc().equals("NX_P26439-1"));
		assertTrue(spec.getIsoformMainName().equals("Iso 1"));
		assertTrue(spec.getPositions().size()==2);
		
		spec = specs.get(1);
		assertTrue(spec.getIsoformAc().equals("NX_P26439-2"));
		assertTrue(spec.getIsoformMainName().equals("Iso 2"));
		assertTrue(spec.getPositions().size()==2);
		
	}

	@Test
	public void shouldReturnIsoformsInProperOrder() {
		List<IsoformSpecificity> specs = this.mimService.findMasterIsoformMappingByEntryName("NX_Q8WZ42");
		int i=0;
		for (IsoformSpecificity spec: specs) {
			i++;
			assertTrue(spec.getIsoformMainName().equals("Iso " + i)); // TITIN has Iso 1, Iso 2, ... Iso 13
		}
		assertTrue(true);
	}

	@Test
	public void shouldReturnIsoformsInProperOrder2() {
		List<IsoformSpecificity> specs = this.mimService.findMasterIsoformMappingByEntryName("NX_P46976");
		assertTrue(specs.get(0).getIsoformMainName().equals("GN-1"));
		assertTrue(specs.get(1).getIsoformMainName().equals("GN-1L"));
		assertTrue(specs.get(2).getIsoformMainName().equals("GN-1S"));
	}
	
	
	@Test
	public void shouldReturnSingleIsoformWithDefaultNameIso_1() {
		List<IsoformSpecificity> specs = this.mimService.findMasterIsoformMappingByEntryName("NX_A0A087WTH1");
		assertTrue(specs.get(0).getIsoformMainName().equals("Iso 1"));
	}
	
}
