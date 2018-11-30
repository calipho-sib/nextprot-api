package org.nextprot.api.core.dao;

import org.junit.Test;
import org.nextprot.api.core.domain.Family;
import org.nextprot.api.core.service.FamilyService;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@ActiveProfiles({ "dev" })
public class FamilyServiceIntegrationTest extends CoreUnitBaseTest {

	@Autowired
	private FamilyService familyService;
	
	@Test
	public void shouldReturn_2_Families_1_Family_Having_1_Parent() {
 		List<Family> families = familyService.findFamilies("NX_Q3SY69");
 		assertTrue(families.size()==2);
 		for (Family fam: families) {
 			assertTrue(fam.getAccession()!=null);
 			assertTrue(fam.getDescription()!=null);
 			assertTrue(fam.getName()!=null);
 			assertTrue(fam.getRegion()!=null); 					// may be null for some other entries
 			assertTrue(fam.getFamilyId()>0);
 			if (fam.getName().startsWith("GART")) {			 	// this family has no parent
 				assertTrue(fam.getParent()==null);
 			} else if (fam.getName().startsWith("ALDH1L")) {   	// this family has 1 parent
 				assertTrue(fam.getParent()!=null);
 			} else {
 				assertTrue(false);								// this should NOT occur
 			}
 		}
	}

	@Test
	public void shouldReturn_1_Family_Having_2_Parents() {
		// -- examples of entries with 1 family having 2 ancestors: NX_O14678  NX_P28288  NX_P33897
 		List<Family> families = familyService.findFamilies("NX_O14678");
 		assertTrue(families.size()==1);
 		Family fam = families.get(0);
 		assertTrue(fam.getParent()!=null);
 		assertTrue(fam.getParent().getParent()!=null);
 		assertTrue(fam.getParent().getParent().getParent()==null);
	}

	@Test
	public void shouldBeSortedByRegion() {

		List<Family> families = familyService.findFamilies("NX_Q3SY69");

		assertEquals(2, families.size());

		assertEquals("In the N-terminal section", families.get(0).getRegion());
		assertEquals("In the C-terminal section", families.get(1).getRegion());
	}
}
