package org.nextprot.api.core.service;

//import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
//import org.nextprot.api.core.utils.TerminologyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({ "dev" })
public class PublicationServiceIntegrationTest extends CoreUnitBaseTest {

	@Autowired
	private PublicationService publicationService;

	@Test
	public void testPublicationIds() {
		 List<Long> ids = publicationService.findAllPublicationIds();
		 Assert.assertTrue(ids.size() > 400000);
	}
	
	
	@Test
	@Ignore
	public void testPublicationXrefs() {
		Publication publication = publicationService.findPublicationById(690405);
		// check that publication bug NEXTPROT-989 is fixed (pmid 12631268), should have 2 xrefs
		Set<DbXref> xrefs = publication.getDbXrefs();
		//System.err.println(TerminologyUtils.convertXrefsToSolrString(new ArrayList<DbXref>(xrefs)));
		Assert.assertTrue(xrefs.size() > 1);
	}
	
	
	@Test
	public void testPublicationAbstract() {
		 Publication publication = publicationService.findPublicationById(41589965);
		 Assert.assertTrue(publication.getFirstPage().equals("254"));
		 // check that abstract bug CALIPHOMISC-241 is fixed (pmid 24867236)
		 Assert.assertTrue(publication.getAbstractText().length() > 0);
	}

	
	@Test
	@Ignore
	public void testPublicationAbstractAsItShouldBeIfLoadedCorrectly() {
		 Publication publication = publicationService.findPublicationById(41589965);
		 Assert.assertTrue(publication.getFirstPage().equals("254"));
		 // check that abstract bug CALIPHOMISC-241 is fixed (pmid 24867236)
		 System.err.println(publication.getAbstractText());
		 Assert.assertTrue(publication.getAbstractText().contains("regulator of microtubule organisation"));
	}

	@Test
	public void testPublicationByMD5() {
		 Publication publication = publicationService.findPublicationByMD5("bf9d4c981d5fb32cdcd5425e9d9226f2");
		 //System.out.println(publication);
		 Assert.assertTrue(publication.getFirstPage().equals("34"));
	}
}
