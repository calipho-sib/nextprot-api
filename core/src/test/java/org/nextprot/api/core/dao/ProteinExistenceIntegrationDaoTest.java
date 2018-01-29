package org.nextprot.api.core.dao;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.ProteinExistence;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("dev")
public class ProteinExistenceIntegrationDaoTest extends CoreUnitBaseTest {

	@Autowired private ProteinExistenceDao proteinExistenceDao;

	@Test
	public void testFindEntryProperties() {
		Assert.assertEquals("Evidence at protein level",
				proteinExistenceDao.findProteinExistenceUniprot("NX_P51659", ProteinExistence.Source.PROTEIN_EXISTENCE_UNIPROT).getDescription());
	}
}
