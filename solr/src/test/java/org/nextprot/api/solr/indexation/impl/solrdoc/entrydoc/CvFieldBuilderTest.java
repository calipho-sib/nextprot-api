package org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.integrationtest.SolrBuildIntegrationTest;
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

	@Test
	public void shouldContainCvTermsFromExperimentalContext() {

		Map<EntrySolrField, Object> collector = new HashMap<>();
		cvSolrFieldCollector.collect(collector, "NX_P27658", true);

		Assert.assertTrue(collector.get(EntrySolrField.CV_ACS) instanceof List);
		//noinspection unchecked
		List<String> cvAcs = (List<String>) collector.get(EntrySolrField.CV_ACS);
		// evidence - evidenceCodeAC
		Assert.assertTrue(cvAcs.contains("ECO:0000219"));
		// evidence - experimentalContext - detectionMethod
		Assert.assertFalse(cvAcs.contains("ECO:0000006"));
		// evidence - experimentalContext - Disease
		Assert.assertTrue(cvAcs.contains("C3749"));
		// evidence - experimentalContext - Tissue
		Assert.assertTrue(cvAcs.contains("TS-0558"));

		// evidence - experimentalContext - DevelopmentalStage : see shouldContainCvTermsFromExperimentalContext_devStage
		// evidence - experimentalContext - CellLine: no example found
		// evidence - experimentalContext - Organelle: no example found

		// TODO: see with pam: See comment in CVSolrFieldCollector line 128 of why cvname has not been added
		Assert.assertTrue(collector.get(EntrySolrField.CV_NAMES) instanceof List);
		//noinspection unchecked
		List<String> cvNames = (List<String>) collector.get(EntrySolrField.CV_NAMES);
		Assert.assertTrue(!cvNames.contains("nucleotide sequencing assay evidence"));
    }
	@Test
	public void shouldContainCvTermsFromExperimentalContext_devStage() {

		Map<EntrySolrField, Object> collector = new HashMap<>();
		cvSolrFieldCollector.collect(collector, "NX_Q6NUJ2", true);

		Assert.assertTrue(collector.get(EntrySolrField.CV_ACS) instanceof List);
		//noinspection unchecked
		List<String> cvAcs = (List<String>) collector.get(EntrySolrField.CV_ACS);
		// evidence - experimentalContext - DevelopmentalStage
		Assert.assertTrue(cvAcs.contains("HsapDO:0000030"));
		Assert.assertFalse(cvAcs.contains("HsapDO:0000005")); // negative evidence

		// TODO: see with pam: See comment in CVSolrFieldCollector line 128 of why cvname has not been added
		Assert.assertTrue(collector.get(EntrySolrField.CV_NAMES) instanceof List);
		//noinspection unchecked
		List<String> cvNames = (List<String>) collector.get(EntrySolrField.CV_NAMES);
		Assert.assertTrue(!cvNames.contains("nucleotide sequencing assay evidence"));
	}

	@Test
	public void shouldContainCvTermsFromPropertyNamesSuchAsTopologyAndOrientation() {

		Map<EntrySolrField, Object> fields = new HashMap<>();
		cvSolrFieldCollector.collect(fields, "NX_Q9BV57", false);
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

		Map<EntrySolrField, Object> fields = new HashMap<>();
		cvSolrFieldCollector.collect(fields, "NX_P12821", false);

		String enzymes = getFieldValue(fields, EntrySolrField.EC_NAME, String.class);
		assertTrue(enzymes.contains("EC 3.4.15.1"));

		List<String> names = getFieldValue(fields, EntrySolrField.CV_NAMES, List.class);
		assertTrue(names.contains("Peptidase M2 family")); //family names
	}

	@Test
	public void shouldContainCorrectCvTermACsFromFamilyFA03241() {

		Map<EntrySolrField, Object> collector = new HashMap<>();

		cvSolrFieldCollector.collect(collector, "NX_O95376", true);

		Assert.assertTrue(collector.get(EntrySolrField.CV_ACS) instanceof List);
		List<String> acs = (List<String>) collector.get(EntrySolrField.CV_ACS);
		Assert.assertTrue(acs.contains("FA-03242"));

		Assert.assertTrue(collector.get(EntrySolrField.CV_ANCESTORS_ACS) instanceof List);
		List<String> ancestorsAcs = (List<String>) collector.get(EntrySolrField.CV_ANCESTORS_ACS);
		Assert.assertTrue(ancestorsAcs.contains("FA-03241"));
	}
}
