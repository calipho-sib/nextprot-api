package org.nextprot.api.tasks.solr.indexer.entry.integration;

import java.util.List;

import org.junit.Test;
import org.junit.Assert;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.SolrBuildIntegrationTest;
import org.nextprot.api.tasks.solr.indexer.entry.impl.AnnotationFieldBuilder;
import org.springframework.beans.factory.annotation.Autowired;


public class AnnotationFieldBuilderIntegrationTest extends SolrBuildIntegrationTest{

	@Autowired	private EntryBuilderService entryBuilderService = null;
	@Autowired	private MasterIdentifierService masterIdentifierService = null;
	@Autowired 	private TerminologyService terminologyService;
	@Test
	public void testBEDintegration() {
		String[] BEDtest_list = {"NX_P35498", "NX_Q99250","NX_Q9NY46", "NX_P35499", "NX_Q14524", "NX_Q01118","NX_Q9UQD0", "NX_Q15858", "NX_Q9Y5Y9", "NX_Q9UI33",
				"NX_P38398", "NX_P51587","NX_P16422", "NX_P40692", "NX_Q9UHC1", "NX_P43246", "NX_P52701", "NX_P54278"};
		int bedAnnotCnt = 0;
		
		AnnotationFieldBuilder afb = new AnnotationFieldBuilder();
		afb.setTerminologyService(terminologyService);
		afb.initializeBuilder(getEntry("NX_P35498"));

		List<String> annotations = afb.getFieldValue(Fields.ANNOTATIONS, List.class);
		for(String annot : annotations) {
			if(annot.startsWith("SCN1A-")) {
				bedAnnotCnt++;
				// System.err.println("BED annot: " + annot);
			}
			
		}
		Assert.assertTrue(bedAnnotCnt >= 33);
		
		bedAnnotCnt = 0;
		afb.reset();
		afb.initializeBuilder(getEntry("NX_P16422"));
		annotations = afb.getFieldValue(Fields.ANNOTATIONS, List.class);
		for(String annot : annotations)
			if(annot.startsWith("EPCAM-"))
				bedAnnotCnt++;
		
		Assert.assertTrue(bedAnnotCnt >= 21);
	}
	
	protected Entry getEntry(String entryName){
		return entryBuilderService.build(EntryConfig.newConfig(entryName).withEverything());
	}
	
}
