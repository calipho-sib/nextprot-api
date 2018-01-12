package org.nextprot.api.core.dao;

import org.junit.Test;
import org.nextprot.api.core.domain.EntryProperties;
import org.nextprot.api.core.domain.ProteinExistence;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@ActiveProfiles("dev")
public class EntryPropertiesIntegrationDaoTest extends CoreUnitBaseTest {

	@Autowired private EntryPropertiesDao entryPropertieDao;
	
	@Test
	public void testFindEntryProperties() {
		EntryProperties props = this.entryPropertieDao.findEntryProperties("NX_P51659");
		assertEquals("Evidence at protein level", props.getProteinExistence(ProteinExistence.Source.PROTEIN_EXISTENCE_UNIPROT).getDescription());
		assertTrue(props.getPtmCount() > 20);
		assertTrue(props.getVarCount() > 100);
		assertTrue(props.getIsoformCount() > 2);
		assertTrue(props.getMaxSeqLen() > 760);
		assertTrue(props.getInteractionCount() > 17);
		assertTrue(props.getFiltermutagenesis());
		assertTrue(props.getFilterstructure());
		assertTrue(props.getFilterdisease());
		assertTrue(props.getFilterproteomics());
		assertTrue(props.getFilterexpressionprofile());
	}
}
