package org.nextprot.api.core.dao;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.nextprot.api.core.domain.EntityName;
import org.nextprot.api.core.domain.Overview.EntityNameClass;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@DatabaseSetup(value = "EntityNameDaoTest.xml", type = DatabaseOperation.INSERT)
public class EntityNameDaoTest extends CoreUnitBaseTest {

	@Autowired private EntityNameDao entityNameDao;
	
	@Test
	public void testFindNames() {
		List<EntityName> names = this.entityNameDao.findNames("PAM");
		
		assertEquals(2, names.size());
		assertEquals(EntityNameClass.GENE_NAMES, names.get(0).getClazz());
		assertEquals(EntityNameClass.PROTEIN_NAMES, names.get(1).getClazz());
		assertEquals("gene name", names.get(0).getType());
		assertEquals("name", names.get(1).getType());
		assertNull(names.get(0).getQualifier());
		assertEquals("full", names.get(1).getQualifier());
	}
}
