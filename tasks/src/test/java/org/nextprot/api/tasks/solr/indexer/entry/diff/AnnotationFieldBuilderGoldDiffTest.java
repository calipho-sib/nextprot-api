package org.nextprot.api.tasks.solr.indexer.entry.diff;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.SolrDiffTest;
import org.nextprot.api.tasks.solr.indexer.entry.impl.AnnotationFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.impl.CVFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.impl.ExpressionFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.impl.InteractionFieldBuilder;
import org.springframework.beans.factory.annotation.Autowired;

public class AnnotationFieldBuilderGoldDiffTest extends SolrDiffTest {

	@Autowired
	private EntryBuilderService entryBuilderService = null;
	@Autowired
	private MasterIdentifierService masterIdentifierService = null;
	@Autowired
	TerminologyService terminologyService;

	@Test
	public void testGoldAnnotations() {
		// Since the functional tests are already done in the various fieldbuilder diff tests we will only test
		// the fields that have quality qualifiers (GOs, PTMs, variants and expression...

		String[] test_list = {"NX_Q13158", "NX_Q06830","NX_Q7Z6P3","NX_E5RQL4","NX_Q14721","NX_Q7Z6P3",
				"NX_Q7Z713", "NX_P22102", "NX_O00115", "NX_Q13286", "NX_Q9UNK4", "NX_Q9NWT6"};

		//for(int i=0; i < test_list.length; i++){
		for(int i=0; i < 120; i++){
			//Entry entry = getEntry(test_list[i]); 
			Entry entry = getEntry(i); // 'random' entry
		    //Entry entry = getEntry("NX_P20592");
			System.out.println(entry.getUniqueName());
			testGoldAnnotations(entry);
		}
		
	}

	@SuppressWarnings("unchecked")
	public void testGoldAnnotations(Entry entry) {

		String entryName = entry.getUniqueName();
		
		AnnotationFieldBuilder afb = new AnnotationFieldBuilder();
		afb.setGold(true);
		afb.setTerminologyService(terminologyService);
		afb.initializeBuilder(entry);

		List<String> annotations = afb.getFieldValue(Fields.ANNOTATIONS, List.class);
		List<String> expectedRawValues = (List<String>) getValueForFieldInCurrentGoldSolrImplementation(entryName, Fields.ANNOTATIONS);
		List<String> expectedValues = new ArrayList<String>();

		for (String s : expectedRawValues) {
			String aux = getValueFromRawData(s,"an_synonyms");
			if(aux != null) 
			  expectedValues.add(StringUtils.getSortedValueFromPipeSeparatedField(aux));
			// check that we have the VAR_ ids (gold) and check that we have no annotation:cosmic*
			//aux = getValueFromRawData(s,"cv_ac");
			//if(aux != null) 
			  //expectedValues.add(StringUtils.getSortedValueFromPipeSeparatedField(aux));
		}

		Set<String> expectedValues2 = new TreeSet<String>(expectedValues);
		Set<String> annotations2 = new TreeSet<String>(annotations);
		Set<String> annotations3 = new TreeSet<String>(annotations);
		String bigbasket = annotations3.toString().toLowerCase();

		Assert.assertFalse(bigbasket.matches("cosm[0-9]*")); // All cosmic variants are silver

		System.err.println("current: " + expectedValues2);
		annotations2.removeAll(expectedValues2);
		System.err.println("Only in current (" + annotations2.size() + ") : " + annotations2);

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
		}
		//System.err.println("previous: " + expectedValues.size() + " current: " + annotations.size());
		assert(expectedValues.size() == annotations.size());
		Assert.assertFalse(bigbasket.matches("cosm[0-9]*"));
		
		// Expression
		ExpressionFieldBuilder efb = new ExpressionFieldBuilder();
		efb.setTerminologyService(terminologyService);
		efb.setGold(true);
		efb.initializeBuilder(entry);
		List<String> explist = (List) getValueForFieldInCurrentGoldSolrImplementation(entryName, Fields.EXPRESSION);
		Set<String> expectedCVSet = new TreeSet<String>();
		Set<String> expressionCVSet = new TreeSet<String>();
		Set<String> exprSet = null;
		if(explist != null) {
		  exprSet = new TreeSet<String>(efb.getFieldValue(Fields.EXPRESSION, List.class));
		  // Consider only tissue CVs
		  for (String s : explist) {
			if(s.startsWith("TS-"))
				expectedCVSet.add(s.split("<")[0]);
			String aux = getValueFromRawData(s,"cv_ac");
			if(aux != null) 
				expectedCVSet.add(StringUtils.getSortedValueFromPipeSeparatedField(aux));
			aux = getValueFromRawData(s,"cv_ancestors_acs");
			if(aux != null) 
				expectedCVSet.add(StringUtils.getSortedValueFromPipeSeparatedField(aux));
			//else System.err.println("no ancestor in: " + s);
		    }
		  for (String s2 : exprSet) {
			 if(s2.startsWith("TS-")) 
				expressionCVSet.add(s2);
		  }
		}
		//System.err.println("expect: " + expectedCVSet.size() + " now: " + expressionCVSet.size());
		//expressionCVSet.removeAll(expectedCVSet);
		//System.err.println("only in current: " + expressionCVSet);
		Assert.assertEquals(expectedCVSet.size(), expressionCVSet.size());
		
		// Interactions
		List<String> expectedInteractions = (List) getValueForFieldInCurrentGoldSolrImplementation(entryName, Fields.INTERACTIONS);
		if(expectedInteractions != null) {
			Integer oldcnt = 0, newcnt = 0;
			InteractionFieldBuilder ifb = new InteractionFieldBuilder();
			ifb.setTerminologyService(terminologyService);
			ifb.setEntryBuilderService(entryBuilderService);
			ifb.setGold(true);
			ifb.initializeBuilder(entry);
			Set<String> itSet = new TreeSet<String>(ifb.getFieldValue(Fields.INTERACTIONS, List.class));
			for(String intactIt : expectedInteractions) if(intactIt.startsWith("<p>Interacts")) oldcnt++;
			for(String newintactIt : itSet) if(newintactIt.startsWith("AC:") || newintactIt.equals("selfInteraction")) newcnt++;
			// There may be one more interaction in the new index (the subunit annotation)
			Assert.assertEquals(oldcnt, newcnt);
		}
		
        // CVs
		Set<String> expectedCVs = new TreeSet<String>((List) getValueForFieldInCurrentGoldSolrImplementation(entryName, Fields.CV_ACS));
		CVFieldBuilder cfb = new CVFieldBuilder();
		cfb.setTerminologyService(terminologyService);
		cfb.setGold(true);
		cfb.initializeBuilder(entry);
		Set<String> CvSet = new TreeSet<String>(cfb.getFieldValue(Fields.CV_ACS, List.class));
        Assert.assertTrue(expectedCVs.size() == CvSet.size());
	}

}
