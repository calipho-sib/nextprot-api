package org.nextprot.api.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;
import org.nextprot.api.dbunit.DBUnitBaseTest;
import org.nextprot.api.domain.Overview.EntityName;
import org.nextprot.api.domain.Overview.EntityNameClass;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@DatabaseSetup(value = "EntityNameDaoTest.xml", type = DatabaseOperation.INSERT)
public class EntityNameDaoTest extends DBUnitBaseTest {

	@Autowired private EntityNameDao entityNameDao;
	
	@Test
	public void testFindNames() {
		List<EntityName> names = this.entityNameDao.findNames("PAM");
		assertEquals(2, names.size());
		assertEquals(EntityNameClass.PROTEIN_NAMES, names.get(0).getClazz());
		assertEquals(EntityNameClass.GENE_NAMES, names.get(1).getClazz());
		assertEquals("name", names.get(0).getType());
		assertEquals("gene name", names.get(1).getType());
		assertNull(names.get(0).getQualifier());
	}
}
