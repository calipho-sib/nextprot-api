package org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.diff;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.AnnotationSolrFieldCollector;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.CVSolrFieldCollector;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.ExpressionSolrFieldCollector;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.InteractionSolrFieldCollector;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.SolrDiffTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class AnnotationFieldBuilderGoldDiffTest extends SolrDiffTest {

	@Autowired
	TerminologyService terminologyService;

	@Ignore
	@Test
	public void testGoldAnnotations() {
		// Since the functional tests are already done in the various fieldbuilder diff tests we will only test
		// the fields that have quality qualifiers (GOs, PTMs, variants and expression...

		String[] test_list = {"NX_Q13158", "NX_Q06830","NX_Q7Z6P3","NX_E5RQL4","NX_Q14721","NX_Q7Z6P3",
				"NX_Q7Z713", "NX_P22102", "NX_O00115", "NX_Q13286", "NX_Q9UNK4", "NX_Q9NWT6"};

		for(int i=0; i < test_list.length; i++){
		//for(int i=0; i < 120; i++){
			Entry entry = getEntry(test_list[i]); 
			//Entry entry = getEntry(i); // 'random' entry
		    //Entry entry = getEntry("NX_P20592");
			System.out.println(entry.getUniqueName());
			testGoldAnnotations(entry);
		}
		
	}

	@SuppressWarnings("unchecked")
	public void testGoldAnnotations(Entry entry) {

		String entryName = entry.getUniqueName();
		
		// Variants
		AnnotationSolrFieldCollector afb = new AnnotationSolrFieldCollector();
		Map<EntrySolrField, Object> fields = new HashMap<>();

		afb.collect(fields, entry, true);
		Integer oldgoldvarcnt = 0, newgoldvarcnt = 0;
		List<String> expectedRawValues = (List<String>) getValueForFieldInCurrentGoldSolrImplementation(entryName, EntrySolrField.ANNOTATIONS);
		for(String rawAnnot : expectedRawValues)
			if(rawAnnot.contains(">sequence variant<"))
				oldgoldvarcnt++;
		List<String> annotations = getFieldValue(fields, EntrySolrField.ANNOTATIONS, List.class);
		for (String s : annotations)
			if(s.startsWith("Variant"))
				newgoldvarcnt++;
		Assert.assertEquals(oldgoldvarcnt, newgoldvarcnt);

		// Expression
		ExpressionSolrFieldCollector efb = new ExpressionSolrFieldCollector();
		fields.clear();
		efb.collect(fields, entry, true);
		List<String> explist = (List) getValueForFieldInCurrentGoldSolrImplementation(entryName, EntrySolrField.EXPRESSION);
		Set<String> expectedCVSet = new TreeSet<String>();
		Set<String> expressionCVSet = new TreeSet<String>();
		Set<String> exprSet = null;
		if(explist != null) {
		  exprSet = new TreeSet<String>(getFieldValue(fields, EntrySolrField.EXPRESSION, List.class));
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
		List<String> expectedInteractions = (List) getValueForFieldInCurrentGoldSolrImplementation(entryName, EntrySolrField.INTERACTIONS);
		if(expectedInteractions != null) {
			Integer oldcnt = 0, newcnt = 0;
			InteractionSolrFieldCollector ifb = new InteractionSolrFieldCollector();
			fields.clear();
			ifb.collect(fields, entry, true);
			Set<String> itSet = new TreeSet<String>(getFieldValue(fields, EntrySolrField.INTERACTIONS, List.class));
			for(String intactIt : expectedInteractions) if(intactIt.startsWith("<p>Interacts")) oldcnt++;
			for(String newintactIt : itSet) if(newintactIt.startsWith("AC:") || newintactIt.equals("selfInteraction")) newcnt++;
			// There may be one more interaction in the new index (the subunit annotation)
			Assert.assertEquals(oldcnt, newcnt);
		}
		
        // CVs
		Set<String> expectedCVs = new TreeSet<>((List) getValueForFieldInCurrentGoldSolrImplementation(entryName, EntrySolrField.CV_ACS));
		CVSolrFieldCollector cfb = new CVSolrFieldCollector();
		fields.clear();
		cfb.collect(fields, entry, true);
		Set<String> CvSet = new TreeSet<>(getFieldValue(fields, EntrySolrField.CV_ACS, List.class));
        Assert.assertTrue(expectedCVs.size() == CvSet.size());
	}

}
