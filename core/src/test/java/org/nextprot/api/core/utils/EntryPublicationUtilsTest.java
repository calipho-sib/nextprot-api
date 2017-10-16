package org.nextprot.api.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.publication.PublicationType;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.nextprot.api.core.utils.EntryPublicationUtils.EntryPublication;
import org.nextprot.api.core.utils.EntryPublicationUtils.EntryPublicationReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({ "dev" })
public class EntryPublicationUtilsTest extends CoreUnitBaseTest{
        
    @Autowired
	private EntryBuilderService entryBuilderService = null;
    
    
    @Test
    public void test1() {
    	List<String> entryAcList = new ArrayList<>(Arrays.asList(
    			"NX_Q8TE04")); //,"NX_P26367","NX_Q9H583","NX_P40763","NX_Q96QD9","NX_Q9UGR2","NX_Q9GZK6","NX_P46778"));
    	for (String ac: entryAcList) {
	        Entry entry = entryBuilderService.build(EntryConfig.newConfig(ac).withEverything());
	        for (Annotation a: entry.getAnnotations()) {
	        	//ProteomicsPageView p
	        }
    	}
    	
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
}