package org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.integration;

import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.CVSolrFieldCollector;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.SolrBuildIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.diff.SolrDiffTest.getFieldValue;


public class CvFieldBuilderTest extends SolrBuildIntegrationTest {


	@Autowired	private EntryBuilderService entryBuilderService ;
	@Autowired	private TerminologyService terminologyService;

	@Test
	public void shouldContainCvTermsFromExperimentalContext() {

		String entryName = "NX_Q9H207";
		Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryName).withOverview().withEnzymes().with("variant"));
		CVSolrFieldCollector cvfb = new CVSolrFieldCollector();
		Map<EntrySolrField, Object> fields = new HashMap<>();
		cvfb.collect(fields, entry, false);
		List<String> cvAvs = getFieldValue(fields, EntrySolrField.CV_ACS, List.class);
        List<String> cvNames = getFieldValue(fields, EntrySolrField.CV_NAMES, List.class);

		assertTrue(cvAvs.contains("ECO:0000219"));
		//The text should not be indexed
		assertFalse(cvNames.contains("nucleotide sequencing assay evidence"));

    }


	@Test
	public void shouldContainCvTermsFromPropertyNamesSuchAsTopologyAndOrientation() {

		String entryName = "NX_Q9BV57";
		Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryName).withOverview().withEnzymes().with("subcellular-location"));

		CVSolrFieldCollector cvfb = new CVSolrFieldCollector();

		Map<EntrySolrField, Object> fields = new HashMap<>();
		cvfb.collect(fields, entry, false);
		List<String> cvAvs = getFieldValue(fields, EntrySolrField.CV_ACS, List.class);
		List<String> cvNames = getFieldValue(fields, EntrySolrField.CV_NAMES, List.class);

		assertTrue(cvAvs.contains("SL-9910"));
		//The text should not be indexed
		assertFalse(cvNames.contains("Cytoplasmic side"));
		assertTrue(cvAvs.contains("SL-9903"));
		//The text should not be indexed
		assertFalse(cvNames.contains("Peripheral membrane protein"));
	}


	@Test
	public void shouldContainEnzymeAndFamilyNames() {

		String entryName = "NX_P12821";
		Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryName).withOverview().withEnzymes().with("subcellular-location"));

		CVSolrFieldCollector cvfb = new CVSolrFieldCollector();
		Map<EntrySolrField, Object> fields = new HashMap<>();
		cvfb.collect(fields, entry, false);

		String enzymes = getFieldValue(fields, EntrySolrField.EC_NAME, String.class);
		assertTrue(enzymes.contains("EC 3.4.15.1"));

		List<String> names = getFieldValue(fields, EntrySolrField.CV_NAMES, List.class);
		assertTrue(names.contains("Peptidase M2 family")); //family names

	}



	@Test
	public void shouldWork() {

		String entryName = "NX_P78536";
		Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryName).withOverview().withEnzymes().withAnnotations());

		CVSolrFieldCollector cvfb = new CVSolrFieldCollector();
		Map<EntrySolrField, Object> fields = new HashMap<>();
		cvfb.collect(fields, entry, false);

	}
}
