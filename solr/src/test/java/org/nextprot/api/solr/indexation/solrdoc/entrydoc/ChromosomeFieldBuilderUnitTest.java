package org.nextprot.api.solr.indexation.solrdoc.entrydoc;

import org.junit.Test;
import org.nextprot.api.commons.dbunit.AbstractUnitBaseTest;

import static org.junit.Assert.assertTrue;

public class ChromosomeFieldBuilderUnitTest extends AbstractUnitBaseTest {

	@Test
	public void testSorting() {
		Integer computedValue = ChromosomeSolrFieldCollector.sortChr("18q21.33");
		assertTrue(computedValue.equals(18071330));
	}
}
