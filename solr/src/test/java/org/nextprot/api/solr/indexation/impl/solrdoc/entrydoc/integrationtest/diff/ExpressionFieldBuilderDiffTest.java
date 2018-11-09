package org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.integrationtest.diff;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.ExpressionSolrFieldCollector;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

//import org.nextprot.api.core.utils.TerminologyUtils;
//import org.nextprot.api.core.domain.Identifier;

public class ExpressionFieldBuilderDiffTest extends SolrDiffTest {

	@Autowired TerminologyService terminologyService;

	@Ignore
	@Test
	public void testExpression() {

		String[] test_list = {"NX_Q8IWA4", "NX_O00115","NX_Q7Z6P3","NX_E5RQL4","NX_O00115","NX_Q7Z6P3",
				"NX_Q7Z713", "NX_P22102", "NX_Q7Z713", "NX_O00116", "NX_Q7Z713", "NX_O15056"};

		for(int i=0; i < 12; i++){ testExpression(getEntry(test_list[i])); } 
		// for(int i=0; i < 10; i++){	testExpression(getEntry(i));	} // 'random' entries
		
		//Entry entry = getEntry("NX_P20592");
		//Entry entry = getEntry("NX_Q6PK18");
		//testExpression(entry);
	
	}

	public void testExpression(Entry entry) {
		
		String entryName = entry.getUniqueName();

		System.out.println("Testing: " + entryName);
		ExpressionSolrFieldCollector efb = new ExpressionSolrFieldCollector();
		Map<EntrySolrField, Object> fields = new HashMap<>();
		efb.collect(fields, entry, false);
		
		List<String> explist = (List) getValueForFieldInCurrentSolrImplementation(entryName, EntrySolrField.EXPRESSION);
		Set<String> expectedExpression = null;
		Set<String> exprSet = null;
		if(explist != null)  {
			//Get expectedExpression as a Set to remove redundancy
			expectedExpression = new TreeSet<>(explist);
		    exprSet = new TreeSet<String>(getFieldValue(fields, EntrySolrField.EXPRESSION, List.class));
		}
		

		if(expectedExpression != null)
		   if (exprSet.size() < expectedExpression.size()) {
			expectedExpression.removeAll(exprSet);
			Set<String> missingSet = new TreeSet<String>();
			Set<String> finalmissingSet = new TreeSet<String>(expectedExpression);
			for(String expectedvalue : expectedExpression) {
				int i = expectedvalue.indexOf("TS-");
				if(i != -1) { 
					String ts = expectedvalue.substring(i, i+7);
					missingSet.add(ts);
					int j = expectedvalue.indexOf("TS-",i+7);
					if(j != -1) missingSet.add(expectedvalue.substring(j, j+7));
					if(exprSet.containsAll(missingSet)) finalmissingSet.remove(expectedvalue);
					missingSet.clear();
				}
				else if(expectedvalue.contains("</p>")) {
					// like TS-0407</p><p><b>cv_name : </b>Stomach glandular cell</p><p><b>cv_synonyms : </b>Gastric glandular cell",
					// or "Caecum</p><p><b>cv_ancestors : </b>Human anatomical entity",
					String ts2 = expectedvalue.substring(0,expectedvalue.indexOf("</p>"));
					if(exprSet.contains(ts2)) finalmissingSet.remove(expectedvalue);
				}
			}
			if(finalmissingSet.size() > 0) {
			for(String missingvalue : finalmissingSet) System.err.println(missingvalue);
			String msg = "expression in current solr contains " + finalmissingSet.size() + " more data:";
			System.err.println(msg);
			Assert.fail(msg);
			}
			else Assert.assertTrue(true);
		}

	}
}
