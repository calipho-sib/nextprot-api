package org.nextprot.api.core.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;
import org.nextprot.api.commons.dbunit.DBUnitBaseTest;
import org.nextprot.api.core.domain.Overview.EntityName;
import org.nextprot.api.core.domain.Overview.EntityNameClass;
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
		assertEquals(EntityNameClass.GENE_NAMES, names.get(0).getClazz());
		assertEquals(EntityNameClass.PROTEIN_NAMES, names.get(1).getClazz());
		assertEquals("gene name", names.get(0).getType());
		assertEquals("name", names.get(1).getType());
		assertNull(names.get(1).getQualifier());
	}
}
