package org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.integrationtest.diff;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.IdentifierSolrFieldCollector;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Ignore
public class IdentifierFieldBuilderDiffTest extends SolrDiffTest {

	@Test
	public void testIdentifiers() {

		for(int i=0; i < 10; i++){
			testIdentfiers(entries.get(i));
		}
		
		//Entry entry = getEntry("NX_Q96I99");
		//testIdentfiers(entry);
	
	}

	
	public void testIdentfiers(String entryName) {

		IdentifierSolrFieldCollector ifb = new IdentifierSolrFieldCollector();
		Map<EntrySolrField, Object> fields = new HashMap<>();
		ifb.collect(fields, entryName, false);
		
		List<String> expectedACs = (List) getValueForFieldInCurrentSolrImplementation(entryName, EntrySolrField.ALTERNATIVE_ACS);
		if(expectedACs != null) {
		  Collections.sort(expectedACs);
		  List<String> secundaryACs = getFieldValue(fields, EntrySolrField.ALTERNATIVE_ACS, List.class);
		  Collections.sort(secundaryACs);
		  Assert.assertEquals(secundaryACs, expectedACs);
		}
		
		List<String> expectedCloneNames = (List) getValueForFieldInCurrentSolrImplementation(entryName, EntrySolrField.CLONE_NAME);
		if(expectedCloneNames != null)
		  Assert.assertEquals(getFieldValue(fields, EntrySolrField.CLONE_NAME, List.class).size(), expectedCloneNames.size());
		
		List<String> expectedMicroarrays = (List) getValueForFieldInCurrentSolrImplementation(entryName, EntrySolrField.MICROARRAY_PROBE);
		if(expectedMicroarrays != null)
		  Assert.assertEquals(getFieldValue(fields, EntrySolrField.MICROARRAY_PROBE, List.class).size(), expectedMicroarrays.size());
		
		// There is only one UNIPROT_NAME per entry, it shouldn't be a list/multifield
		List<String> expectedUniProtName = (List) getValueForFieldInCurrentSolrImplementation(entryName, EntrySolrField.UNIPROT_NAME);
		Assert.assertEquals(getFieldValue(fields, EntrySolrField.UNIPROT_NAME, List.class), expectedUniProtName);
	}
	
}
