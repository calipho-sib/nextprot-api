package org.nextprot.api.tasks.solr.indexer.entry.diff;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.SolrDiffTest;
import org.nextprot.api.tasks.solr.indexer.entry.impl.PublicationsFieldBuilder;

public class PublicationFieldBuilderDiffTest extends SolrDiffTest {

	@Test
	public void testPublications() {

		
		 //for(int i=0; i < 10; i++){ testPublications(getEntry(i)); }
		 

		Entry entry = getEntry("NX_Q96I99");
		testPublications(entry);

	}

	public void testPublications(Entry entry) {
		String entryName = entry.getUniqueName();

		System.out.println("Testing: " + entryName);
		PublicationsFieldBuilder pfb = new PublicationsFieldBuilder();
		pfb.initializeBuilder(entry);
		
		Set<String> expectedPublis = new TreeSet<String>((List) getValueForFieldInCurrentSolrImplementation(entryName, Fields.PUBLICATIONS));
		Set<String> PublicationSet = new TreeSet<String>(pfb.getFieldValue(Fields.PUBLICATIONS, List.class));
		//Assert.assertEquals( expectedPublis.size(), PublicationSet.size());
		//Set<String> expectedPubliscopy = new TreeSet<String>(expectedPublis);
		Set<String> PublicationSetcopy = new TreeSet<String>(PublicationSet);
		
		PublicationSet.removeAll(expectedPublis);
		System.err.println(PublicationSet.size() + " elements are only in the new index");
		System.err.println(PublicationSet);
		expectedPublis.removeAll(PublicationSetcopy);
		System.err.println(expectedPublis.size() + " elements are only in the old index");
		for(String elem : expectedPublis)
			System.err.println(elem);
		
		
		//String expectedPE = (String) getValueForFieldInCurrentSolrImplementation(entryName, Fields.PROTEIN_EXISTENCE);
		//Assert.assertEquals(getValueForFieldInCurrentSolrImplementation(entryName, Fields.PUBLI_CURATED_COUNT), pfb.getFieldValue(Fields.PUBLI_CURATED_COUNT, Integer.class));

		//Assert.assertEquals(getValueForFieldInCurrentSolrImplementation(entryName, Fields.PUBLI_COMPUTED_COUNT), pfb.getFieldValue(Fields.PUBLI_COMPUTED_COUNT, Integer.class));

		//Assert.assertEquals(getValueForFieldInCurrentSolrImplementation(entryName, Fields.PUBLI_LARGE_SCALE_COUNT), pfb.getFieldValue(Fields.PUBLI_LARGE_SCALE_COUNT, Integer.class));

		//Assert.assertEquals(getValueForFieldInCurrentSolrImplementation(entryName, Fields.INFORMATIONAL_SCORE), pfb.getFieldValue(Fields.INFORMATIONAL_SCORE, Float.class));
}

}
