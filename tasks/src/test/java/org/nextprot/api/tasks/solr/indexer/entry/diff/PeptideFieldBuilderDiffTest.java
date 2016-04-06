package org.nextprot.api.tasks.solr.indexer.entry.diff;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.SolrDiffTest;
import org.nextprot.api.tasks.solr.indexer.entry.impl.PeptideFieldBuilder;

public class PeptideFieldBuilderDiffTest extends SolrDiffTest {

	@Test
	public void testPeptides() {

		String[] test_list = {"NX_Q8IWA4", "NX_O00115","NX_Q7Z6P3","NX_E5RQL4","NX_O00115","NX_Q7Z6P3",
				"NX_Q7Z713", "NX_P22102", "NX_Q7Z713", "NX_O00116", "NX_Q7Z713", "NX_O15056"};

		for(int i=0; i < 12; i++){ testPeptides(getEntry(test_list[i])); }
		//for(int i=0; i < 80; i++){ testPeptides(getEntry(i)); } // 'random' entries

		//Entry entry = getEntry("NX_P43686");
		//testPeptides(entry);
	}

	public void testPeptides(Entry entry) {

		String entryName = entry.getUniqueName();
		System.out.println("Testing " + entryName);

		PeptideFieldBuilder pfb = new PeptideFieldBuilder();
		pfb.initializeBuilder(entry);
		List<String> peptideList = (List) getValueForFieldInCurrentSolrImplementation(entryName, Fields.PEPTIDE);
		if(peptideList == null) return; // No peptides in this entry
		
		Set<String> peptideSet = new TreeSet<String>(peptideList);
		Set<String> expectedPeptideSet = new TreeSet<String>((List) getValueForFieldInCurrentSolrImplementation(entryName, Fields.PEPTIDE));

		if (expectedPeptideSet.size() > peptideSet.size()) {
			expectedPeptideSet.removeAll(peptideSet);
			String msg = "Expected peptides contains more data: " + expectedPeptideSet;
			System.err.println(msg);
			Assert.fail(msg);
		}

		if (peptideSet.size() > expectedPeptideSet.size()) {
			peptideSet.removeAll(expectedPeptideSet);
			String msg = "Peptides contains more data: " + peptideSet;
			System.err.println(msg);
			Assert.fail(msg);
		} 

		Assert.assertEquals(peptideSet.size(), expectedPeptideSet.size());
		Assert.assertEquals(peptideSet, expectedPeptideSet);
	}

}
