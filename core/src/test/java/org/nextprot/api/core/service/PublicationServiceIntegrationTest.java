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
	public void testPublicationById() {
		 List<Long> ids = publicationService.findAllPublicationIds();
		 //Publication publication = publicationService.findPublicationById(ids.get(0));
		 Publication publication = publicationService.findPublicationById(41632424);
		 System.out.println(publication);
	}
}
