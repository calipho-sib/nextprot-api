package org.nextprot.api.core.service;

import org.nextprot.api.core.dao.StatementDao;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.nextprot.api.core.dao.PublicationDao;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.PublicationAuthor;
import org.nextprot.api.core.domain.publication.EntryPublication;
import org.nextprot.api.core.service.impl.PublicationServiceImpl;

import java.util.ArrayList;
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
	private MasterIdentifierService masterIdentifierService;

	@Mock
	private PublicationDao publicationDao;

	@Mock
	private AuthorService authorService;

	@Mock
	private DbXrefService dbXrefService;

	@Mock
	private StatementDao statementDao; 	// injected in PublicationServiceImpl

	@Mock
	private List<EntryPublication> eps;

	@Before
	public void init() {

		MockitoAnnotations.initMocks(this);

		eps = new ArrayList<>();

		EntryPublication ep1 = new EntryPublication(
				"NX_P02675",
				50174690
		);

		EntryPublication ep2 = new EntryPublication(
				"NX_O75912",
				50175394
		);

		EntryPublication ep3 = new EntryPublication(
				"NX_P08246",
				51283737
		);

		EntryPublication ep4 = new EntryPublication(
				"NX_P02462",
				51283731
		);

		eps.add(ep1);
		eps.add(ep2);
		eps.add(ep3);
		eps.add(ep4);

	}

	@Test
	public void verifyFindPublicationById() {

		Publication publication = mock(Publication.class);
		when(publication.getPublicationId()).thenReturn(1L);

		when(publicationDao.findPublicationById(100L)).thenReturn(publication);

		publicationService.findPublicationById(100L);

		verify(publicationDao).findPublicationById(100L);
		verify(authorService).findAuthorsByPublicationId(1L);
		verify(dbXrefService).findDbXRefByPublicationId(1L);
	}

	@Ignore
	@Test
	public void testFindPublicationById() {
		Publication publication = this.publicationService.findPublicationById(100L);
		assertEquals(100L, publication.getPublicationId());
		assertEquals("an interesting paper", publication.getTitle());
	}

	@Test
	public void verifyFindPublicationsByMasterUniqueName() {

		Publication publication = mock(Publication.class);
		when(publication.getPublicationId()).thenReturn(1L);
		when(masterIdentifierService.findIdByUniqueName("NX_P12345")).thenReturn(100L);

		when(publicationDao.findSortedPublicationsByMasterId(100L)).thenReturn(Arrays.asList(publication));

		publicationService.findPublicationsByEntryName("NX_P12345");

		verify(masterIdentifierService).findIdByUniqueName("NX_P12345");
		verify(publicationDao).findSortedPublicationsByMasterId(100L);

		verify(dbXrefService).findDbXRefByPublicationIds(Arrays.asList(1L));
		verify(authorService).findAuthorsByPublicationIds(Arrays.asList(1L));
	}

	@Ignore
	@Test
	public void testFindPublicationsByMasterUniqueName() {
		List<Publication> publications = this.publicationService.findPublicationsByEntryName("NX_P12345");
		
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
		assertEquals("Pretty Revue of Science", publications.get(0).getJournalResourceLocator().getName());
		assertEquals("Revue of Science", publications.get(1).getJournalResourceLocator().getName());
		
	}

	@Test
	public void testGetEntryPublicationPage() {
		assertNotEquals("NX_P08246", eps.get(0).getEntryAccession());
		assertEquals("NX_P08246", this.publicationService.prioritizeEntry(eps, "NX_P08246").get(0).getEntryAccession() );
		assertEquals("NX_O75912", this.publicationService.prioritizeEntry(eps, "NX_O75912").get(0).getEntryAccession() );

	}

	@Test
	public void testGetEntryPublicationsSublist() {
		List<EntryPublication> sublist = this.publicationService.getEntryPublicationsSublist(eps, 1,2);
		assertEquals(2, sublist.size());
		assertEquals("NX_O75912", sublist.get(0).getEntryAccession());
		assertEquals("NX_P08246", sublist.get(1).getEntryAccession());

		sublist = this.publicationService.getEntryPublicationsSublist(eps, 2,1);
		assertEquals(1, sublist.size());
		assertEquals("NX_P08246", sublist.get(0).getEntryAccession());
	}
}
