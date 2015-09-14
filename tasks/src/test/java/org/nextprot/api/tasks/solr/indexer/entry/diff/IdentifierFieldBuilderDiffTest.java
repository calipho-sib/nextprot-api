package org.nextprot.api.tasks.solr.indexer.entry.diff;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.SolrDiffTest;
import org.nextprot.api.tasks.solr.indexer.entry.impl.IdentifierFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.impl.OverviewFieldBuilder;

public class IdentifierFieldBuilderDiffTest extends SolrDiffTest {

	@Test
	public void testIdentifiers() {

		for(int i=0; i < 10; i++){
			testIdentfiers(getEntry(i));
		}
		
		//Entry entry = getEntry("NX_Q96I99");
		//testIdentfiers(entry);
	
	}

	
	public void testIdentfiers(Entry entry) {
		
		String entryName = entry.getUniqueName();

		IdentifierFieldBuilder ifb = new IdentifierFieldBuilder();
		ifb.initializeBuilder(entry);
		
		List<String> expectedACs = (List) getValueForFieldInCurrentSolrImplementation(entryName, Fields.ALTERNATIVE_ACS);
		if(expectedACs != null) {
		  Collections.sort(expectedACs);
		  List<String> secundaryACs = ifb.getFieldValue(Fields.ALTERNATIVE_ACS, List.class);
		  Collections.sort(secundaryACs);
		  Assert.assertEquals(secundaryACs, expectedACs);
		}
		
		List<String> expectedCloneNames = (List) getValueForFieldInCurrentSolrImplementation(entryName, Fields.CLONE_NAME);
		if(expectedCloneNames != null)
		  Assert.assertEquals(ifb.getFieldValue(Fields.CLONE_NAME, List.class).size(), expectedCloneNames.size());
		
		List<String> expectedMicroarrays = (List) getValueForFieldInCurrentSolrImplementation(entryName, Fields.MICROARRAY_PROBE);
		if(expectedMicroarrays != null)
		  Assert.assertEquals(ifb.getFieldValue(Fields.MICROARRAY_PROBE, List.class).size(), expectedMicroarrays.size());
		
		// There is only one UNIPROT_NAME per entry, it shouldn't be a list/multifield
		List<String> expectedUniProtName = (List) getValueForFieldInCurrentSolrImplementation(entryName, Fields.UNIPROT_NAME);
		Assert.assertEquals(ifb.getFieldValue(Fields.UNIPROT_NAME, List.class), expectedUniProtName);
	}
	
}
