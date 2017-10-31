package org.nextprot.api.core.dao;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.dao.MasterIdentifierDao;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.publication.EditedVolumeBookResourceLocator;
import org.nextprot.api.core.domain.publication.JournalResourceLocator;
import org.nextprot.api.core.domain.publication.PublicationType;
import org.nextprot.api.core.domain.publication.WebPublicationPage;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@ActiveProfiles({ "dev" })
public class PublicationDaoIntegrationTest extends CoreUnitBaseTest {
	
	@Autowired PublicationDao publicationDao;
	@Autowired MasterIdentifierDao masterIdentifierDao;

	@Test
	public void testArticlePublication() {

		Publication publication = publicationDao.findPublicationById(681448L);

		Assert.assertEquals(PublicationType.ARTICLE, publication.getPublicationType());
		Assert.assertTrue(publication.isLocalizableInBookMedium());
		Assert.assertTrue(publication.isLocatedInScientificJournal());
		Assert.assertTrue(!publication.isLocatedInEditedVolumeBook());
		Assert.assertEquals("Molecular basis of fibrinogen Naples associated with defective thrombin binding and thrombophilia. Homozygous substitution of B beta 68 Ala----Thr.", publication.getTitle());
		Assert.assertEquals("1992-07", publication.getTextDate());
		Assert.assertTrue(publication.getPublicationResourceLocator() instanceof JournalResourceLocator);
		Assert.assertEquals("The Journal of clinical investigation", publication.getPublicationLocatorName());
		Assert.assertEquals("238", ((JournalResourceLocator)publication.getPublicationResourceLocator()).getFirstPage());
		Assert.assertEquals("244", ((JournalResourceLocator)publication.getPublicationResourceLocator()).getLastPage());
		Assert.assertEquals("90", ((JournalResourceLocator)publication.getPublicationResourceLocator()).getVolume());
		Assert.assertEquals("1", ((JournalResourceLocator)publication.getPublicationResourceLocator()).getIssue());
	}

	
	@Test
	public void testPubMedPublication() {
		Publication publication = publicationDao.findPublicationByDatabaseAndAccession("PubMed", "25923089");
		Assert.assertEquals("Correlation of hepcidin level with insulin resistance and endocrine glands function in major thalassemia.", publication.getTitle());
	}
	
	@Test
	public void testBookPublication() {
		
		Publication publication = publicationDao.findPublicationById(15642147L);

		Assert.assertEquals(PublicationType.BOOK, publication.getPublicationType());
		Assert.assertTrue(publication.isLocalizableInBookMedium());
		Assert.assertTrue(!publication.isLocatedInScientificJournal());
		Assert.assertTrue(publication.isLocatedInEditedVolumeBook());
		Assert.assertTrue(publication.getPublicationResourceLocator() instanceof EditedVolumeBookResourceLocator);
		Assert.assertEquals("Plenum Press", publication.getPublisherName());
		Assert.assertEquals("New York", publication.getPublisherCity());
		Assert.assertEquals("Fibrinogen, thrombosis, coagulation and fibrinolysis", publication.getPublicationLocatorName());
		Assert.assertEquals("39", publication.getFirstPage());
		Assert.assertEquals("48", publication.getLastPage());
		Assert.assertEquals("1991", publication.getTextDate());
	}

	@Test
	public void testOnlinePublication() {

		Publication publication = publicationDao.findPublicationById(3183821L);

		Assert.assertEquals(PublicationType.ONLINE_PUBLICATION, publication.getPublicationType());
		Assert.assertTrue(!publication.isLocalizableInBookMedium());
		Assert.assertEquals("SHMPD", publication.getPublicationResourceLocator().getName());
		Assert.assertEquals("http://shmpd.bii.a-star.edu.sg/gene.php?genestart=A&genename=BRCA1", ((WebPublicationPage)publication.getPublicationResourceLocator()).getUrl());
		Assert.assertEquals("The Singapore human mutation and polymorphism database", publication.getTitle());
	}

	@Test
	public void testPublicationsByMasterId() {

		Long id = masterIdentifierDao.findIdByUniqueName("NX_P02675"); // NX_P02675 -> 582546
		List<Long> pubs = publicationDao.findSortedPublicationIdsByMasterId(id);
		Assert.assertTrue(!pubs.isEmpty());
	}

	@Test
	public void testMissingTitle() {

		Publication publication = publicationDao.findPublicationById(3183815L);

		Assert.assertTrue(!publication.hasTitle());
	}

	@Test
	public void testMissingAuthors() {

		Publication publication = publicationDao.findPublicationById(3183815L);

		Assert.assertTrue(!publication.hasAuthors());
	}
}
