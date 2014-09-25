package org.nextprot.api.core.dao;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.nextprot.api.commons.dbunit.DBUnitBaseTest;
import org.nextprot.api.core.dao.AuthorDao;
import org.nextprot.api.core.domain.PublicationAuthor;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@DatabaseSetup(value = "AuthorDaoTest.xml", type = DatabaseOperation.INSERT)
public class AuthorDaoTest extends DBUnitBaseTest {

	@Autowired private AuthorDao authorDao;
	
	@Test
	public void findAuthorsByPublicationId() {
		List<PublicationAuthor> authors = this.authorDao.findAuthorsByPublicationId(106L);
		assertEquals(2, authors.size());
		assertEquals("Luke", authors.get(1).getLastName());
		assertEquals("Toni", authors.get(0).getForeName());
		assertEquals("Montana", authors.get(0).getLastName());
	}
	
	@Test
	public void findAuthorsByPublicationIds() {
		List<Long> publicationIds = new ArrayList<Long>();
		publicationIds.add(106L);
		publicationIds.add(107L);
		List<PublicationAuthor> authors = this.authorDao.findAuthorsByPublicationIds(publicationIds);
		assertEquals(3, authors.size());
		assertEquals("Luke", authors.get(0).getLastName());
		assertEquals("Toni", authors.get(1).getForeName());
		assertEquals("Montana", authors.get(1).getLastName());
		assertEquals("Han", authors.get(2).getForeName());
		assertEquals("Solo", authors.get(2).getLastName());
	}
}
