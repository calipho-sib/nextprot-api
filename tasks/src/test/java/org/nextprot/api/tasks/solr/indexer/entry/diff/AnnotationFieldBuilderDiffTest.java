package org.nextprot.api.tasks.solr.indexer.entry.diff;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.SolrDiffTest;
import org.nextprot.api.tasks.solr.indexer.entry.impl.AnnotationFieldBuilder;
import org.springframework.beans.factory.annotation.Autowired;

public class AnnotationFieldBuilderDiffTest extends SolrDiffTest {

	@Autowired
	private EntryBuilderService entryBuilderService = null;
	@Autowired
	private MasterIdentifierService masterIdentifierService = null;

	@Test
	public void testAnnotationsAndFunctionalDescriptions() {

		for(int i=0; i < 1; i++){
			Entry entry = getEntry(i);
			System.out.println(entry.getUniqueName());
			testFunctionalDesc(entry);
			testAnnotations(entry);
		}
	}

	@SuppressWarnings("unchecked")
	public void testFunctionalDesc(Entry entry) {

		AnnotationFieldBuilder afb = new AnnotationFieldBuilder();
		afb.initializeBuilder(entry);
		List<String> functionalDescriptions = afb.getFieldValue(Fields.FUNCTION_DESC, List.class);

		List<String> expectedValues = (List<String>) getValueForFieldInCurrentSolrImplementation(entry.getUniqueName(), Fields.FUNCTION_DESC);

		if (!((expectedValues == null) && (functionalDescriptions == null))) {
			assertEquals(functionalDescriptions.size(), expectedValues.size());
			if (!functionalDescriptions.isEmpty()) {
				assertEquals(functionalDescriptions.get(0), expectedValues.get(0));
			}
		}

	}

	@SuppressWarnings("unchecked")
	public void testAnnotations(Entry entry) {

		AnnotationFieldBuilder afb = new AnnotationFieldBuilder();
		afb.initializeBuilder(entry);

		List<String> annotations = afb.getFieldValue(Fields.ANNOTATIONS, List.class);
		List<String> expectedRawValues = (List<String>) getValueForFieldInCurrentSolrImplementation(entry.getUniqueName(), Fields.ANNOTATIONS);
		List<String> expectedValues = new ArrayList<String>();

		for (String s : expectedRawValues) {
			String aux = getValueFromRawData(s,"description");
			if(aux != null) //System.err.println("extracting desc from: " + s);
			  expectedValues.add(aux);
		}

		Collections.sort(annotations);
		Collections.sort(expectedValues);

		assertEquals(annotations.size(), expectedValues.size());

		// TODO remove "reference proteome", unless already in stopwords
		for (int i = 0; i < annotations.size(); i++) {
			assertEquals(annotations.get(i), expectedValues.get(i));
		}

	}

	@Test
	public void testCleanRawData() {
		String result = getValueFromRawData("<p><b>anno_name : </b>caution</p><p><b>anno_qualname : </b>GOLD</p><p><b>description : </b>Product of a dubious CDS prediction.</p>","description");
		assertEquals("Product of a dubious CDS prediction.", result);
	}

}
