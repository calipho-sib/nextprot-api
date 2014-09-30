package org.nextprot.api.core.dao;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.nextprot.api.core.domain.Family;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@DatabaseSetup(value = "FamilyDaoTest.xml", type = DatabaseOperation.INSERT)
public class FamilyDaoTest extends CoreUnitBaseTest {

	@Autowired private FamilyDao familyDao;
	
	@Test
	public void testFindFamilies() {
		List<Family> families = this.familyDao.findFamilies("OLIV");
		assertEquals(1, families.size());
		assertEquals("Rothschild", families.get(0).getName());
		assertEquals("SP_007", families.get(0).getAccession());
	}
}
