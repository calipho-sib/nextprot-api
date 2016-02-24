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

		for (int i = 0; i < 10; i++) {
			Entry entry = getEntry(i);
			testPeptides(entry);
		}
		//Entry entry = getEntry("NX_Q96I99");
		//testPeptides(entry);
	}

	public void testPeptides(Entry entry) {

		String entryName = entry.getUniqueName();
		System.out.println("Testing " + entryName);

		PeptideFieldBuilder pfb = new PeptideFieldBuilder();
		pfb.initializeBuilder(entry);
		Set<String> peptideSet = new TreeSet<String>(pfb.getFieldValue(Fields.PEPTIDE, List.class));
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
