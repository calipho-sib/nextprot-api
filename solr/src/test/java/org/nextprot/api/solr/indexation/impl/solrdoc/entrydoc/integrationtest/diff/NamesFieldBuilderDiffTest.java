package org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.integrationtest.diff;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.NamesSolrFieldCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Ignore
@ActiveProfiles({"dev"})
@ContextConfiguration("classpath:spring/commons-context.xml")
public class NamesFieldBuilderDiffTest extends SolrDiffTest {

	@Autowired
	NamesSolrFieldCollector namesSolrFieldCollector;

	@Test
	public void testNames() {
		String[] test_list = {"NX_Q8IWA4", "NX_O00115","NX_Q7Z6P3","NX_E5RQL4","NX_Q12809","NX_Q7Z6P3",
				"NX_Q7Z713", "NX_P22102", "NX_Q8IYV9", "NX_O00116", "NX_Q7Z713", "NX_O15056"};

		for(int i=0; i < test_list.length; i++) {
			testNames(test_list[i]);
		}
		//for(int i=0; i < 80; i++){ testNames(getEntry(i)); } // 'random' entries
		//Entry entry = getEntry("NX_P62805");
		//testNames(entry);
	}

	public void testNames(String entryName) {

		Map<EntrySolrField, Object> fields = new HashMap<>();
		namesSolrFieldCollector.collect(fields, entryName, false);
		
		// RECOMMENDED_NAME are indexed and tested with the overviewFieldBuilder
		String expectedGenenames = (String) getValueForFieldInCurrentSolrImplementation(entryName, EntrySolrField.RECOMMENDED_GENE_NAMES);
		String nowGenenames = getFieldValue(fields, EntrySolrField.RECOMMENDED_GENE_NAMES, String.class);
		
		List<String> altnamelist = (List) getValueForFieldInCurrentSolrImplementation(entryName, EntrySolrField.ALTERNATIVE_NAMES);
		Set<String> expectedAltnames = null;
		Set<String> AltenameSet = null;
		if(altnamelist != null)  {
			expectedAltnames = new TreeSet<>(altnamelist);
			AltenameSet = new TreeSet<String>(getFieldValue(fields, EntrySolrField.ALTERNATIVE_NAMES, List.class));
			//System.err.println("expected: " + expectedAltnames);
			//System.err.println(AltenameSet);
			if(AltenameSet.size() > expectedAltnames.size()) {
				AltenameSet.removeAll(expectedAltnames);
				System.err.println("WARNING: " + AltenameSet + " should also be indexed as an ALTERNATIVE_NAMES token");
			}
			else Assert.assertEquals(expectedAltnames.size(), AltenameSet.size());
		}

		List<String> altgenlist = (List) getValueForFieldInCurrentSolrImplementation(entryName, EntrySolrField.ALTERNATIVE_GENE_NAMES);
		Set<String> expectedAltGenename = null;
		Set<String> AltGenenameSet = null;
		if(altgenlist != null)  {
			expectedAltGenename = new TreeSet<>(altgenlist);
			AltGenenameSet = new TreeSet<String>(getFieldValue(fields, EntrySolrField.ALTERNATIVE_GENE_NAMES, List.class));
			//System.err.println(expectedAltGenename);
			//System.err.println(AltGenenameSet);
			Assert.assertEquals(expectedAltGenename.size(), AltGenenameSet.size());
		}

		List<String>  expectedCD = (List) getValueForFieldInCurrentSolrImplementation(entryName, EntrySolrField.CD_ANTIGEN);
		if(expectedCD != null)  {
			Assert.assertEquals(expectedCD, getFieldValue(fields, EntrySolrField.CD_ANTIGEN, List.class));
		}

		List<String>  expectedINN = (List) getValueForFieldInCurrentSolrImplementation(entryName, EntrySolrField.INTERNATIONAL_NAME);
		if(expectedINN != null)  {
			Assert.assertEquals(expectedCD, getFieldValue(fields, EntrySolrField.INTERNATIONAL_NAME, List.class));
		}

		List<String>  expectedRNList = (List) getValueForFieldInCurrentSolrImplementation(entryName, EntrySolrField.REGION_NAME);
		Set<String> expectedRNSet = null;
		Set<String> RNSet = null;
		if(expectedRNList != null)  {
			expectedRNSet = new TreeSet<>(expectedRNList);
			RNSet = new TreeSet<String>(getFieldValue(fields, EntrySolrField.REGION_NAME, List.class));
			Assert.assertEquals(expectedRNSet, RNSet);
		}

		List<String> orflist = (List) getValueForFieldInCurrentSolrImplementation(entryName, EntrySolrField.ORF_NAMES);
		Set<String> expectedorfnames = null;
		Set<String> orfnameSet = null;
		if(orflist != null)  {
			expectedorfnames = new TreeSet<>(orflist);
			//System.err.println(expectedorfnames);
			if(getFieldValue(fields, EntrySolrField.ORF_NAMES, List.class) != null)
			  orfnameSet = new TreeSet<String>(getFieldValue(fields, EntrySolrField.ORF_NAMES, List.class));
			//System.err.println(expectedorfnames);
			//System.err.println(orfnameSet);
			Assert.assertEquals(expectedorfnames, orfnameSet);
		}

		String expectedfamilies = (String) getValueForFieldInCurrentSolrImplementation(entryName, EntrySolrField.FAMILY_NAMES);
		if(expectedfamilies != null)  {
			//System.err.println(expectedfamilies);
			Set<String> expectedFamilySet = new TreeSet<String>(Arrays.asList(expectedfamilies.split(" , ")));
			Set<String> FamilySet = new TreeSet<String>(Arrays.asList(getFieldValue(fields, EntrySolrField.FAMILY_NAMES, String.class).split(" , ")));
			Assert.assertEquals(expectedFamilySet, FamilySet);
		}
	}

}
