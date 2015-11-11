package org.nextprot.api.tasks.solr.indexer.entry.impl;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.nextprot.api.commons.dbunit.AbstractUnitBaseTest;

public class ChromosomeFieldBuilderUnitTest extends AbstractUnitBaseTest {

	@Test
	public void testSorting() {
		Integer computedValue = ChromosomeFieldBuilder.sortChr("18q21.33");
		assertTrue(computedValue.equals(18071330));
	}
}
