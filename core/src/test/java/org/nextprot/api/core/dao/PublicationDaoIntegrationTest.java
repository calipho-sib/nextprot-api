package org.nextprot.api.core.dao;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.dao.MasterIdentifierDao;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.publication.EditedVolumeBookLocation;
import org.nextprot.api.core.domain.publication.JournalLocation;
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

		Assert.assertEquals(PublicationType.ARTICLE, PublicationType.valueOfName(publication.getPublicationType()));
		Assert.assertTrue(publication.isLocalizableInBookMedium());
		Assert.assertTrue(publication.isLocatedInScientificJournal());
		Assert.assertTrue(!publication.isLocatedInEditedVolumeBook());
		Assert.assertEquals("Molecular basis of fibrinogen Naples associated with defective thrombin binding and thrombophilia. Homozygous substitution of B beta 68 Ala----Thr.", publication.getTitle());
		Assert.assertEquals("1992-07", publication.getTextDate());
		Assert.assertTrue(publication.getPublicationLocation() instanceof JournalLocation);
		Assert.assertEquals("The Journal of clinical investigation", publication.getPublicationLocationName());
		Assert.assertEquals("238", ((JournalLocation)publication.getPublicationLocation()).getFirstPage());
		Assert.assertEquals("244", ((JournalLocation)publication.getPublicationLocation()).getLastPage());
		Assert.assertEquals("90", ((JournalLocation)publication.getPublicationLocation()).getVolume());
		Assert.assertEquals("1", ((JournalLocation)publication.getPublicationLocation()).getIssue());
	}

	@Test
	public void testBookPublication() {
		
		Publication publication = publicationDao.findPublicationById(15642147L);

		Assert.assertEquals(PublicationType.BOOK, PublicationType.valueOfName(publication.getPublicationType()));
		Assert.assertTrue(publication.isLocalizableInBookMedium());
		Assert.assertTrue(!publication.isLocatedInScientificJournal());
		Assert.assertTrue(publication.isLocatedInEditedVolumeBook());
		Assert.assertTrue(publication.getPublicationLocation() instanceof EditedVolumeBookLocation);
		Assert.assertEquals("Plenum Press", publication.getPublisherName());
		Assert.assertEquals("New York", publication.getPublisherCity());
		Assert.assertEquals("Fibrinogen, thrombosis, coagulation and fibrinolysis", publication.getPublicationLocationName());
		Assert.assertEquals("39", publication.getFirstPage());
		Assert.assertEquals("48", publication.getLastPage());
		Assert.assertEquals("1991", publication.getTextDate());
	}

	@Test
	public void testOnlinePublication() {

		Publication publication = publicationDao.findPublicationById(3183821L);

		Assert.assertEquals(PublicationType.ONLINE_PUBLICATION, PublicationType.valueOfName(publication.getPublicationType()));
		Assert.assertTrue(!publication.isLocalizableInBookMedium());
		Assert.assertEquals("SHMPD", publication.getPublicationLocation().getName());
		Assert.assertEquals("http://shmpd.bii.a-star.edu.sg/gene.php?genestart=A&genename=BRCA1", ((WebPublicationPage)publication.getPublicationLocation()).getUrl());
		Assert.assertEquals("The Singapore human mutation and polymorphism database", publication.getTitle());
	}

	@Test
	public void testPublicationsByMasterId() {

		Long id = masterIdentifierDao.findIdByUniqueName("NX_P02675"); // NX_P02675 -> 582546
		List<Long> pubs = publicationDao.findSortedPublicationIdsByMasterId(id);
		Assert.assertTrue(!pubs.isEmpty());
	}

	@Test
	public void testMissingJournalName() {

		Publication publication = publicationDao.findPublicationById(7089529L);

		Assert.assertEquals("Stem Cells", publication.getPublicationLocationName());
	}
}
