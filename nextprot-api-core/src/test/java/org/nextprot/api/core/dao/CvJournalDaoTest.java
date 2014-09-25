package org.nextprot.api.core.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.nextprot.api.commons.dbunit.DBUnitBaseTest;
import org.nextprot.api.core.domain.CvJournal;
import org.nextprot.api.core.domain.PublicationCvJournal;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@DatabaseSetup(value = "CvJournalDaoTest.xml", type = DatabaseOperation.INSERT)
public class CvJournalDaoTest extends DBUnitBaseTest {

	@Autowired private CvJournalDao cvJournalDao;
	
	@Test
	public void testFindByPublicationId() {
		List<CvJournal> cvJournals = this.cvJournalDao.findByPublicationId(101L);
		assertEquals(1, cvJournals.size());
		assertEquals("Pretty Revue of Science", cvJournals.get(0).getName());
		assertTrue(100L == cvJournals.get(0).getJournalId());
	}
	
	@Test
	public void testFindByPublicationIds() {
		List<Long> ids = new ArrayList<Long>();
		ids.add(101L);
		ids.add(102L);
		ids.add(103L);
		List<PublicationCvJournal> cvJournals = this.cvJournalDao.findCvJournalsByPublicationIds(ids);
		assertEquals(3, cvJournals.size());
		assertTrue(101L == cvJournals.get(0).getPublicationId());
		assertTrue(102L == cvJournals.get(1).getPublicationId());
		assertTrue(103L == cvJournals.get(2).getPublicationId());
	}
	
	@Test
	public void testFindById() {
		CvJournal cvJournal = this.cvJournalDao.findById(101L);
		assertEquals("Revue of Science", cvJournal.getName());
	}
}
