package org.nextprot.api.tasks.solr.indexer.entry.diff;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.commons.utils.StringUtils;
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
	//@Ignore
	public void testAnnotationsAndFunctionalDescriptions() {

		String[] test_list = {"NX_Q06830", "NX_O00115","NX_Q7Z6P3","NX_E5RQL4","NX_Q14721","NX_Q7Z6P3",
				"NX_Q7Z713", "NX_P22102", "NX_Q7Z713", "NX_Q13286", "NX_Q9UNK4", "NX_Q9NWT6"};

		for(int i=0; i < 12; i++){
			Entry entry = getEntry(test_list[i]); 
			//Entry entry = getEntry(i); // 'random' entry
		    //Entry entry = getEntry("NX_P20592");
			//Entry entry = getEntry("NX_Q15078");
			//Entry entry = getEntry("NX_Q13286"); misses plenty
			System.out.println(entry.getUniqueName());
			testFunctionalDesc(entry);
			testAnnotations(entry);
		}
		
	}

	@SuppressWarnings("unchecked")
	public void testFunctionalDesc(Entry entry) {

		AnnotationFieldBuilder afb = new AnnotationFieldBuilder();
		afb.setTerminologyService(terminologyService);
		afb.initializeBuilder(entry);
		List<String> functionalDescriptions = afb.getFieldValue(Fields.FUNCTION_DESC, List.class);
		List<String> expectedValues = (List<String>) getValueForFieldInCurrentSolrImplementation(entry.getUniqueName(), Fields.FUNCTION_DESC);

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

		AnnotationFieldBuilder afb = new AnnotationFieldBuilder();
		afb.setTerminologyService(terminologyService);
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
