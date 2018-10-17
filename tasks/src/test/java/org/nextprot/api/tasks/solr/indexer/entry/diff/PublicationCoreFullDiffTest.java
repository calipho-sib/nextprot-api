package org.nextprot.api.tasks.solr.indexer.entry.diff;

import org.apache.solr.common.SolrInputDocument;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.commons.utils.DateFormatter;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.publication.PublicationType;
import org.nextprot.api.core.service.GlobalPublicationService;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.solr.index.PublicationIndex.PubField;
import org.nextprot.api.tasks.solr.indexer.PublicationSolrindexer;
import org.nextprot.api.tasks.solr.indexer.SolrIndexer;
import org.nextprot.api.tasks.solr.indexer.entry.SolrDiffTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class PublicationCoreFullDiffTest extends SolrDiffTest {

	private SolrIndexer<Publication> pubindexer;

	@Autowired
    private PublicationService publicationService;

    @Autowired
    private GlobalPublicationService globalPublicationService;

	@Before
    public void setup() {
        pubindexer = new PublicationSolrindexer("http://localhost:8983/solr/nppublications1", publicationService);
    }

	@Test
	public void testPublis() {
		for(Long pubid : globalPublicationService.findAllPublicationIds()) {
		  PublicationType pubtype = publicationService.findPublicationById(pubid).getPublicationType();
		  if(pubtype.equals(PublicationType.ARTICLE))
		    testPublicationData(pubid);
		  } 
		
		testPublicationData(7047618); 
		//testPublicationData(710790); // date precision 60 
		//testPublicationData(6725923); // date precision 10
		//testPublicationData(7115344); // not in prev index
	}

	
	public void testPublicationData(long pubid) {
		// Following pub ids have an affiliation which is embedded in the author's field
		// if(pubid == 6850164 || pubid == 37476626 || pubid == 39173492 || pubid == 42825961 || pubid == 6945504 || pubid == 28036837) return;
		String entry = Long.toString(pubid);
		//System.err.println("Testing publi: " + entry);
		SolrInputDocument solrDoc = pubindexer.convertToSolrDocument(publicationService.findPublicationById(pubid));
		if(getValueForFieldInCurrentSolrImplementation(entry, PubField.ID) == null)
		  {
		  System.err.println(entry + " Not in current kant index, pubmed: " + solrDoc.getFieldValue("ac"));
		  return;
		  }
		
		String expected = (String) getValueForFieldInCurrentSolrImplementation(entry, PubField.TYPE);
		//System.err.println("type: " + expected);
		Assert.assertEquals(expected, solrDoc.getFieldValue("type"));
		
		expected = (String) getValueForFieldInCurrentSolrImplementation(entry, PubField.FILTERS);
		//System.err.println("expected: " + expected + " -> " + solrDoc.getFieldValue("filters"));
		Set<String> expectedFilters = new TreeSet<String>(Arrays.asList(expected.split(" ")));
		Set<String> sortedFilters = new TreeSet<String>(Arrays.asList(solrDoc.getFieldValue("filters").toString().split(" ")));
		Assert.assertEquals(expectedFilters, sortedFilters);
		
		expected = new DateFormatter().format((Date) getValueForFieldInCurrentSolrImplementation(entry, PubField.DATE), DateFormatter.DAY_PRECISION);
		//System.err.println("expected date: " + expected + " -> " + solrDoc.getFieldValue("date"));
		Assert.assertEquals(expected, solrDoc.getFieldValue("date").toString());
		
		expected = (String) getValueForFieldInCurrentSolrImplementation(entry, PubField.YEAR);
		//System.err.println("year: " + expected);
		Assert.assertEquals(expected, solrDoc.getFieldValue("year"));
		
		expected = (String) getValueForFieldInCurrentSolrImplementation(entry, PubField.VOLUME);
		System.err.println("volume: " + solrDoc.getFieldValue("volume"));
		Assert.assertEquals(expected, solrDoc.getFieldValue("volume"));
		
		expected = (String) getValueForFieldInCurrentSolrImplementation(entry, PubField.FIRST_PAGE);
		//System.err.println("first_page: " + expected);
		Assert.assertEquals(expected, solrDoc.getFieldValue("first_page"));
		
		expected = (String) getValueForFieldInCurrentSolrImplementation(entry, PubField.LAST_PAGE);
		//System.err.println("last_page: " + expected);
		Assert.assertEquals(expected, solrDoc.getFieldValue("last_page"));
		
		expected = (String) getValueForFieldInCurrentSolrImplementation(entry, PubField.JOURNAL);
		//System.err.println("journal: " + expected);
		Assert.assertEquals(expected, solrDoc.getFieldValue("journal"));
		
		expected = (String) getValueForFieldInCurrentSolrImplementation(entry, PubField.PRETTY_JOURNAL);
		//System.err.println("pretty_journal: " + expected);
		Assert.assertEquals(expected, solrDoc.getFieldValue("pretty_journal"));
		
		expected = (String) getValueForFieldInCurrentSolrImplementation(entry, PubField.TITLE);
		// square brackets around titles means a non-english publication, brackets are trimmed in the new index schema 
		if (expected.startsWith("["))
			expected = expected.substring(1);
		if (expected.endsWith("]"))
			expected = expected.substring(0, expected.length() - 1);
		if(expected.length() > 1)
		  {
		  String penultimate = expected.substring(expected.length() - 2,expected.length() - 1);
		  if(penultimate.equals("]")) // Sometimes the closing bracket is inconstantly placed before the final dot (eg: pubid 10665637)
			 expected = expected.substring(0, expected.length() - 2) + ".";
		  }
		//System.err.println("title: " + expected);
		Assert.assertEquals(expected, solrDoc.getFieldValue("title"));
		
		expected = (String) getValueForFieldInCurrentSolrImplementation(entry, PubField.ABSTRACT);
		//System.err.println("abstract: " + expected);
		Assert.assertEquals(expected, solrDoc.getFieldValue("abstract"));
		
		expected = (String) getValueForFieldInCurrentSolrImplementation(entry, PubField.AC);
		//System.err.println("expected AC: " + expected + " -> " + solrDoc.getFieldValue("ac"));
		//System.err.println("expected AC: " + expected + " -> " + solrDoc.getFieldValue("ac"));
		//Assert.assertEquals(solrDoc.getFieldValue("ac"), expected);

		//expected = (String) getValueForFieldInCurrentSolrImplementation(entry, Fields.SOURCE);
		//System.err.println("expected source: " + expected);
		//Assert.assertEquals(expected, solrDoc.getFieldValue("source"));

		if(getValueForFieldInCurrentSolrImplementation(entry, PubField.AUTHORS) != null) // Some publis have no authors
		{
		List<String> authorList = (List) getValueForFieldInCurrentSolrImplementation(entry, PubField.AUTHORS);
		Set<String> expectedAuthors = new TreeSet<String>();
		for (String author : authorList) expectedAuthors.add(author.replaceAll("  ", " "));
		
		Set<String> sortedAuthors = new TreeSet<String>((List) solrDoc.getFieldValues("authors"));
		//System.err.println(expectedData.size() + " authors");
		Assert.assertEquals(expectedAuthors, sortedAuthors);

		expected = (String) getValueForFieldInCurrentSolrImplementation(entry, PubField.PRETTY_AUTHORS);
		expectedAuthors = new TreeSet<String>(Arrays.asList(expected.split(" \\| ")));
		sortedAuthors = new TreeSet<String>(Arrays.asList(solrDoc.getFieldValue("pretty_authors").toString().split(" \\| ")));
		//System.err.println(expectedData.size() + " authors");
		Assert.assertEquals(expectedAuthors, sortedAuthors);
        }
	}
}
