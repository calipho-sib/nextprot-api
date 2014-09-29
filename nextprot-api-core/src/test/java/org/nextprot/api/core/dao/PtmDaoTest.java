package org.nextprot.api.core.dao;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.nextprot.api.core.domain.Feature;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@DatabaseSetup(value = "PtmDaoTest.xml", type = DatabaseOperation.INSERT)
public class PtmDaoTest extends CoreUnitBaseTest {

	@Autowired private PtmDao ptmDao;
	
	@Test
	public void testFindPtmsByEntry() {
		List<Feature> features = this.ptmDao.findPtmsByEntry("NP_MAS1");
		assertEquals(2, features.size());
		assertEquals("PTM-204", features.get(0).getAccession());
		assertEquals("GLC-204", features.get(1).getAccession());
	}
}
