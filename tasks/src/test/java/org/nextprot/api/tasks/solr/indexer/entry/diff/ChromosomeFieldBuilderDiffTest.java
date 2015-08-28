package org.nextprot.api.tasks.solr.indexer.entry.diff;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.SolrDiffTest;
import org.nextprot.api.tasks.solr.indexer.entry.impl.ChromosomeFieldBuilder;

public class ChromosomeFieldBuilderDiffTest extends SolrDiffTest {

	@Test
	public void testChromsomalLocation() {
		
		for(int i=0; i < 10; i++){
			Entry entry = getEntry(i);
			System.out.println(entry.getUniqueName());
			testChrLoc(entry);
			testChrLocS(entry);
			testGeneBand(entry);
		}
	}

	
	public void testChrLoc(Entry entry) {
		
		Fields field = Fields.CHR_LOC;

		ChromosomeFieldBuilder cfb = new ChromosomeFieldBuilder();
		cfb.initializeBuilder(entry);
		String chrLocValue = cfb.getFieldValue(field, String.class);
		
		String expectedValues = (String) getValueForFieldInCurrentSolrImplementation(entry.getUniqueName(), field);
		assertEquals(expectedValues, chrLocValue);

	}
	
	
	public void testChrLocS(Entry entry) {
		
		Fields field = Fields.CHR_LOC_S;

		ChromosomeFieldBuilder cfb = new ChromosomeFieldBuilder();
		cfb.initializeBuilder(entry);
		
		Integer chrLocValue = cfb.getFieldValue(field, Integer.class);
		
		Integer expectedValue = (Integer) getValueForFieldInCurrentSolrImplementation(entry.getUniqueName(), field);
		
		assertEquals(expectedValue, chrLocValue);

	}
	

	@SuppressWarnings("unchecked")
	private void testGeneBand(Entry entry) {
		
		Fields field = Fields.GENE_BAND;

		ChromosomeFieldBuilder cfb = new ChromosomeFieldBuilder();
		cfb.initializeBuilder(entry);
		List<String> geneBandValues = cfb.getFieldValue(field, List.class);
		List<String> expectedValues = (List<String>) getValueForFieldInCurrentSolrImplementation(entry.getUniqueName(), field);
		
		assertEquals(expectedValues.size(), 1); // Even it if maps to many genes we only take the 1st one
		assertEquals(geneBandValues.size(), 1); // Even it if maps to many genes we only take the 1st one

		String expectedValue = expectedValues.get(0);
		String newValue = geneBandValues.get(0).toString();
		
		assertTrue(expectedValue.startsWith(newValue)); // Kant: [p13.11 16p13.11] new version: [p13.11]

	}


}
