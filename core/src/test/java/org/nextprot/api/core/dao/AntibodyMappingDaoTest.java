package org.nextprot.api.core.dao;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.nextprot.api.core.domain.AntibodyMapping;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@DatabaseSetup(value = "AntibodyMappingDaoTest.xml", type = DatabaseOperation.INSERT)
@TransactionConfiguration(defaultRollback = true)
public class AntibodyMappingDaoTest extends CoreUnitBaseTest {
	
	@Autowired private AntibodyMappingDao antibodyMappingDao;
	
	@Test
	public void testFindAntibodiesById() {
		List<AntibodyMapping> mappings = this.antibodyMappingDao.findAntibodiesById(596889L);
		assertEquals(1, mappings.size());
		AntibodyMapping mapping = mappings.get(0);
		assertEquals("NX_HPA004932", mapping.getAntibodyUniqueName());
		assertEquals(1, mapping.getIsoformSpecificity().size());
		AnnotationIsoformSpecificity isospec = mapping.getIsoformSpecificity().values().iterator().next();
		assertTrue(2 == isospec.getFirstPosition());
		assertTrue(1000 == isospec.getLastPosition());
	}

}
