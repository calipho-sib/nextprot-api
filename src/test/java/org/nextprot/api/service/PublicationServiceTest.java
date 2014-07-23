package org.nextprot.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.List;
import java.util.SortedSet;

import org.junit.Test;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.PublicationAuthor;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.dbunit.DBUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@DatabaseSetup(value = "PublicationServiceTest.xml", type = DatabaseOperation.INSERT)
public class PublicationServiceTest extends DBUnitBaseTest {

	@Autowired private PublicationService publicationService;
	
	@Test
	public void testFindPublicationById() {
		Publication publication = this.publicationService.findPublicationById(100L);
		assertEquals(100L, publication.getPublicationId());
		assertEquals("an interesting paper", publication.getTitle());
	}
	
	@Test
	public void testFindPublicationByMasterId() {
		List<Publication> publications = this.publicationService.findPublicationsByMasterId(100L);
		assertEquals(2, publications.size());
		publications = this.publicationService.findPublicationsByMasterId(99L);
		assertEquals(0, publications.size());
	}
	
	
	@Test
	public void testFindPublicationsByMasterUniqueName() {
		List<Publication> publications = this.publicationService.findPublicationsByMasterUniqueName("NX_P12345");
		
		assertEquals(2, publications.size());
		
		// Authors
		assertEquals("an interesting paper", publications.get(0).getTitle());
		SortedSet<PublicationAuthor> authors = publications.get(0).getAuthors();
		assertEquals("Luke", authors.first().getLastName());
		assertEquals("Kent", authors.last().getLastName());
		assertNotEquals("Lightyear", authors.first().getLastName());
		
		assertEquals("an AWESOME paper", publications.get(1).getTitle());
		assertEquals(1, publications.get(1).getAuthors().size());
		assertEquals("Lightyear", publications.get(1).getAuthors().first().getLastName());
		
		// Xrefs
		assertEquals(1, publications.get(0).getDbXrefs().size());
		assertEquals(2, publications.get(1).getDbXrefs().size());
		
		// Journals
		assertEquals("Pretty Revue of Science", publications.get(0).getCvJournal().getName());
		assertEquals("Revue of Science", publications.get(1).getCvJournal().getName());
		
	}
}
