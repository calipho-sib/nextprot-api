package org.nextprot.api.core.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.nextprot.api.commons.dbunit.DBUnitBaseTest;
import org.nextprot.api.commons.utils.Pair;
import org.nextprot.api.core.dao.AntibodyMappingDao;
import org.nextprot.api.core.domain.AntibodyMapping;
import org.nextprot.api.core.domain.IsoformSpecificity;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@DatabaseSetup(value = "AntibodyMappingDaoTest.xml", type = DatabaseOperation.INSERT)
public class AntibodyMappingDaoTest extends DBUnitBaseTest {
	
	@Autowired private AntibodyMappingDao antibodyMappingDao;
	
	@Test
	public void testFindAntibodiesById() {
		List<AntibodyMapping> mappings = this.antibodyMappingDao.findAntibodiesById(596889L);
		assertEquals(1, mappings.size());
		AntibodyMapping mapping = mappings.get(0);
		//System.out.println("--------------------------------");
		//System.out.println(mapping.toString());
		//System.out.println("--------------------------------");
		assertEquals("NX_HPA004932", mapping.getAntibodyUniqueName());
		//System.out.println("specificity size: "+ mapping.getIsoformSpecificity().size());
		assertEquals(1, mapping.getIsoformSpecificity().size());
		IsoformSpecificity isospec = mapping.getIsoformSpecificity().values().iterator().next();
		//System.out.println("done isospec:" + isospec);
		List<Pair<Integer, Integer>> positions = isospec.getPositions();
		//System.out.println("done get positions:" + positions);
		//System.out.println("positions size: "+ positions.size());
		assertEquals(1, positions.size());
		//System.out.println("position first: "+ positions.get(0).getFirst());
		assertTrue(1 == positions.get(0).getFirst());
		//System.out.println("position second: "+ positions.get(0).getSecond());
		assertTrue(1000 == positions.get(0).getSecond());
	}

}
