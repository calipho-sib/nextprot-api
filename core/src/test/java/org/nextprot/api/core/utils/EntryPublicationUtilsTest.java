package org.nextprot.api.core.utils;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.publication.*;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

//@ActiveProfiles({ "dev","cache" })
@ActiveProfiles({ "dev" })
public class EntryPublicationUtilsTest extends CoreUnitBaseTest{
        
    @Autowired
	private EntryBuilderService entryBuilderService = null;
    
    
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
	        EntryPublicationReport report = EntryPublicationUtils.buildReport(entry);
    		tBuild.add(System.currentTimeMillis()-tLoad.get(idx)-t0.get(idx));
    		annCnt.add(entry.getAnnotations().size());
    		pubCnt.add(entry.getPublications().size());
    		curCnt.add(report.getEntryPublicationList(PublicationView.CURATED).size());
    		addCnt.add(report.getEntryPublicationList(PublicationView.ADDITIONAL).size());
    		System.out.println(
    				entry.getUniqueName() + " " +
    				"load="+(tLoad.get(idx)) + "ms " +
    	    		"build="+(tBuild.get(idx)) + "ms " +
    				"annot="+annCnt.get(idx) + " " +
    				"publi="+pubCnt.get(idx) + " " +
    				"curated="+curCnt.get(idx) + " " +
    				"additonal="+addCnt.get(idx) + " " 
    				);
    	}
    	System.out.println("END");
    	
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
	        Entry entry = entryBuilderService.build(EntryConfig.newConfig(ac).withEverything());
	        EntryPublicationReport report = EntryPublicationUtils.buildReport(entry);
	        entry.getPublications().forEach(p -> {
	        	EntryPublication ep = report.getEntryPublication(p.getPublicationId());
	        	
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
	        	if (PublicationType.valueOfName(p.getPublicationType())==PublicationType.ONLINE_PUBLICATION) {	
	        		Assert.assertEquals(true,ep.isOnline());
	        		Assert.assertEquals(true,ep.isCited()); // always link B and opt. link A, both from UniProt  
	        	}
	        	if (PublicationType.valueOfName(p.getPublicationType())==PublicationType.SUBMISSION) {	
	        		Assert.assertEquals(true,ep.isSubmission());
	        		Assert.assertEquals(true,ep.isCited()); // always link B and opt. link A, both from UniProt  
	        	}
	        	if (PublicationType.valueOfName(p.getPublicationType())==PublicationType.UNPUBLISHED_OBSERVATION) {	
	        		Assert.assertEquals(true,ep.isCited()); // always link B and opt. link A, both from UniProt  
	        		Assert.assertEquals(true,ep.isCurated()); // always link B and opt. link A, both from UniProt  
	        	}
	        });
    	}
    }


	@Test
	public void testPublicationDirectLinkListOrder() {
		Publication p = new Publication();
		p.setId(188);
		List<String> scopes = Arrays.asList(
				"VARIANT SCA34 PHE-168",
				"CLEAVAGE OF INITIATOR METHIONINE [LARGE SCALE ANALYSIS]",
				"INVOLVEMENT IN SCA34");
		List<String> comments = Arrays.asList(
				"[PDB:1JWU] [Structure]",
				"[iPTMnet:P04637] [PTM/processing]Phosphorylation",
				"[GeneRif:3303] S100A4 has opposite roles in Tag7 and Hsp70- mediated tumoricidal mechanisms",
				"[PRO:PR:000028557] [PTM/processing]P18848-1",
				"[GAD:125207] [Pathology & Biotech]Associated with CARDIOVASCULAR: pulmonary hypertension; thrombosis, deep vein; pulmonary thromboembolism; HLA-B");

        Map<PublicationProperty, List<PublicationDirectLink>> m = new HashMap<>();
        m.put(PublicationProperty.SCOPE, new ArrayList<>());
        m.put(PublicationProperty.COMMENT, new ArrayList<>());
		scopes.forEach(scope -> m.get(PublicationProperty.SCOPE).add(new PublicationDirectLink(188, PublicationProperty.SCOPE, scope)));
        comments.forEach(comment -> m.get(PublicationProperty.COMMENT).add(new PublicationDirectLink(188, PublicationProperty.COMMENT, comment)));

		p.setDirectLinks(m);

		List<PublicationDirectLink> links = p.getDirectLinks();
		Assert.assertEquals(8, links.size());
		// should be datasource UniProt first, then order by database alpha insensitive, then by label alpha
		Assert.assertEquals(links.get(0).getLabel(), "CLEAVAGE OF INITIATOR METHIONINE [LARGE SCALE ANALYSIS]");
		Assert.assertEquals(links.get(1).getLabel(), "INVOLVEMENT IN SCA34");
        Assert.assertEquals(links.get(2).getLabel(), "VARIANT SCA34 PHE-168");
		Assert.assertEquals(links.get(3).getLabel(), "[Pathology & Biotech]Associated with CARDIOVASCULAR: pulmonary hypertension; thrombosis, deep vein; pulmonary thromboembolism; HLA-B");
		Assert.assertEquals(links.get(4).getLabel(), "S100A4 has opposite roles in Tag7 and Hsp70- mediated tumoricidal mechanisms");
		Assert.assertEquals(links.get(5).getLabel(), "[PTM/processing]Phosphorylation");
		Assert.assertEquals(links.get(6).getLabel(), "[Structure]");
		Assert.assertEquals(links.get(7).getLabel(), "[PTM/processing]P18848-1");

        Assert.assertEquals(links.get(0).getDatasource(), "Uniprot");
        Assert.assertEquals(links.get(1).getDatasource(), "Uniprot");
        Assert.assertEquals(links.get(2).getDatasource(), "Uniprot");
        Assert.assertEquals(links.get(3).getDatasource(), "PIR");
        Assert.assertEquals(links.get(4).getDatasource(), "PIR");
        Assert.assertEquals(links.get(5).getDatasource(), "PIR");
        Assert.assertEquals(links.get(6).getDatasource(), "PIR");
        Assert.assertEquals(links.get(7).getDatasource(), "PIR");
	}
}