package org.nextprot.api.tasks.solr.indexer.entry.diff;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.SolrDiffTest;
import org.nextprot.api.tasks.solr.indexer.entry.impl.NamesFieldBuilder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class NamesFieldBuilderDiffTest extends SolrDiffTest {


	@Test
	public void testNames() {
		String[] test_list = {"NX_Q8IWA4", "NX_O00115","NX_Q7Z6P3","NX_E5RQL4","NX_Q12809","NX_Q7Z6P3",
				"NX_Q7Z713", "NX_P22102", "NX_Q8IYV9", "NX_O00116", "NX_Q7Z713", "NX_O15056"};

		for(int i=0; i < test_list.length; i++){ testNames(getEntry(test_list[i])); }
		//for(int i=0; i < 80; i++){ testNames(getEntry(i)); } // 'random' entries

		//Entry entry = getEntry("NX_P62805");
		//testNames(entry);
	}

	public void testNames(Entry entry) {
		String entryName = entry.getUniqueName();

		System.out.println("Testing: " + entryName);
		NamesFieldBuilder nfb = new NamesFieldBuilder();
		nfb.initializeBuilder(entry);
		
		// RECOMMENDED_NAME are indexed and tested with the overviewFieldBuilder
		String expectedGenenames = (String) getValueForFieldInCurrentSolrImplementation(entryName, Fields.RECOMMENDED_GENE_NAMES);
		String nowGenenames = nfb.getFieldValue(Fields.RECOMMENDED_GENE_NAMES, String.class);
		
		List<String> altnamelist = (List) getValueForFieldInCurrentSolrImplementation(entryName, Fields.ALTERNATIVE_NAMES);
		Set<String> expectedAltnames = null;
		Set<String> AltenameSet = null;
		if(altnamelist != null)  {
			expectedAltnames = new TreeSet<String>(altnamelist);
			AltenameSet = new TreeSet<String>(nfb.getFieldValue(Fields.ALTERNATIVE_NAMES, List.class));
			//System.err.println("expected: " + expectedAltnames);
			//System.err.println(AltenameSet);
			if(AltenameSet.size() > expectedAltnames.size()) {
				AltenameSet.removeAll(expectedAltnames);
				System.err.println("WARNING: " + AltenameSet + " should also be indexed as an ALTERNATIVE_NAMES token");
			}
			else Assert.assertEquals(expectedAltnames.size(), AltenameSet.size());
		}

		List<String> altgenlist = (List) getValueForFieldInCurrentSolrImplementation(entryName, Fields.ALTERNATIVE_GENE_NAMES);
		Set<String> expectedAltGenename = null;
		Set<String> AltGenenameSet = null;
		if(altgenlist != null)  {
			expectedAltGenename = new TreeSet<String>(altgenlist);
			AltGenenameSet = new TreeSet<String>(nfb.getFieldValue(Fields.ALTERNATIVE_GENE_NAMES, List.class));
			//System.err.println(expectedAltGenename);
			//System.err.println(AltGenenameSet);
			Assert.assertEquals(expectedAltGenename.size(), AltGenenameSet.size());
		}

		List<String>  expectedCD = (List) getValueForFieldInCurrentSolrImplementation(entryName, Fields.CD_ANTIGEN);
		if(expectedCD != null)  {
			Assert.assertEquals(expectedCD, nfb.getFieldValue(Fields.CD_ANTIGEN, List.class));
		}

		List<String>  expectedINN = (List) getValueForFieldInCurrentSolrImplementation(entryName, Fields.INTERNATIONAL_NAME);
		if(expectedINN != null)  {
			Assert.assertEquals(expectedCD, nfb.getFieldValue(Fields.INTERNATIONAL_NAME, List.class));
		}

		List<String>  expectedRNList = (List) getValueForFieldInCurrentSolrImplementation(entryName, Fields.REGION_NAME);
		Set<String> expectedRNSet = null;
		Set<String> RNSet = null;
		if(expectedRNList != null)  {
			expectedRNSet = new TreeSet<String>(expectedRNList);
			RNSet = new TreeSet<String>(nfb.getFieldValue(Fields.REGION_NAME, List.class));
			Assert.assertEquals(expectedRNSet, RNSet);
		}

		List<String> orflist = (List) getValueForFieldInCurrentSolrImplementation(entryName, Fields.ORF_NAMES);
		Set<String> expectedorfnames = null;
		Set<String> orfnameSet = null;
		if(orflist != null)  {
			expectedorfnames = new TreeSet<String>(orflist);
			//System.err.println(expectedorfnames);
			if(nfb.getFieldValue(Fields.ORF_NAMES, List.class) != null)
			  orfnameSet = new TreeSet<String>(nfb.getFieldValue(Fields.ORF_NAMES, List.class));
			//System.err.println(expectedorfnames);
			//System.err.println(orfnameSet);
			Assert.assertEquals(expectedorfnames, orfnameSet);
		}

		String expectedfamilies = (String) getValueForFieldInCurrentSolrImplementation(entryName, Fields.FAMILY_NAMES);
		if(expectedfamilies != null)  {
			//System.err.println(expectedfamilies);
			Set<String> expectedFamilySet = new TreeSet<String>(Arrays.asList(expectedfamilies.split(" , ")));
			Set<String> FamilySet = new TreeSet<String>(Arrays.asList(nfb.getFieldValue(Fields.FAMILY_NAMES, String.class).split(" , ")));
			Assert.assertEquals(expectedFamilySet, FamilySet);
		}
	}

}
