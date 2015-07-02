package org.nextprot.api.core.dao;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.nextprot.api.core.domain.TemporaryIsoformSpecificity;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({ "dev" })
public class MasterIsoformMappingDaoIntegrationTest extends CoreUnitBaseTest {

	@Autowired
	private MasterIsoformMappingDao mimdao;
	
	@Test
	public void shouldReturn_4_Mappings() {
 		List<TemporaryIsoformSpecificity> specs = mimdao.findIsoformMappingByMaster("NX_P26439");
 		assertTrue(specs.size()==4); // for each of the 2 isoform we have 2 mapping positions: 2 x 2 = 4
 		for (TemporaryIsoformSpecificity spec: specs) {
			assertTrue(spec.getIsoformName()==null);	// is set later by service
			assertTrue(spec.getIsoformAc()!=null);
			assertTrue(spec.getPositions().size()==1);  // we expect one position item by row and thus by IsoformSpecificity
 			System.out.println(spec.toString());
 		}
	}	
	
}
