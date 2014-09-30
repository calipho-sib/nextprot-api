package org.nextprot.api.core.dao;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.nextprot.api.core.domain.Overview.History;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@DatabaseSetup(value = "HistoryDaoTest.xml", type = DatabaseOperation.INSERT)
public class HistoryDaoTest extends CoreUnitBaseTest {

	@Autowired private HistoryDao historyDao;
	
	@Test
	public void testFindHistoryEntry() {
		List<History> histories = this.historyDao.findHistoryByEntry("PAM");
		assertEquals(1, histories.size());
		
		History history = histories.get(0);
		assertEquals("Evidence_at_protein_level", history.getProteinExistence());
		assertEquals("2010-03-01", history.getFormattedNextprotIntegrationDate());
		assertEquals("2013-06-15", history.getFormattedNextprotUpdateDate());
		assertEquals("2006-10-31", history.getFormattedUniprotIntegrationDate());
		assertEquals("2013-04-03", history.getFormattedUniprotUpdateDate());
		assertEquals("1", history.getSequenceVersion());
	}
}
