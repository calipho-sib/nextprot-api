package org.nextprot.api.tasks.solr.indexer.entry.diff;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.SolrDiffTest;
import org.nextprot.api.tasks.solr.indexer.entry.impl.AnnotationFieldBuilder;
import org.springframework.beans.factory.annotation.Autowired;

public class AnnotationFieldBuilderDiffTest extends SolrDiffTest {

	@Autowired
	private EntryBuilderService entryBuilderService = null;
	@Autowired
	private MasterIdentifierService masterIdentifierService = null;
	@Autowired
	TerminologyService terminologyService;

	@Test
	public void testAnnotationsAndFunctionalDescriptions() {

		for(int i=0; i < 1; i++){
			//Entry entry = getEntry(i);
			Entry entry = getEntry("NX_P11532");
			//Entry entry = getEntry("P42680");
			System.out.println(entry.getUniqueName());
			//testFunctionalDesc(entry);
			testAnnotations(entry);
		}
		
	}

	@SuppressWarnings("unchecked")
	public void testFunctionalDesc(Entry entry) {

		AnnotationFieldBuilder afb = new AnnotationFieldBuilder();
		afb.setTerminologyservice(terminologyService);
		afb.initializeBuilder(entry);
		List<String> functionalDescriptions = afb.getFieldValue(Fields.FUNCTION_DESC, List.class);

		List<String> expectedValues = (List<String>) getValueForFieldInCurrentSolrImplementation(entry.getUniqueName(), Fields.FUNCTION_DESC);

		if (!((expectedValues == null) && (functionalDescriptions == null))) {
			//System.out.println(expectedValues);
			//System.err.println(functionalDescriptions);
			//assertEquals(functionalDescriptions.size(), expectedValues.size());
			// Only one functionalDescription is indexed in current solr implementation (eg: NX_P02751)
			if (!functionalDescriptions.isEmpty()) {
				//assertEquals(functionalDescriptions.get(0), expectedValues.get(0));
			}
		}

	}

	@SuppressWarnings("unchecked")
	public void testAnnotations(Entry entry) {

		AnnotationFieldBuilder afb = new AnnotationFieldBuilder();
		afb.setTerminologyservice(terminologyService);
		afb.initializeBuilder(entry);

		List<String> annotations = afb.getFieldValue(Fields.ANNOTATIONS, List.class);
		List<String> expectedRawValues = (List<String>) getValueForFieldInCurrentSolrImplementation(entry.getUniqueName(), Fields.ANNOTATIONS);
		List<String> expectedValues = new ArrayList<String>();

		for (String s : expectedRawValues) {
			String aux = getValueFromRawData(s,"description");
			if(aux != null) //System.err.println("extracting desc from: " + s);
			  expectedValues.add(aux);
			aux = getValueFromRawData(s,"an_synonyms");
			if(aux != null) //System.err.println("extracting an_synonym from: " + s);
			  expectedValues.add(getSortedValueFromPipeSeparatedField(aux));
			aux = getValueFromRawData(s,"sequence_variant_mutation_aa");
			if(aux != null) 
			  expectedValues.add(getSortedValueFromPipeSeparatedField(aux));
			aux = getValueFromRawData(s,"cv_ancestors");
			if(aux != null) 
			  expectedValues.add(getSortedValueFromPipeSeparatedField(aux));
			aux = getValueFromRawData(s,"cv_ac");
			if(aux != null) 
			  expectedValues.add(getSortedValueFromPipeSeparatedField(aux));
			aux = getValueFromRawData(s,"cv_name");
			if(aux != null) 
			  expectedValues.add(getSortedValueFromPipeSeparatedField(aux));
			aux = getValueFromRawData(s,"cv_synonyms");
			if(aux != null) 
			  expectedValues.add(getSortedValueFromPipeSeparatedField(aux));
			aux = getValueFromRawData(s,"sequence_caution_conflict_type");
			if(aux != null) 
			  expectedValues.add(getSortedValueFromPipeSeparatedField(aux));
		}

		Set<String> expectedValues2 = new TreeSet<String>(expectedValues);
		Set<String> annotations2 = new TreeSet<String>(annotations);
		Set<String> annotations3 = new TreeSet<String>(annotations);

		annotations2.removeAll(expectedValues2);
		System.err.println("Only in current (" + annotations2.size() + ") : " + annotations2);

		expectedValues2.removeAll(annotations3);
		System.err.println("Only in previous solr (" + expectedValues2.size() + ") : " + expectedValues2);
		//assertEquals(annotations.size(), expectedValues.size());

		// TODO remove "reference proteome", unless already in stopwords
		for (int i = 0; i < annotations.size(); i++) {
			//System.err.println(annotations.get(i));
			//assertEquals(annotations.get(i), expectedValues.get(i));
		}
		//System.err.println(expectedValues);
		assertEquals(expectedValues.size(), annotations.size());
	}

	@Test
	public void testCleanRawData() {
		String result = getValueFromRawData("<p><b>anno_name : </b>caution</p><p><b>anno_qualname : </b>GOLD</p><p><b>description : </b>Product of a dubious CDS prediction.</p>","description");
		assertEquals("Product of a dubious CDS prediction.", result);
	}

	@Test
	public void testSortedValueFromPipeSeparatedField() {
		String result = getSortedValueFromPipeSeparatedField("cosmic:COSM4859577 | cosmic:COSM1149023 | cosmic:COSM720040");
		assertEquals("cosmic:COSM1149023 | cosmic:COSM4859577 | cosmic:COSM720040", result);
	}

}
