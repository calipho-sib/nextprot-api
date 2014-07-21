package org.nextprot.api.dao;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.nextprot.api.dbunit.DBUnitBaseTest;
import org.nextprot.api.domain.Publication;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@DatabaseSetup(value = "PublicationDaoTest.xml", type = DatabaseOperation.INSERT)
public class PublicationDaoTest extends DBUnitBaseTest {

	@Autowired private PublicationDao publicationDao;
	
	@Test
	public void testFindSortedPublicationsByMasterId() {
		List<Publication> publications = this.publicationDao.findSortedPublicationsByMasterId(100L);
		assertEquals(6, publications.size());
	}

}
