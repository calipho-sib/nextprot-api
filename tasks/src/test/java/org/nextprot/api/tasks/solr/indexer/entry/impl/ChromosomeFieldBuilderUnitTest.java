package org.nextprot.api.tasks.solr.indexer.entry.impl;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;

public class ChromosomeFieldBuilderUnitTest extends CoreUnitBaseTest {

	@Test
	public void testSorting() {
		Integer computedValue = ChromosomeFieldBuilder.sortChr("18q21.33");
		assertTrue(computedValue.equals(18071330));
	}
}
