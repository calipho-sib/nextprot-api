package org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.dbunit.AbstractUnitBaseTest;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertTrue;

@ActiveProfiles({"dev", "cache"})
@ContextConfiguration("classpath:spring/solr-context.xml")
public class ChromosomeSolrFieldCollectorTest extends AbstractUnitBaseTest {

	// Class under test
	@Autowired
	private ChromosomeSolrFieldCollector collector;

	@Test
	public void testSorting() {
		Integer computedValue = ChromosomeSolrFieldCollector.sortChr("18q21.33");
		assertTrue(computedValue.equals(18071330));
	}

	@Test
	public void testNX_Q9P2G1() {

		Map<EntrySolrField, Object> fields = new HashMap<>();

		collector.collect(fields, "NX_Q9P2G1", true);

		Set<EntrySolrField> fieldKeys = fields.keySet();

		Assert.assertEquals(3, fieldKeys.size());
		Assert.assertTrue(collector.getCollectedFields().containsAll(fieldKeys));

		Assert.assertTrue(fields.get(EntrySolrField.GENE_BAND) instanceof List);
		List bands = (List)fields.get(EntrySolrField.GENE_BAND);
		Assert.assertEquals(1, bands.size());
		// TODO: do we really want only one element that is a string with space delimitor?
		Assert.assertEquals("q21.2 7q21.2", bands.get(0));
		Assert.assertEquals("7q21.2", fields.get(EntrySolrField.CHR_LOC));
		Assert.assertEquals(7071200, fields.get(EntrySolrField.CHR_LOC_S));
	}
}
