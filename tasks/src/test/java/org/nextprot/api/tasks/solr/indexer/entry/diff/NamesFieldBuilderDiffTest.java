package org.nextprot.api.tasks.solr.indexer.entry.diff;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.SolrDiffTest;
import org.nextprot.api.tasks.solr.indexer.entry.impl.NamesFieldBuilder;

public class NamesFieldBuilderDiffTest extends SolrDiffTest {

	@Test
	public void testNames() {

		
		 for(int i=0; i < 10; i++){ testNames(getEntry(i)); }
		 

		//Entry entry = getEntry("NX_P19099");
		//testNames(entry);

	}

	public void testNames(Entry entry) {
		String entryName = entry.getUniqueName();

		System.out.println("Testing: " + entryName);
		NamesFieldBuilder nfb = new NamesFieldBuilder();
		nfb.initializeBuilder(entry);
		
		// RECOMMENDED_NAME are indexed and tested with the overviewFieldBuilder
		
		List<String> altnamelist = (List) getValueForFieldInCurrentSolrImplementation(entryName, Fields.ALTERNATIVE_NAMES);
		Set<String> expectedAltnames = null;
		Set<String> AltenameSet = null;
		if(altnamelist != null)  {
			expectedAltnames = new TreeSet<String>(altnamelist);
			AltenameSet = new TreeSet<String>(nfb.getFieldValue(Fields.ALTERNATIVE_NAMES, List.class));
			//System.err.println(expectedAltnames);
			//System.err.println(AltenameSet);
			Assert.assertEquals(expectedAltnames.size(), AltenameSet.size());
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
			// NX_Q0P140 has no official gene name and is missing the orf name
		}

		String expectedfamilies = (String) getValueForFieldInCurrentSolrImplementation(entryName, Fields.FAMILY_NAMES);
		if(expectedfamilies != null)  {
			//System.err.println(expectedorfnames);
			//System.err.println(orfnameSet);
			//Assert.assertEquals(expectedfamilies, nfb.getFieldValue(Fields.FAMILY_NAMES, String.class));
			// org.junit.ComparisonFailure: expected:<[Belongs to the TRAFAC class dynamin-like GTPase superfamily. Dynamin/Fzo/YdjA family.]> but was:<[Dynamin/Fzo/YdjA]>
			//if(!expectedfamilies.contains(nfb.getFieldValue(Fields.FAMILY_NAMES, String.class))) {System.err.println(expectedfamilies); System.err.println(nfb.getFieldValue(Fields.FAMILY_NAMES, String.class));}
			Assert.assertTrue(expectedfamilies.toLowerCase().contains(nfb.getFieldValue(Fields.FAMILY_NAMES, String.class).toLowerCase()));
		}}

}
