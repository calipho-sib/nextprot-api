package org.nextprot.api.core.dao;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.nextprot.api.commons.dbunit.DBUnitBaseTest;
import org.nextprot.api.commons.utils.Pair;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@DatabaseSetup(value = "BioPhyChemPropsDaoTest.xml", type = DatabaseOperation.INSERT)

public class BioPhyChemPropsDaoTest extends DBUnitBaseTest {

	@Autowired private BioPhyChemPropsDao bpcpDao;
	
	@Test
	public void testFindPropertiesByUniqueName() {
		List<Pair<String, String>> props = this.bpcpDao.findPropertiesByUniqueName("NX_P12345");
		assertEquals(2, props.size());
		assertEquals("absorption note", props.get(0).getFirst());
		assertEquals("whatever", props.get(0).getSecond());
		assertEquals("kinetic KM", props.get(1).getFirst());
		assertEquals("whoever", props.get(1).getSecond());
		
		props = this.bpcpDao.findPropertiesByUniqueName("NX_P54321");
		assertEquals(0, props.size());
	}
}
