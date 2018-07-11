package org.nextprot.api.core.dao;

import org.junit.Test;
import org.nextprot.api.core.domain.EntryProperties;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.Assert.assertTrue;

@ActiveProfiles("dev")
public class EntryPropertiesIntegrationDaoTest extends CoreUnitBaseTest {

	@Autowired private EntryPropertiesDao entryPropertieDao;

	@Test
	public void testFindEntryProperties() {
		EntryProperties props = this.entryPropertieDao.findEntryProperties("NX_P51659");
		assertTrue(props.getMaxSeqLen() > 760);
		assertTrue(props.getInteractionCount() > 17);
		assertTrue(props.getFilterstructure());
		assertTrue(props.getFilterdisease());
		assertTrue(props.getFilterexpressionprofile());
	}
}
