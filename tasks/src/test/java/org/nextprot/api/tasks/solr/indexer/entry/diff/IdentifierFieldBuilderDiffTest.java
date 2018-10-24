package org.nextprot.api.tasks.solr.indexer.entry.diff;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.index.EntryField;
import org.nextprot.api.tasks.solr.indexer.entry.SolrDiffTest;
import org.nextprot.api.tasks.solr.indexer.entry.impl.IdentifierFieldBuilder;

import java.util.Collections;
import java.util.List;

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

		System.out.println("Testing: " + entryName);
		IdentifierFieldBuilder ifb = new IdentifierFieldBuilder();
		ifb.initializeBuilder(entry);
		
		List<String> expectedACs = (List) getValueForFieldInCurrentSolrImplementation(entryName, EntryField.ALTERNATIVE_ACS);
		if(expectedACs != null) {
		  Collections.sort(expectedACs);
		  List<String> secundaryACs = ifb.getFieldValue(EntryField.ALTERNATIVE_ACS, List.class);
		  Collections.sort(secundaryACs);
		  Assert.assertEquals(secundaryACs, expectedACs);
		}
		
		List<String> expectedCloneNames = (List) getValueForFieldInCurrentSolrImplementation(entryName, EntryField.CLONE_NAME);
		if(expectedCloneNames != null)
		  Assert.assertEquals(ifb.getFieldValue(EntryField.CLONE_NAME, List.class).size(), expectedCloneNames.size());
		
		List<String> expectedMicroarrays = (List) getValueForFieldInCurrentSolrImplementation(entryName, EntryField.MICROARRAY_PROBE);
		if(expectedMicroarrays != null)
		  Assert.assertEquals(ifb.getFieldValue(EntryField.MICROARRAY_PROBE, List.class).size(), expectedMicroarrays.size());
		
		// There is only one UNIPROT_NAME per entry, it shouldn't be a list/multifield
		List<String> expectedUniProtName = (List) getValueForFieldInCurrentSolrImplementation(entryName, EntryField.UNIPROT_NAME);
		Assert.assertEquals(ifb.getFieldValue(EntryField.UNIPROT_NAME, List.class), expectedUniProtName);
	}
	
}
