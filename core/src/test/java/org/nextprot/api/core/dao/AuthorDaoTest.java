package org.nextprot.api.core.dao;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.nextprot.api.core.domain.PublicationAuthor;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

/*
 * This test requires the following view to be created in db_unit database 
 * 

CREATE TABLE "nextprot"."view_paper_scale"
(
   publication_id bigint,
   num_entries bigint,
   is_largescale int
)
;
CREATE INDEX fk1_v_pap_pubs ON "nextprot"."view_paper_scale"(publication_id)
;

*/

@DatabaseSetup(value = "AuthorDaoTest.xml", type = DatabaseOperation.INSERT)
public class AuthorDaoTest extends CoreUnitBaseTest {

	@Autowired private AuthorDao authorDao;
	
	@Test
	public void findAuthorsByPublicationId() {
		List<PublicationAuthor> authors = this.authorDao.findAuthorsByPublicationId(106L);
		assertEquals(2, authors.size());
		assertEquals("Luke", authors.get(0).getLastName());
		assertEquals("Lucky", authors.get(0).getForeName());
		assertEquals("Montana", authors.get(1).getLastName());
		assertEquals("Toni", authors.get(1).getForeName());
	}
	
	@Test
	public void findAuthorsByPublicationIds() {
		List<Long> publicationIds = new ArrayList<Long>();
		publicationIds.add(106L);
		publicationIds.add(107L);
		List<PublicationAuthor> authors = this.authorDao.findAuthorsByPublicationIds(publicationIds);
		assertEquals(3, authors.size());
		assertEquals("Luke", authors.get(0).getLastName());
		assertEquals("Lucky", authors.get(0).getForeName());
		assertEquals("Montana", authors.get(1).getLastName());
		assertEquals("Toni", authors.get(1).getForeName());
		assertEquals("Han", authors.get(2).getForeName());
		assertEquals("Solo", authors.get(2).getLastName());
	}
}
