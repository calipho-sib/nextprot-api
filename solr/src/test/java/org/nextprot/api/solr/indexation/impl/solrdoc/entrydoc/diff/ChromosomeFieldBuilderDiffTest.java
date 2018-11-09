package org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.diff;

import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.ChromosomeSolrFieldCollector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;

public class ChromosomeFieldBuilderDiffTest extends SolrDiffTest {

	@Ignore
	@Test
	public void testEntryWith3LocationsOn3differentChromosomes() {
		testEntryGivesSameIndexAsSolrIndex("NX_P62158"); // locations: 2p21, 19q13.32, 14q32.11
	}

	@Test
	public void testEntryWith2LocationsOnSameChromosomeAndDifferentBand() {
		testEntryGivesSameIndexAsSolrIndex("NX_E9PJI5"); // locations: 16p12.3 (CHR_HSCHR16_1_CTG1), 16p13.11
		testEntryGivesSameIndexAsSolrIndex("NX_O75144"); // locations: 21q22.3, 21p12
		testEntryGivesSameIndexAsSolrIndex("NX_O95255"); // locations: 16p13.11, 16p12.3 (CHR_HSCHR16_1_CTG1)
		testEntryGivesSameIndexAsSolrIndex("NX_P03989"); // locations: 6p22.1 (CHR_HSCHR6_MHC_MANN_CTG1), 6p21.33		
	}
	
	@Test
	public void testEntryWith2LocationsOnTheSameChromosomeAndBand() {
		// Other entries with same situation:NX_A8MYA2, NX_O75900, NX_P01562
		testEntryGivesSameIndexAsSolrIndex("NX_A6NE21"); // locations:  8p23.1
		testEntryGivesSameIndexAsSolrIndex("NX_P43686"); // locations: 19q13.2 		
	}

	@Test
	public void testEntryWith1Location() {
		testEntryGivesSameIndexAsSolrIndex("NX_A0PK05"); // locations: 10q11.21
		testEntryGivesSameIndexAsSolrIndex("NX_A0MZ66"); // locations: 10q25.3
		testEntryGivesSameIndexAsSolrIndex("NX_A1L3X4"); // locations: 16q13
		testEntryGivesSameIndexAsSolrIndex("NX_A0PJX8"); // locations: 1p26.31
		testEntryGivesSameIndexAsSolrIndex("NX_A1L170"); // locations: 1q23.3
		testEntryGivesSameIndexAsSolrIndex("NX_A1L020"); // locations: 1q22
		testEntryGivesSameIndexAsSolrIndex("NX_A2A368"); // locations: Xp21.1
		testEntryGivesSameIndexAsSolrIndex("NX_A6NDE4"); // locations: Yq11.223
	}

	
	@Test
	public void testEntryWithChromosomeAndUnknownBand() {
		testEntryGivesSameIndexAsSolrIndex("NX_Q13072"); // locations: 13 (unknown band) 
		testEntryGivesSameIndexAsSolrIndex("NX_Q86Y27"); // locations: 13 (unknown band)
	}

	@Test
	public void testEntryWithChromosomeAndBlankBand() {
		testEntryGivesSameIndexAsSolrIndex("NX_Q8WZ33"); // locations:  4 (blank band)
		testEntryGivesSameIndexAsSolrIndex("NX_P00156"); // locations: MT (blank band)
		testEntryGivesSameIndexAsSolrIndex("NX_P03905"); // locations: MT (blank band)
	}

	@Test
	public void testEntryWithUnknownChromosomeLocation() {
		// other cases with unknown locations: NX_O00370 NX_P0CW71 NX_Q96PT4 NX_Q96PT3 NX_Q9UN81
		testEntryGivesSameIndexAsSolrIndex("NX_P0CW71"); // locations: (unknown chromosome and band)
	}
	
	
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

	
	private void testEntryGivesSameIndexAsSolrIndex(String uniqueName) {
		Entry entry = getEntry(uniqueName);
		System.out.println(entry.getUniqueName());
		testChrLoc(entry);
		testChrLocS(entry);
		testGeneBand(entry);		
	}
	
	private void testChrLoc(Entry entry) {
		
		EntrySolrField field = EntrySolrField.CHR_LOC;

		ChromosomeSolrFieldCollector cfb = new ChromosomeSolrFieldCollector();
		Map<EntrySolrField, Object> fields = new HashMap<>();
		cfb.collect(fields, entry, false);
		
		// build a set with the list of actual values in field (which are separated by spaces)
		Set<String> actualSet = new TreeSet<>();
		String actualValue = getFieldValue(fields, field, String.class);
		actualSet.addAll(Arrays.asList(actualValue.split(" ")));
		
		// in the current pam implementation the data is the same 
		// but can contain several times the same values (and may have a different order)
		// nevertheless the set of unique values should be the same
		Set<String> expectedSet = new TreeSet<>();
		String expectedValue = (String) getValueForFieldInCurrentSolrImplementation(entry.getUniqueName(), field);
		expectedSet.addAll(Arrays.asList(expectedValue.replace(",","").split(" ")));

		//showActualAndExpectedValues(expectedSet,actualSet, "chr_loc");

		assertEquals(expectedSet,actualSet);

	}

	
	/*
	 * 
	 * The old pam implementation now sorts alphabetically 
	 * the multiple locations retrieved from db before indexing them in chr_loc field
	 * The new alain's implementation does the same as well now
	 * The value of chr_loc_s (sort order value for chr location) is computed 
	 * on the basis of the first location found in chr_loc in both old and new implementation
	 * 
	 */
	private void testChrLocS(Entry entry) {
		
		ChromosomeSolrFieldCollector cfb = new ChromosomeSolrFieldCollector();
		Map<EntrySolrField, Object> fields = new HashMap<>();
		cfb.collect(fields, entry, false);

		Integer expectedValue = (Integer) getValueForFieldInCurrentSolrImplementation(entry.getUniqueName(), EntrySolrField.CHR_LOC_S);
		Integer actualValue = getFieldValue(fields, EntrySolrField.CHR_LOC_S, Integer.class);

		assertEquals(expectedValue, actualValue);

	}
	

	@SuppressWarnings("unchecked")
	private void testGeneBand(Entry entry) {
		
		EntrySolrField field = EntrySolrField.GENE_BAND;

		ChromosomeSolrFieldCollector cfb = new ChromosomeSolrFieldCollector();
		Map<EntrySolrField, Object> fields = new HashMap<>();
		cfb.collect(fields, entry, false);
		
		Set<String> actualSet = new TreeSet<>();
		List<String> geneBandValues = getFieldValue(fields, field, List.class);
		for (String s: geneBandValues) actualSet.addAll(Arrays.asList(s.split(" ")));

		// in the current pam implementation the data is the same 
		// but can contains several times the same values and have a different order
		// nevertheless the set of unique values should be the same
		Set<String> expectedSet = new TreeSet<String>();
		List<String> expectedValues = (List<String>) getValueForFieldInCurrentSolrImplementation(entry.getUniqueName(), field);
		for (String s: expectedValues) expectedSet.addAll(Arrays.asList(s.split(" ")));
		
		//showActualAndExpectedValues(expectedSet,actualSet, "gene_band");
		
		assertEquals(expectedSet,actualSet);

	}

	private void showActualAndExpectedValues(Set<String> expectedSet, Set<String> actualSet, String fieldName) {
		for (String s: actualSet)   System.out.println("actual   " + fieldName + " value: " +s);
		for (String s: expectedSet) System.out.println("expected " + fieldName + " value: " +s);		
	}
	

}
