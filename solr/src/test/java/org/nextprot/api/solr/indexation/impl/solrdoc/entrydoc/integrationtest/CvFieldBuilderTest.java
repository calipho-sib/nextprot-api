package org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.integrationtest;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.CVSolrFieldCollector;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.integrationtest.diff.SolrDiffTest.getFieldValue;

public class CvFieldBuilderTest extends SolrBuildIntegrationTest {

	@Autowired
	private CVSolrFieldCollector cvSolrFieldCollector;

	@Autowired
	private TerminologyService terminologyService;

	@Test
	public void shouldContainCvTermsFromExperimentalContext() {

		String entryName = "NX_Q9H207";
		CVSolrFieldCollector cvfb = new CVSolrFieldCollector();
		Map<EntrySolrField, Object> fields = new HashMap<>();
		cvfb.collect(fields, entryName, false);
		List<String> cvAvs = getFieldValue(fields, EntrySolrField.CV_ACS, List.class);
        List<String> cvNames = getFieldValue(fields, EntrySolrField.CV_NAMES, List.class);

		assertTrue(cvAvs.contains("ECO:0000219"));
		//The text should not be indexed
		assertFalse(cvNames.contains("nucleotide sequencing assay evidence"));
    }

	@Test
	public void shouldContainCvTermsFromPropertyNamesSuchAsTopologyAndOrientation() {

		String entryName = "NX_Q9BV57";

		CVSolrFieldCollector cvfb = new CVSolrFieldCollector();

		Map<EntrySolrField, Object> fields = new HashMap<>();
		cvfb.collect(fields, entryName, false);
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

		CVSolrFieldCollector cvfb = new CVSolrFieldCollector();
		Map<EntrySolrField, Object> fields = new HashMap<>();
		cvfb.collect(fields, entryName, false);

		String enzymes = getFieldValue(fields, EntrySolrField.EC_NAME, String.class);
		assertTrue(enzymes.contains("EC 3.4.15.1"));

		List<String> names = getFieldValue(fields, EntrySolrField.CV_NAMES, List.class);
		assertTrue(names.contains("Peptidase M2 family")); //family names
	}

	@Test
	public void shouldWork() {

		String entryName = "NX_P78536";

		CVSolrFieldCollector cvfb = new CVSolrFieldCollector();
		Map<EntrySolrField, Object> fields = new HashMap<>();
		cvfb.collect(fields, entryName, false);
	}

	@Test
	public void shouldContainCorrectCvTermACsFromFamilyFA03241() {

		String entryName = "NX_O95376";

		Map<EntrySolrField, Object> collector = new HashMap<>();

		cvSolrFieldCollector.collect(collector, entryName, true);

		Assert.assertTrue(collector.get(EntrySolrField.CV_ACS) instanceof List);
		List<String> acs = (List<String>) collector.get(EntrySolrField.CV_ACS);
		Assert.assertTrue(acs.contains("FA-03242"));

		Assert.assertTrue(collector.get(EntrySolrField.CV_ANCESTORS_ACS) instanceof List);
		List<String> ancestorsAcs = (List<String>) collector.get(EntrySolrField.CV_ANCESTORS_ACS);
		Assert.assertTrue(ancestorsAcs.contains("FA-03241"));
	}
}
