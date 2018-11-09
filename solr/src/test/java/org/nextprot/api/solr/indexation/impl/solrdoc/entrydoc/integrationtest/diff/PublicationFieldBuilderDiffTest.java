package org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.integrationtest.diff;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.PublicationsSolrFieldCollector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class PublicationFieldBuilderDiffTest extends SolrDiffTest {

	// This is the test for the Publication field of the entries core index (npentries1), not the publication index
	@Ignore
	@Test
	public void testPublications() {

		String[] test_list = {"NX_Q8IWA4", "NX_O00115","NX_Q7Z6P3","NX_E5RQL4","NX_P43686","NX_Q7Z6P3",
				"NX_Q7Z713", "NX_P22102", "NX_Q7Z713", "NX_O00116", "NX_Q7Z713", "NX_O15056"};

		for(int i=0; i < test_list.length; i++){ testPublications(getEntry(test_list[i])); }
		// for(int i=0; i < 10; i++){ testPublications(getEntry(i)); } // 'random' entries
		 
		//Entry entry = getEntry("NX_P22102"); // fails
		//Entry entry = getEntry("NX_P61604");
		//testPublications(entry);
	}

	public void testPublications(Entry entry) {
		String entryName = entry.getUniqueName();

		System.out.println("Testing: " + entryName);
		PublicationsSolrFieldCollector pfb = new PublicationsSolrFieldCollector(null);
		Map<EntrySolrField, Object> fields = new HashMap<>();
		pfb.collect(fields, entry, false);
		
		Set<String> expectedPublisRaw = new TreeSet<String>((List) getValueForFieldInCurrentSolrImplementation(entryName, EntrySolrField.PUBLICATIONS));
		Set<String> expectedValues = new TreeSet<String>();

		for (String s : expectedPublisRaw) {
			String indextoken;
			
			// Many values in current index end with spaces -> trimming
			if(s.startsWith("<p>")) {
				// like <p><b>title : </b>Mapping the hallmarks of lung adenocarcinoma with massively parallel sequencing.</p><p><b>journal</b> : Cell - Cell</p><p><b>nlmid:</b>0413066</p><p><b>authors : </b>Imielinski Marcin M",
				indextoken = getValueFromRawData(s,"title");
				if(indextoken != null && indextoken.length() > 1) {
					// Titles from foreign lanuage journals are often enclosed in square brackets, they are stripped in the api but not in current index
					if(indextoken.startsWith("[")) indextoken = indextoken.substring(1, indextoken.length()-1);
					expectedValues.add(removeDoubleSpace(indextoken, "title"));
				}
				indextoken = getValueFromRawData(s,"journal");
				if(indextoken != null) expectedValues.add(indextoken.substring(3)); 
				indextoken = getValueFromRawData(s,"nlmid");
				if(indextoken != null) expectedValues.add(removeDoubleSpace(indextoken, "nlmid"));
				indextoken = getValueFromRawData(s,"authors");
				if(indextoken != null) expectedValues.add(removeDoubleSpace(indextoken,"authors"));
				}
			else if(s.endsWith("</p>")) {
				indextoken = s.substring(0,s.indexOf("</p>")).trim();
				expectedValues.add(removeDoubleSpace(indextoken, "unknown subfield")); // like "Meyerson Matthew M</p>"
			}
			else expectedValues.add(removeDoubleSpace(s, "unknown subfield 2"));
		}
		
		//Set<String> expectedPubliscopy = new TreeSet<String>(expectedPublis);
		/*for(String elem : PublicationSet)
			System.out.println(elem);
		System.err.println(PublicationSet.size() + " elements in the new index"); */
		
		Set<String> tmpset = new TreeSet<String>(getFieldValue(fields, EntrySolrField.PUBLICATIONS, List.class));
		TreeSet<String> PublicationSet = new TreeSet<>();
		for (String s: tmpset) {
			PublicationSet.add(removeDoubleSpace(s, "new index data"));
		}
		
		/* Set<String> PublicationSetcopy = new TreeSet<String>(PublicationSet);
		
		PublicationSet.removeAll(expectedValues);
		System.err.println(PublicationSet.size() + " elements are only in the new index");
		for(String elem : PublicationSet)
			System.out.println(elem);
		expectedValues.removeAll(PublicationSetcopy);
		System.err.println("\n" + expectedValues.size() + " elements are only in the old index");
		for(String elem : expectedValues)
			System.out.println(elem); */
		
		//Assert.assertEquals( expectedValues.size(), PublicationSet.size());
		Assert.assertTrue( PublicationSet.size() >= expectedValues.size());
		//System.err.println("expected: " + expectedValues.size() + " actual: " + PublicationSet.size());
        if(expectedValues.size() < PublicationSet.size()) {
			PublicationSet.removeAll(expectedValues);
			System.err.println("WARNING: (" + entryName + ") " + PublicationSet.size() + " element(s) are only in the new index: " + PublicationSet);
		}
		
		int pubCount, expectedPubCount;
		expectedPubCount = (int) getValueForFieldInCurrentSolrImplementation(entryName, EntrySolrField.PUBLI_CURATED_COUNT);
		pubCount = getFieldValue(fields, EntrySolrField.PUBLI_CURATED_COUNT, Integer.class);
		//System.err.println("PUBLI_CURATED_COUNT: " + expectedPubCount + " Now: " + pubCount);
		Assert.assertEquals(expectedPubCount, pubCount);

		expectedPubCount = (int) getValueForFieldInCurrentSolrImplementation(entryName, EntrySolrField.PUBLI_COMPUTED_COUNT);
		pubCount = getFieldValue(fields, EntrySolrField.PUBLI_COMPUTED_COUNT, Integer.class);
		//System.err.println("PUBLI_COMPUTED_COUNT: " + expectedPubCount + " Now: " + pubCount);
		Assert.assertEquals(expectedPubCount, pubCount);

		expectedPubCount = (int) getValueForFieldInCurrentSolrImplementation(entryName, EntrySolrField.PUBLI_LARGE_SCALE_COUNT);
		pubCount = getFieldValue(fields, EntrySolrField.PUBLI_LARGE_SCALE_COUNT, Integer.class);
		//System.err.println("PUBLI_LARGE_SCALE_COUNT: " + expectedPubCount + " Now: " + pubCount);
		Assert.assertEquals(expectedPubCount, pubCount);

		float expectedScore = (float) getValueForFieldInCurrentSolrImplementation(entryName, EntrySolrField.INFORMATIONAL_SCORE);
		float score = getFieldValue(fields, EntrySolrField.INFORMATIONAL_SCORE, Float.class);
		//System.err.println("INFORMATIONAL_SCORE: " + expectedScore + " Now: " + score);
		Assert.assertEquals(expectedScore, score, 0.001);
	}
	
	private String removeDoubleSpace(String s, String fname) {
		//System.out.println(fname + " avant:<" + s + ">");
		//if (s.contains("Cancer Genome Atlas Research")) System.out.println(fname + " ascii: " + (int)s.charAt(s.length()-2)+ " " + (int)s.charAt(s.length()-1));
		s.trim();
		int lng=s.length();
		while(true) {
			s = s.replaceAll("  ", " ");
			if (s.length()==lng) break;
			lng=s.length();
		}
		//System.out.println(fname + " apres:<" + s + ">");
		//if (s.contains("Cancer Genome Atlas Research")) System.out.println(fname + " ascii: " + (int)s.charAt(s.length()-2) + " " + (int)s.charAt(s.length()-1));
		return s;
	}

}
