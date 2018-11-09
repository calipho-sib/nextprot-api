package org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.integrationtest.diff;

import org.junit.Test;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.AnnotationSolrFieldCollector;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;

public class AnnotationFieldBuilderDiffTest extends SolrDiffTest {

	@Autowired
	TerminologyService terminologyService;

	@Test
	public void testAnnotationsAndFunctionalDescriptions() {

		String[] test_list = {"NX_Q8IWA4", "NX_O00115","NX_Q7Z6P3","NX_E5RQL4","NX_Q12809","NX_Q7Z6P3","NX_Q7Z713",  "NX_P35499",
				"NX_P22102", "NX_Q8IYV9", "NX_O00116", "NX_Q7Z713", "NX_O15056", "NX_P35498", "NX_Q99250","NX_Q9NY46", "NX_P43246", 
				"NX_Q9UQD0", "NX_P52701", "NX_P54278"};

		String[] BEDtest_list = {"NX_P35498", "NX_Q99250","NX_Q9NY46", "NX_P35499", "NX_Q14524", "NX_Q01118","NX_Q9UQD0", "NX_Q15858", "NX_Q9Y5Y9", "NX_Q9UI33",
				"NX_P38398", "NX_P51587","NX_P16422", "NX_P40692", "NX_Q9UHC1", "NX_P43246", "NX_P52701", "NX_P54278"};
		
		for(int i=0; i < test_list.length; i++){
			Entry entry = getEntry(test_list[i]); 
			//Entry entry = getEntry(i); // 'random' entry
			System.out.println(entry.getUniqueName());
			testFunctionalDesc(entry);
			testAnnotations(entry);
		}
		
	}

	@SuppressWarnings("unchecked")
	public void testFunctionalDesc(Entry entry) {

		AnnotationSolrFieldCollector afb = new AnnotationSolrFieldCollector();

		Map<EntrySolrField, Object> fields = new HashMap<>();
		afb.collect(fields, entry, false);
		List<String> functionalDescriptions = getFieldValue(fields, EntrySolrField.FUNCTION_DESC, List.class);
		List<String> expectedValues = (List<String>) getValueForFieldInCurrentSolrImplementation(entry.getUniqueName(), EntrySolrField.FUNCTION_DESC);

		if (!((expectedValues == null) && (functionalDescriptions == null))) {
			//System.err.println("exp: " + expectedValues + "\nact: " + functionalDescriptions);
			assert(functionalDescriptions.size() >= expectedValues.size());
			//assertEquals(functionalDescriptions.size(), expectedValues.size());
			// Only one functionalDescription is indexed in current solr implementation (eg: NX_P02751)
			if (!functionalDescriptions.isEmpty()) {
				assert(functionalDescriptions.contains(expectedValues.get(0)));
			}
		}

	}

	@SuppressWarnings("unchecked")
	public void testAnnotations(Entry entry) {

		AnnotationSolrFieldCollector afb = new AnnotationSolrFieldCollector();

		Map<EntrySolrField, Object> fields = new HashMap<>();
		afb.collect(fields, entry, false);

		List<String> annotations = getFieldValue(fields, EntrySolrField.ANNOTATIONS, List.class);
		List<String> expectedRawValues = (List<String>) getValueForFieldInCurrentSolrImplementation(entry.getUniqueName(), EntrySolrField.ANNOTATIONS);
		List<String> expectedValues = new ArrayList<String>();

		for (String s : expectedRawValues) {
			String aux = getValueFromRawData(s,"description");
			if(aux != null) //System.err.println("extracting desc from: " + s);
			  expectedValues.add(aux);
			aux = getValueFromRawData(s,"an_synonyms");
			if(aux != null) //System.err.println("extracting an_synonym from: " + s);
			  expectedValues.add(StringUtils.getSortedValueFromPipeSeparatedField(aux));
			aux = getValueFromRawData(s,"sequence_variant_mutation_aa");
			if(aux != null) 
			  expectedValues.add(StringUtils.getSortedValueFromPipeSeparatedField(aux));
			aux = getValueFromRawData(s,"cv_ancestors");
			if(aux != null) 
			  expectedValues.add(StringUtils.getSortedValueFromPipeSeparatedField(aux));
			aux = getValueFromRawData(s,"cv_ac");
			if(aux != null) 
			  expectedValues.add(StringUtils.getSortedValueFromPipeSeparatedField(aux));
			aux = getValueFromRawData(s,"cv_name");
			if(aux != null) 
			  expectedValues.add(StringUtils.getSortedValueFromPipeSeparatedField(aux));
			aux = getValueFromRawData(s,"cv_synonyms");
			if(aux != null) 
			  expectedValues.add(StringUtils.getSortedValueFromPipeSeparatedField(aux));
			aux = getValueFromRawData(s,"sequence_caution_conflict_type");
			if(aux != null) 
			  expectedValues.add(StringUtils.getSortedValueFromPipeSeparatedField(aux));
		}

		Set<String> expectedValues2 = new TreeSet<String>(expectedValues);
		Set<String> annotations2 = new TreeSet<String>(annotations);
		//Set<String> annotationsSet = new TreeSet<String>(annotations);
		//for(String annot : annotations2)	System.err.println(annot);
		Set<String> annotations3 = new TreeSet<String>(annotations);
		String bigbasket = annotations3.toString().toLowerCase();

		//System.err.println("current: " + expectedValues2.size() + " new: " + annotations2.size());
		annotations2.removeAll(expectedValues2);
		//System.err.println("Only in current (" + annotations2.size() + ") : " + annotations2);

		expectedValues2.removeAll(annotations3);
		//System.err.println("Only in previous solr (" + expectedValues2.size() + ") : " + expectedValues2);
		for(String annot : expectedValues2) {
			//System.err.println("annot: " + annot);
			//f(annot.contains("|")) {System.err.println(annot); for(String token : annot.split(" \\| ")) if(!annotations3.contains(token)) System.err.println("MISS: " + token);}
			if(annot.contains("|")) {
				for(String token : annot.split(" \\| ")) if(!bigbasket.contains(token.toLowerCase())) System.err.println("MISS token: " + token);
				}
			// we miss the useless feature numbering/naming for TMs, etc
			//else if(!annotations3.contains(annot))
			else if(!bigbasket.contains(annot.toLowerCase()))
				if((!annot.contains("In Ref")) && (!annot.startsWith("PRO_")) && annot.length() > 2) System.err.println("MISS: " + annot);
		}

		//System.err.println(expectedValues);
		//assertEquals(expectedValues.size(), annotations.size());
		//for(String annot : annotations3) {	System.err.println(annot); }
		//System.err.println(bigbasket);
		assert(expectedValues.size() <= annotations.size());
	}

	@Test
	public void testCleanRawData() {
		String result = getValueFromRawData("<p><b>anno_name : </b>caution</p><p><b>anno_qualname : </b>GOLD</p><p><b>description : </b>Product of a dubious CDS prediction.</p>","description");
		assertEquals("Product of a dubious CDS prediction.", result);
	}


}
