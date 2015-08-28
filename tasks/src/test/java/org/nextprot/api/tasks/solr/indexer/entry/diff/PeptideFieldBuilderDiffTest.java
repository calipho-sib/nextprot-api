package org.nextprot.api.tasks.solr.indexer.entry.diff;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.SolrDiffTest;
import org.nextprot.api.tasks.solr.indexer.entry.impl.PeptideFieldBuilder;
import org.springframework.beans.factory.annotation.Autowired;

public class PeptideFieldBuilderDiffTest extends SolrDiffTest {

	@Autowired	private EntryBuilderService entryBuilderService = null;
	@Autowired	private MasterIdentifierService masterIdentifierService = null;
	
	@Test
	public void testPeptides() {
		
		Set<String> entries = masterIdentifierService.findUniqueNames();
		Iterator<String> entriesIt = entries.iterator();

		int i = 0;
		while(entriesIt.hasNext() && i < 1){
			i++;
			//String entryName = entriesIt.next();
			String entryName = "NX_Q96I99";
			Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryName).withEverything());
			testPeptides(entryName, entry);
		}
	
	}

	
	public void testPeptides(String entryName, Entry entry) {
		
		System.err.println(entryName);
		
		Fields field = Fields.PEPTIDE;

		PeptideFieldBuilder pfb = new PeptideFieldBuilder();
		pfb.initializeBuilder(entry);
		List<String> peptides = pfb.getFieldValue(field, List.class);
		List<String> rawPeptides = (List) getValueForFieldInCurrentSolrImplementation(entryName, field);

		Set<String> peptideSet = new TreeSet<String>(peptides);
		Set<String> rawPeptideSet = new TreeSet<String>(rawPeptides);

		//  On Kant there are some PubMed Ids taken as well, why? Does this make sense? See with PAM
		/*NX_Q96I99
		265
		262
		[PubMed:19413330, PubMed:21139048, PubMed:23236377]
		*/	
		if(rawPeptideSet.size() > peptideSet.size()){
			rawPeptideSet.removeAll(peptideSet);
			String msg = "Raw peptides contains more data: " + rawPeptideSet;
			System.err.println(msg); Assert.fail(msg);
		}

		if(peptideSet.size() > rawPeptides.size()){
			peptideSet.removeAll(rawPeptideSet);
			String msg = "Peptides contains more data: " + peptideSet;
			System.err.println(msg); Assert.fail(msg);
		}
		
		Assert.assertEquals(peptideSet.size(), rawPeptideSet.size());
		Assert.assertEquals(peptideSet, rawPeptideSet);

	}
	
}
