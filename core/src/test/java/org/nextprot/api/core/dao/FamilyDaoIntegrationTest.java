package org.nextprot.api.core.dao;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.nextprot.api.core.domain.Family;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({ "dev" })
public class FamilyDaoIntegrationTest extends CoreUnitBaseTest {

	@Autowired
	private FamilyDao familyDao;
	
	@Test
	public void shouldReturn_2_Families() {
 		List<Family> families = familyDao.findFamilies("NX_Q3SY69");
 		assertTrue(families.size()==2);
 		for (Family fam: families) {
 			assertTrue(fam.getAccession()!=null);
 			assertTrue(fam.getDescription()!=null);
 			assertTrue(fam.getName()!=null);
 			assertTrue(fam.getRegion()!=null); // may be null for some other entries
 			assertTrue(fam.getFamilyId()>0);
 			assertTrue(fam.getParent()==null); // the parent (if any) is set later in the family service
 		}
	}	

	
	@Test
	public void shouldReturn_1_Parent_Family() {
 		List<Family> families = familyDao.findFamilies("NX_Q3SY69");
 		Long familyId = families.get(0).getFamilyId();
 		Family fam = familyDao.findParentOfFamilyId(familyId);
		assertTrue(fam.getAccession()!=null);
		assertTrue(fam.getDescription()==null); // always null (region comes from annotation)
		assertTrue(fam.getName()!=null);
		assertTrue(fam.getRegion()==null);      // always null (region comes from annotation)
		assertTrue(fam.getFamilyId()>0);
		assertTrue(fam.getParent()==null);      // the parent (if any) is set later in the family service
	}
	@Test
	
	
	public void shouldReturn_No_Parent_Family() {
 		Long familyId = 872345298725L;  // random id
 		Family fam = familyDao.findParentOfFamilyId(familyId);
		assertTrue(fam==null);
	}
}
