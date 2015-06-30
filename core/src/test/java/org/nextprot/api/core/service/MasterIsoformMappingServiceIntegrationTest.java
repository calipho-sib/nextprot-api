package org.nextprot.api.core.service;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;
import org.nextprot.api.core.domain.IsoformSpecificity;
import org.nextprot.api.core.service.impl.MasterIsoformMappingService;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({ "dev" })
public class MasterIsoformMappingServiceIntegrationTest extends CoreUnitBaseTest {

	@Autowired
	private MasterIsoformMappingService mimService;

	@Test
	public void shouldReturn2IsoformsWith2MappingPositionsEach() {
		Map<String,IsoformSpecificity> mappings = this.mimService.findMasterIsoformMappingByEntryName("NX_P26439");
		assertTrue(mappings.size() == 2);
		assertTrue(mappings.containsKey("NX_P26439-1"));
		assertTrue(mappings.containsKey("NX_P26439-2"));
		System.out.println(mappings.get("NX_P26439-1").getIsoformName());
		System.out.println(mappings.get("NX_P26439-2").getIsoformName());
		assertTrue(mappings.get("NX_P26439-1").getIsoformName().equals("Iso 1"));
		assertTrue(mappings.get("NX_P26439-2").getIsoformName().equals("Iso 2"));
		assertTrue(mappings.get("NX_P26439-1").getPositions().size()==2);
		assertTrue(mappings.get("NX_P26439-2").getPositions().size()==2);
	}
	
	@Test
	public void shouldReturn3IsoformsWithMainNames() {
		Map<String,IsoformSpecificity> mappings = this.mimService.findMasterIsoformMappingByEntryName("NX_P46976");
		assertTrue(mappings.size() == 3);
		assertTrue(mappings.containsKey("NX_P46976-1"));
		assertTrue(mappings.containsKey("NX_P46976-2"));
		assertTrue(mappings.containsKey("NX_P46976-3"));
		assertTrue(mappings.get("NX_P46976-1").getIsoformName().equals("GN-1L"));
		assertTrue(mappings.get("NX_P46976-2").getIsoformName().equals("GN-1"));
		assertTrue(mappings.get("NX_P46976-3").getIsoformName().equals("GN-1S"));
	}
	
}
