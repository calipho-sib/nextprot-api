package org.nextprot.api.core.service;

//import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.publication.PublicationDirectLink;
import org.nextprot.api.core.domain.publication.PublicationView;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//import org.nextprot.api.core.utils.TerminologyUtils;

@ActiveProfiles({ "dev" })
public class PublicationServiceIntegrationTest extends CoreUnitBaseTest {

	@Autowired
	private PublicationService publicationService;

	@Test
	public void testPublicationIds() {
		 List<Long> ids = publicationService.findAllPublicationIds();
		 System.out.println(ids.size());
		 Assert.assertTrue(ids.size() > 370000);
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
	
	
	@Test
	public void testPublicationByDOI() {
		 Publication publication = publicationService.findPublicationByDatabaseAndAccession("DOI", "10.1111/j.1349-7006.2012.02267.x");
		 Assert.assertEquals("High levels of DJ-1 protein in nipple fluid of patients with breast cancer.", publication.getTitle());
	}

    // TODO: Should be in EntryPublication instead
    @Test
    public void testPublicationDirectLinksFromAnEntry() {

        List<Publication> publications = publicationService.findPublicationsByEntryName("NX_Q14587");

        List<Publication> filteredSingleton = publications.stream()
                .filter(publication -> publication.getPublicationId() == 29230867)
                .collect(Collectors.toList());

        Assert.assertEquals(1, filteredSingleton.size());

        List<PublicationDirectLink> directLinks = filteredSingleton.get(0).getDirectLinks();

        Assert.assertEquals(3, directLinks.size());

        String[] expectedLabels = new String[] {"INTERACTION WITH TRIM28", "MUTAGENESIS OF ASP-85; VAL-86; VAL-88; PHE-90; GLU-93; GLU-94 AND TRP-95", "SUBCELLULAR LOCATION (ISOFORMS 1 AND 2)"};

        for (int i=0 ; i<3 ;i++) {

            Assert.assertEquals(29230867, directLinks.get(i).getPublicationId());
            Assert.assertEquals("Uniprot", directLinks.get(i).getDatasource());
            Assert.assertEquals("UniProtKB", directLinks.get(i).getDatabase());
            Assert.assertEquals(expectedLabels[i], directLinks.get(i).getLabel());
        }
    }

    // TODO: Should be in EntryPublication instead
    @Ignore
    @Test
    public void testPublicationDirectLinksFromAnEntryForSubmissionView() {

        List<Publication> publications = publicationService.findPublicationsByEntryName("NX_Q14587", PublicationView.SUBMISSION);

        Assert.assertEquals(1, publications.size());
        System.out.println(publications);
    }
}
