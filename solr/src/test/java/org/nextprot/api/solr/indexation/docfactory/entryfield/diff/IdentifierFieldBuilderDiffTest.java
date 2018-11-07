package org.nextprot.api.solr.indexation.docfactory.entryfield.diff;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.core.schema.EntrySolrField;
import org.nextprot.api.solr.indexation.docfactory.entryfield.IdentifierSolrFieldCollector;
import org.nextprot.api.solr.indexation.docfactory.entryfield.SolrDiffTest;

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
		IdentifierSolrFieldCollector ifb = new IdentifierSolrFieldCollector();
		ifb.collect(entry, false);
		
		List<String> expectedACs = (List) getValueForFieldInCurrentSolrImplementation(entryName, EntrySolrField.ALTERNATIVE_ACS);
		if(expectedACs != null) {
		  Collections.sort(expectedACs);
		  List<String> secundaryACs = ifb.getFieldValue(EntrySolrField.ALTERNATIVE_ACS, List.class);
		  Collections.sort(secundaryACs);
		  Assert.assertEquals(secundaryACs, expectedACs);
		}
		
		List<String> expectedCloneNames = (List) getValueForFieldInCurrentSolrImplementation(entryName, EntrySolrField.CLONE_NAME);
		if(expectedCloneNames != null)
		  Assert.assertEquals(ifb.getFieldValue(EntrySolrField.CLONE_NAME, List.class).size(), expectedCloneNames.size());
		
		List<String> expectedMicroarrays = (List) getValueForFieldInCurrentSolrImplementation(entryName, EntrySolrField.MICROARRAY_PROBE);
		if(expectedMicroarrays != null)
		  Assert.assertEquals(ifb.getFieldValue(EntrySolrField.MICROARRAY_PROBE, List.class).size(), expectedMicroarrays.size());
		
		// There is only one UNIPROT_NAME per entry, it shouldn't be a list/multifield
		List<String> expectedUniProtName = (List) getValueForFieldInCurrentSolrImplementation(entryName, EntrySolrField.UNIPROT_NAME);
		Assert.assertEquals(ifb.getFieldValue(EntrySolrField.UNIPROT_NAME, List.class), expectedUniProtName);
	}
	
}
