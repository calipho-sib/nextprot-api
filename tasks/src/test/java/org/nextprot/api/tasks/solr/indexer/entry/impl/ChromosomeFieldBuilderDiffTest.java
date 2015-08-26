package org.nextprot.api.tasks.solr.indexer.entry.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.SolrDiffTest;
import org.springframework.beans.factory.annotation.Autowired;

public class ChromosomeFieldBuilderDiffTest extends SolrDiffTest {

	@Autowired	private EntryBuilderService entryBuilderService = null;
	@Autowired	private MasterIdentifierService masterIdentifierService = null;
	
	@Test
	public void testChromsomalLocation() {
		
		Set<String> entries = masterIdentifierService.findUniqueNames();
		Iterator<String> entriesIt = entries.iterator();

		int i = 0;
		while(entriesIt.hasNext() && i < 100){
			String entryName = entriesIt.next();
			System.out.print(i++);
			System.out.println(entryName);
			
			Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryName).withChromosomalLocations());

			testChrLoc(entryName, entry);
			testChrLocS(entryName, entry);
			testGeneBand(entryName, entry);
		}
		
		
	}

	
	public void testChrLoc(String entryName, Entry entry) {
		
		Fields field = Fields.CHR_LOC;

		ChromosomeFieldBuilder cfb = new ChromosomeFieldBuilder(entry);
		String chrLocValue = cfb.build(entry, field, String.class);
		
		List<Object> expectedValues = getValueForFieldInCurrentSolrImplementation(entryName, field);
		
		assertEquals(expectedValues.size(), 1);
		assertEquals(expectedValues.get(0), chrLocValue);

	}
	
	
	public void testChrLocS(String entryName, Entry entry) {
		
		Fields field = Fields.CHR_LOC_S;

		ChromosomeFieldBuilder cfb = new ChromosomeFieldBuilder(entry);
		Integer chrLocValue = cfb.build(entry, field, Integer.class);
		
		List<Object> expectedValues = getValueForFieldInCurrentSolrImplementation(entryName, field);
		
		assertEquals(expectedValues.size(), 1);
		assertEquals(expectedValues.get(0), chrLocValue);

	}
	

	@SuppressWarnings("unchecked")
	private void testGeneBand(String entryName, Entry entry) {
		
		Fields field = Fields.GENE_BAND;

		ChromosomeFieldBuilder cfb = new ChromosomeFieldBuilder(entry);
		List<String> geneBandValues = cfb.build(entry, field, List.class);
		List<Object> expectedValues = getValueForFieldInCurrentSolrImplementation(entryName, field);
		
		assertEquals(expectedValues.size(), 1); // Even it if maps to many genes we only take the 1st one
		assertEquals(geneBandValues.size(), 1); // Even it if maps to many genes we only take the 1st one

		String expectedValue = ((List<String>) expectedValues.get(0)).get(0);
		String newValue = geneBandValues.get(0).toString();
		
		assertTrue(expectedValue.startsWith(newValue)); // Kant: [p13.11 16p13.11] new version: [p13.11]

	}


}
