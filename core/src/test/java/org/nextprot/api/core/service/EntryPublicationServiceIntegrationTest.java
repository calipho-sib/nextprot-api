package org.nextprot.api.core.service;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.publication.EntryPublication;
import org.nextprot.api.core.domain.publication.EntryPublicationView;
import org.nextprot.api.core.domain.publication.EntryPublications;
import org.nextprot.api.core.domain.publication.PublicationCategory;
import org.nextprot.api.core.domain.publication.PublicationDirectLink;
import org.nextprot.api.core.domain.publication.PublicationType;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ActiveProfiles({ "dev", "cache"})
public class EntryPublicationServiceIntegrationTest extends CoreUnitBaseTest{
        
    @Autowired
	private EntryPublicationService entryPublicationService;

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private EntryBuilderService entryBuilderService;

	@Autowired
	private EntryPublicationViewService entryPublicationViewService;

    @Ignore
    @Test
    public void testPerformance() {
    	
    	/*  big ones (but not huge)
    		
    		AC			annot count (np1)
    		-----------------------------------
    	    NX_Q8WZ42	23683 // titin excluded
			NX_Q8WXI7	10765 //  also excluded
			NX_Q5VST9	5588
			NX_Q8IVF2	5437
			NX_Q8NF91	5383
			NX_P20930	5147
			NX_P20929	4388
			NX_Q03001	4347
			NX_O75445	4159
			NX_Q685J3	4083
			NX_P51587	4053
			NX_Q9HC84	4043
			
			Results without cache:

			NX_Q5VST9 load=128035ms build=36ms annot=6107 publi=155 
			NX_Q8IVF2 load=129688ms build=16ms annot=6702 publi=130 
			NX_Q8NF91 load=134330ms build=14ms annot=6135 publi=218 
			NX_P20930 load=105603ms build=8ms annot=5524 publi=351 
			NX_P20929 load=52249ms build=8ms annot=5293 publi=146 
			NX_Q03001 load=64498ms build=8ms annot=5373 publi=189 
			NX_O75445 load=34551ms build=6ms annot=4194 publi=220 
			NX_Q685J3 load=31822ms build=3ms annot=4111 publi=113 
			NX_P51587 load=66270ms build=6ms annot=4872 publi=1041 
			NX_Q9HC84 load=44158ms build=3ms annot=4573 publi=189 
			
			=> time for building report is fast enough, max = 36ms , don't know time for loading with cache enabled
			=> 0.036 * 20000 = 720 sec = 12 minutes (if they are all as big as NX_Q5VST9 which is not the case)

			Results with cache:
			
			NX_Q5VST9 load=66ms build=6ms annot=6107 publi=155 
			NX_Q8IVF2 load=285ms build=7ms annot=6702 publi=130 
			NX_Q8NF91 load=368ms build=5ms annot=6135 publi=218 
			NX_P20930 load=100ms build=4ms annot=5524 publi=351 
			NX_P20929 load=130ms build=4ms annot=5293 publi=146 
			NX_Q03001 load=149ms build=3ms annot=5373 publi=189 
			NX_O75445 load=103ms build=4ms annot=4194 publi=220 
			NX_Q685J3 load=78ms build=3ms annot=4111 publi=113 
			NX_P51587 load=118ms build=7ms annot=4872 publi=1041 
			NX_Q9HC84 load=73ms build=3ms annot=4573 publi=189 
			
			=> load time is still more than 10 times slower than building report

    	 */
    	
    	List<String> entryAcList = new ArrayList<>(Arrays.asList(
    			"NX_Q5VST9","NX_Q8IVF2","NX_Q8NF91","NX_P20930","NX_P20929","NX_Q03001","NX_O75445","NX_Q685J3","NX_P51587","NX_Q9HC84"
    			// ,"NX_Q5VST9","NX_Q8IVF2","NX_Q8NF91","NX_P20930","NX_P20929","NX_Q03001","NX_O75445","NX_Q685J3","NX_P51587","NX_Q9HC84" // for testing with cache the first time
    			));

    	List<Long> t0=new ArrayList<>(), tLoad=new ArrayList<>(), tBuild=new ArrayList<>();
    	List<Integer>annCnt=new ArrayList<>(), pubCnt=new ArrayList<>(), curCnt=new ArrayList<>(), addCnt=new ArrayList<>();
    	int idx=-1;
    	for (String ac: entryAcList) {
    		idx++;
    		t0.add(System.currentTimeMillis());
	        Entry entry = entryBuilderService.build(EntryConfig.newConfig(ac).withEverything());
    		tLoad.add(System.currentTimeMillis()-t0.get(idx));
	        EntryPublications report = entryPublicationService.findEntryPublications(ac);
    		tBuild.add(System.currentTimeMillis()-tLoad.get(idx)-t0.get(idx));
    		annCnt.add(entry.getAnnotations().size());
    		pubCnt.add(entry.getPublications().size());
    		curCnt.add(report.getEntryPublicationList(PublicationCategory.CURATED).size());
    		addCnt.add(report.getEntryPublicationList(PublicationCategory.ADDITIONAL).size());
//    		System.out.println(
//    				entry.getUniqueName() + " " +
//    				"load="+(tLoad.get(idx)) + "ms " +
//    	    		"build="+(tBuild.get(idx)) + "ms " +
//    				"annot="+annCnt.get(idx) + " " +
//    				"publi="+pubCnt.get(idx) + " " +
//    				"curated="+curCnt.get(idx) + " " +
//    				"additonal="+addCnt.get(idx) + " "
//    				);
    	}
	    //System.out.println("END");
    }
    
    @Test
	public void testEntryPublicationFlagsConsistency() {
    	
    	// NX_Q9GZK6: with article, book, online publications
    	// NX_P46778: with article, submission
    	// NX_Q9UGR2: with thesis having both A and B links always from UniProt
    	// NX_Q96QD9: with thesis having only B link, always from UniProt
    	// NX_Q9H583; with patent having both A and B always links from UniProt
    	// NX_P40763: with patent having only B link, always from UniProt
    	// NX_Q8TE04: with unpublished observation having only B link, always from UniProt
    	// NX_P26367; with unpublished observation having both A and B always links from UniProt
    	
    	List<String> entryAcList = new ArrayList<>(Arrays.asList(
    			"NX_Q8TE04","NX_P26367","NX_Q9H583","NX_P40763","NX_Q96QD9","NX_Q9UGR2","NX_Q9GZK6","NX_P46778"));
    	for (String ac: entryAcList) {

            List<Publication> publications = publicationService.findPublicationsByEntryName(ac);
	        EntryPublications entryPublications = entryPublicationService.findEntryPublications(ac);
            publications.forEach(p -> {
	        	EntryPublication ep = entryPublications.getEntryPublication(p.getPublicationId());

	        	// for debugging
	        	/*
	        	String pro = entry.getUniqueName();
	        	String ttl = p.getTitle();
	        	String typ = p.getPublicationType();
	        	String dat = p.getPublicationYear();
	        	System.out.println(pro + " publi " + ep + " " + dat +" " +  typ + " " + ttl);
	        	*/
	        	
	        	// entry flags are exclusive: each publi should have one and only flag ON
	        	int entryFlagsOn = 0; 
	        	if (ep.isCurated()) entryFlagsOn++;
	        	if (ep.isAdditional()) entryFlagsOn++;
	        	if (ep.isPatent()) entryFlagsOn++;
	        	if (ep.isSubmission()) entryFlagsOn++;
	        	if (ep.isOnline()) entryFlagsOn++;
	        	Assert.assertEquals(1,entryFlagsOn);
	        	// other flags are exclusive: each publi should have one and only flag ON
	        	int otherFlagsOn = 0; 
	        	if (ep.isCited()) otherFlagsOn++;
	        	if (ep.isUncited()) otherFlagsOn++;
	        	Assert.assertEquals(1,otherFlagsOn);  
	        	// ONLINE publications should flags: online=ON and cited=ON
	        	if (p.getPublicationType()==PublicationType.ONLINE_PUBLICATION) {
	        		Assert.assertEquals(true,ep.isOnline());
	        		Assert.assertEquals(true,ep.isCited()); // always link B and opt. link A, both from UniProt  
	        	}
	        	if (p.getPublicationType()==PublicationType.SUBMISSION) {
	        		Assert.assertEquals(true,ep.isSubmission());
	        		Assert.assertEquals(true,ep.isCited()); // always link B and opt. link A, both from UniProt  
	        	}
	        	if (p.getPublicationType()==PublicationType.UNPUBLISHED_OBSERVATION) {
	        		Assert.assertEquals(true,ep.isCited()); // always link B and opt. link A, both from UniProt  
	        		Assert.assertEquals(true,ep.isSubmission()); // always link B and opt. link A, both from UniProt
	        	}
	        });
    	}
    }

    @Test
    public void testPublicationDirectLinksFromAnEntry() {

        EntryPublications entryPublications = entryPublicationService.findEntryPublications("NX_Q14587");

        List<EntryPublication> filteredSingleton = entryPublications.getEntryPublicationList(PublicationCategory.CURATED).stream()
                .filter(ep -> ep.getPubId() == 29230867)
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

    @Test
    public void testPublicationDirectLinksFromAnEntryForSubmissionView() {

        List<EntryPublication> publications = entryPublicationService.findEntryPublications("NX_Q14587")
                .getEntryPublicationList(PublicationCategory.SUBMISSION);

        Assert.assertTrue(publications.size() >= 1);
    }

    @Test
    public void testPublicationDirectLinksFromAnEntryForAllView() {

        List<EntryPublication> publications = entryPublicationService.findEntryPublications("NX_Q14587")
                .getEntryPublicationList(PublicationCategory.ALL);

        Assert.assertTrue(publications.size() >= 46);
    }

	@Test
	public void NX_P02768SubmissionShouldBeCitedForViewsSequenceAndNotStructureAnyMore() {

		List<EntryPublicationView> epvList = entryPublicationViewService.buildEntryPublicationView("NX_P02768", PublicationCategory.PATENT);

		Optional<EntryPublicationView> epvOpt = epvList.stream()
				.filter(epv -> epv.getPublication().getPublicationId() == 2707627)
				.findFirst();

		Assert.assertTrue(epvOpt.isPresent());

		Map<String, String> views = epvOpt.get().getCitedInViews();

		Assert.assertTrue(views.containsKey("Sequence"));
		Assert.assertFalse(views.containsKey("Structures"));
	}

	@Test
	public void NX_P02768PatentShouldBeCitedForViewsSequenceAndNotStructureAnyMore() {

		List<EntryPublicationView> epvList = entryPublicationViewService.buildEntryPublicationView("NX_P02768", PublicationCategory.SUBMISSION);

		Optional<EntryPublicationView> epvOpt = epvList.stream()
				.filter(epv -> epv.getPublication().getPublicationId() == 6635395)
				.findFirst();

		Assert.assertTrue(epvOpt.isPresent());

		Map<String, String> views = epvOpt.get().getCitedInViews();

		Assert.assertTrue(views.containsKey("Sequence"));
		Assert.assertFalse(views.containsKey("Structures"));
	}
}