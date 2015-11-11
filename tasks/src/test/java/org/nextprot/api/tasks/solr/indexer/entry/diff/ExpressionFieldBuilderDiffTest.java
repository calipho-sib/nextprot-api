package org.nextprot.api.tasks.solr.indexer.entry.diff;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.TerminologyService;
//import org.nextprot.api.core.utils.TerminologyUtils;
//import org.nextprot.api.core.domain.Identifier;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.SolrDiffTest;
import org.nextprot.api.tasks.solr.indexer.entry.impl.ExpressionFieldBuilder;
import org.springframework.beans.factory.annotation.Autowired;
//import org.nextprot.api.tasks.solr.indexer.entry.impl.IdentifierFieldBuilder;
//import org.nextprot.api.tasks.solr.indexer.entry.impl.OverviewFieldBuilder;
//import org.nextprot.api.tasks.solr.indexer.entry.impl.XrefFieldBuilder;

public class ExpressionFieldBuilderDiffTest extends SolrDiffTest {

	@Autowired TerminologyService terminologyService;

	@Test
	public void testExpression() {

		for(int i=0; i < 10; i++){
			testExpression(getEntry(i)); 
		} 
		
		//Entry entry = getEntry("NX_P20592");
		//Entry entry = getEntry("NX_Q6PK18");
		//testExpression(entry);
	
	}

	
	public void testExpression(Entry entry) {
		
		String entryName = entry.getUniqueName();

		System.out.println("Testing: " + entryName);
		ExpressionFieldBuilder efb = new ExpressionFieldBuilder();
		efb.setTerminologyservice(terminologyService);
		efb.initializeBuilder(entry);
		
		// Xrefs to HPA antibodies temporarily not in the API
		/*List<String> expectedABs = (List) getValueForFieldInCurrentSolrImplementation(entryName, Fields.ANTIBODY);
		if(expectedABs != null) {
		  Collections.sort(expectedABs);
		  List<String> currentABs = xfb.getFieldValue(Fields.ANTIBODY, List.class);
		  Collections.sort(currentABs);
		  Assert.assertEquals(expectedABs, currentABs);
		}*/
		

		List<String> explist = (List) getValueForFieldInCurrentSolrImplementation(entryName, Fields.EXPRESSION);
		Set<String> expectedExpression = null;
		Set<String> exprSet = null;
		if(explist != null)  {
			//Get expectedExpression as a Set to remove redundancy
			expectedExpression = new TreeSet<String>(explist);
		    exprSet = new TreeSet<String>(efb.getFieldValue(Fields.EXPRESSION, List.class));
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
