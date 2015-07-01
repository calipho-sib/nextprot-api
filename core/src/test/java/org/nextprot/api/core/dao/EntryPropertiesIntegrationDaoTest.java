package org.nextprot.api.core.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.nextprot.api.core.domain.EntryProperties;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("dev")
public class EntryPropertiesIntegrationDaoTest extends CoreUnitBaseTest {

	@Autowired private EntryPropertiesDao entryPropertieDao;
	
	@Test
	public void testFindEntryProperties() {
		EntryProperties props = this.entryPropertieDao.findEntryProperties("NX_P51659");
		assertEquals("Evidence at protein level", props.getProteinExistence());
		assertTrue(props.getPtmCount() > 20);
		assertTrue(props.getVarCount() > 100);
		assertTrue(props.getIsoformCount() > 2);
		assertTrue(props.getMaxSeqLen() > 760);
		assertTrue(props.getInteractionCount() > 17);
		assertTrue(props.getMutagenesisCount() > 12);
		assertEquals(true, props.getFiltermutagenesis());
		assertEquals(true, props.getFilterstructure());
		assertEquals(true, props.getFilterdisease());
		assertEquals(true, props.getFilterproteomics());
		assertEquals(true, props.getFilterexpressionprofile());
		//System.err.println("int: " + props.getInteractionCount());
	}

}
