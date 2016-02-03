package org.nextprot.api.core.service;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.nextprot.api.commons.dao.MasterIdentifierDao;
import org.nextprot.api.core.dao.AuthorDao;
import org.nextprot.api.core.dao.CvJournalDao;
import org.nextprot.api.core.dao.DbXrefDao;
import org.nextprot.api.core.dao.PublicationDao;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.PublicationAuthor;
import org.nextprot.api.core.service.impl.PublicationServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.*;

//@DatabaseSetup(value = "PublicationServiceTest.xml", type = DatabaseOperation.INSERT)
public class PublicationServiceTest {

	@InjectMocks
	private PublicationService publicationService = new PublicationServiceImpl();

	@Mock
	private MasterIdentifierDao masterIdentifierDao;

	@Mock
	private PublicationDao publicationDao;

	@Mock
	private AuthorDao authorDao;

	@Mock
	private DbXrefDao dbXrefDao;

	@Mock
	private DbXrefService dbXrefService;

	@Mock
	private CvJournalDao cvJournalDao;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void verifyFindPublicationById() {

		Publication publication = mock(Publication.class);
		when(publication.getPublicationId()).thenReturn(1L);

		when(publicationDao.findPublicationById(100L)).thenReturn(publication);

		publicationService.findPublicationById(100L);

		verify(publicationDao).findPublicationById(100L);
		verify(authorDao).findAuthorsByPublicationId(1L);
		verify(dbXrefDao).findDbXRefsByPublicationId(1L);
		verify(cvJournalDao).findByPublicationId(1L);
	}

	@Ignore
	@Test
	public void testFindPublicationById() {
		Publication publication = this.publicationService.findPublicationById(100L);
		assertEquals(100L, publication.getPublicationId());
		assertEquals("an interesting paper", publication.getTitle());
	}

	@Test
	public void verifyFindPublicationsByMasterId() {

		Publication publication = mock(Publication.class);
		when(publication.getPublicationId()).thenReturn(1L);

		Publication publication2 = mock(Publication.class);
		when(publication.getPublicationId()).thenReturn(2L);

		when(publicationDao.findSortedPublicationsByMasterId(100L)).thenReturn(Arrays.asList(publication, publication2));

		publicationService.findPublicationsByMasterId(100L);

		verify(publicationDao).findSortedPublicationsByMasterId(100L);
		verify(authorDao, times(2)).findAuthorsByPublicationId(anyLong());
		verify(dbXrefDao, times(2)).findDbXRefsByPublicationId(anyLong());
		verify(cvJournalDao, times(2)).findByPublicationId(anyLong());
	}

	@Ignore
	@Test
	public void testFindPublicationByMasterId() {
		List<Publication> publications = this.publicationService.findPublicationsByMasterId(100L);
		assertEquals(2, publications.size());
		publications = this.publicationService.findPublicationsByMasterId(99L);
		assertEquals(0, publications.size());
	}

	@Test
	public void verifyFindPublicationsByMasterUniqueName() {

		Publication publication = mock(Publication.class);
		when(publication.getPublicationId()).thenReturn(1L);
		when(masterIdentifierDao.findIdByUniqueName("NX_P12345")).thenReturn(100L);

		when(publicationDao.findSortedPublicationsByMasterId(100L)).thenReturn(Arrays.asList(publication));

		publicationService.findPublicationsByMasterUniqueName("NX_P12345");

		verify(masterIdentifierDao).findIdByUniqueName("NX_P12345");
		verify(publicationDao).findSortedPublicationsByMasterId(100L);

		verify(dbXrefService).findDbXRefByPublicationIds(Arrays.asList(1L));
		verify(authorDao).findAuthorsByPublicationIds(Arrays.asList(1L));
		verify(cvJournalDao).findCvJournalsByPublicationIds(Arrays.asList(1L));
	}

	@Ignore
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
		assertEquals("Pretty Revue of Science", publications.get(0).getJournal().getName());
		assertEquals("Revue of Science", publications.get(1).getJournal().getName());
		
	}
}
