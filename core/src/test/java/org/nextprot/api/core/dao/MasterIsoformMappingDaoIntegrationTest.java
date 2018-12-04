package org.nextprot.api.core.dao;

import org.junit.Test;
import org.nextprot.api.core.domain.IsoformSpecificity;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.Assert.assertTrue;

@ActiveProfiles({ "dev" })
public class MasterIsoformMappingDaoIntegrationTest extends CoreUnitBaseTest {

	@Autowired
	private MasterIsoformMappingDao mimdao;
	
	@Test
	public void shouldReturn_4_Mappings() {
 		List<IsoformSpecificity> specs = mimdao.findIsoformMappingByMaster("NX_P26439");
 		assertTrue(specs.size()==4); // for each of the 2 isoform we have 2 mapping positions: 2 x 2 = 4
 		for (IsoformSpecificity spec: specs) {
			assertTrue(spec.getIsoformMainName()==null);	// is set later by service
			assertTrue(spec.getIsoformAc()!=null);
			assertTrue(spec.getPositions().size()==1);  // we expect one position item by row and thus by IsoformSpecificity
 		}
	}		
}
