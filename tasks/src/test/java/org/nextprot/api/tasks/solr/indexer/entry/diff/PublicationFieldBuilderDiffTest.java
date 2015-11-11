package org.nextprot.api.tasks.solr.indexer.entry.diff;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.SolrDiffTest;
import org.nextprot.api.tasks.solr.indexer.entry.impl.PublicationsFieldBuilder;

public class PublicationFieldBuilderDiffTest extends SolrDiffTest {

	@Test
	@Ignore
	public void testPublications() {

		
		 //for(int i=0; i < 10; i++){ testPublications(getEntry(i)); }
		 

		//Entry entry = getEntry("NX_Q96I99");
		Entry entry = getEntry("NX_P61604");
		testPublications(entry);

	}

	public void testPublications(Entry entry) {
		String entryName = entry.getUniqueName();

		System.out.println("Testing: " + entryName);
		PublicationsFieldBuilder pfb = new PublicationsFieldBuilder();
		pfb.initializeBuilder(entry);
		
		Set<String> expectedPublisRaw = new TreeSet<String>((List) getValueForFieldInCurrentSolrImplementation(entryName, Fields.PUBLICATIONS));
		Set<String> PublicationSet = new TreeSet<String>(pfb.getFieldValue(Fields.PUBLICATIONS, List.class));
		Set<String> expectedValues = new TreeSet<String>();

		for (String s : expectedPublisRaw) {
			String indextoken;
			
			if(s.startsWith("<p>")) {
				// like <p><b>title : </b>Mapping the hallmarks of lung adenocarcinoma with massively parallel sequencing.</p><p><b>journal</b> : Cell - Cell</p><p><b>nlmid:</b>0413066</p><p><b>authors : </b>Imielinski Marcin M",
				indextoken = getValueFromRawData(s,"title");
				if(indextoken != null && indextoken.length() > 1) expectedValues.add(indextoken);
				indextoken = getValueFromRawData(s,"journal");
				if(indextoken != null) expectedValues.add(indextoken.substring(3)); 
				indextoken = getValueFromRawData(s,"nlmid");
				if(indextoken != null) expectedValues.add(indextoken);
				indextoken = getValueFromRawData(s,"authors");
				if(indextoken != null) expectedValues.add(indextoken);
				}
			else if(s.endsWith("</p>")) expectedValues.add(s.substring(0,s.indexOf("</p>"))); // like "Meyerson Matthew M</p>"
			else expectedValues.add(s);
		}
		
		//Assert.assertEquals( expectedValues.size(), PublicationSet.size());
		//Set<String> expectedPubliscopy = new TreeSet<String>(expectedPublis);
		/*for(String elem : PublicationSet)
			System.out.println(elem);
		System.err.println(PublicationSet.size() + " elements in the new index");*/
		
		Set<String> PublicationSetcopy = new TreeSet<String>(PublicationSet);
		
		PublicationSet.removeAll(expectedValues);
		System.err.println(PublicationSet.size() + " elements are only in the new index");
		for(String elem : PublicationSet)
			System.out.println(elem);
		expectedValues.removeAll(PublicationSetcopy);
		System.err.println("\n" + expectedValues.size() + " elements are only in the old index");
		for(String elem : expectedValues)
			System.out.println(elem);
		
		//Assert.assertTrue( expectedValues.size()==0 && PublicationSet.size()==0);
		
		int pubCount, expectedPubCount;
		expectedPubCount = (int) getValueForFieldInCurrentSolrImplementation(entryName, Fields.PUBLI_CURATED_COUNT);
		pubCount = pfb.getFieldValue(Fields.PUBLI_CURATED_COUNT, Integer.class);
		System.err.println("PUBLI_CURATED_COUNT: " + expectedPubCount + " Now: " + pubCount);
		//Assert.assertEquals(expectedPubCount, pubCount);

		expectedPubCount = (int) getValueForFieldInCurrentSolrImplementation(entryName, Fields.PUBLI_COMPUTED_COUNT);
		pubCount = pfb.getFieldValue(Fields.PUBLI_COMPUTED_COUNT, Integer.class);
		System.err.println("PUBLI_COMPUTED_COUNT: " + expectedPubCount + " Now: " + pubCount);
		//Assert.assertEquals(expectedPubCount, pubCount);

		expectedPubCount = (int) getValueForFieldInCurrentSolrImplementation(entryName, Fields.PUBLI_LARGE_SCALE_COUNT);
		pubCount = pfb.getFieldValue(Fields.PUBLI_LARGE_SCALE_COUNT, Integer.class);
		System.err.println("PUBLI_LARGE_SCALE_COUNT: " + expectedPubCount + " Now: " + pubCount);
		//Assert.assertEquals(expectedPubCount, pubCount);

		expectedPubCount = (int) getValueForFieldInCurrentSolrImplementation(entryName, Fields.INFORMATIONAL_SCORE);
		pubCount = pfb.getFieldValue(Fields.INFORMATIONAL_SCORE, Integer.class);
		System.err.println("INFORMATIONAL_SCORE: " + expectedPubCount + " Now: " + pubCount);
		Assert.assertEquals(expectedPubCount, pubCount);
	}

}
