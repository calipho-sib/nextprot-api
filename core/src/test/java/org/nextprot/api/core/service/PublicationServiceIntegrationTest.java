package org.nextprot.api.core.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
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
	public void testPublicationByMD5() {
		 Publication publication = publicationService.findPublicationByMD5("fd2943320efb55276584a53b5b094049");
		 System.out.println(publication);
		 Assert.assertTrue(publication.getFirstPage().equals("254"));
		 // check that abstract bug CALIPHOMISC-241 is fixed (pmid 24867236)
		 Assert.assertTrue(publication.getAbstractText().contains("regulator of microtubule organisation"));
	}
}
