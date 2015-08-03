package org.nextprot.api.core.dao;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

@DatabaseSetup(value = "BioPhyChemPropsDaoTest.xml", type = DatabaseOperation.INSERT)
public class BioPhyChemPropsDaoTest extends CoreUnitBaseTest {

	@Autowired private BioPhyChemPropsDao bpcpDao;
	
	@Test
	public void testFindPropertiesByUniqueName() {
		List<AnnotationProperty> props = this.bpcpDao.findPropertiesByUniqueName("NX_P12345");
		assertEquals(2, props.size());

		assertEquals("kinetic KM", props.get(0).getName());
		assertEquals("whoever", props.get(0).getValue());
		assertEquals(501, props.get(0).getAnnotationId());

		assertEquals("absorption note", props.get(1).getName());
		assertEquals("whatever", props.get(1).getValue());
		assertEquals(500, props.get(1).getAnnotationId());

		
		props = this.bpcpDao.findPropertiesByUniqueName("NX_P54321");
		assertEquals(0, props.size());
	}
}
