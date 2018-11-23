package org.nextprot.api.solr.indexation.impl.solrdoc;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.dbunit.AbstractUnitBaseTest;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.EntrySolrFieldCollector;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collection;

@ActiveProfiles({"build"})
@ContextConfiguration("classpath:spring/commons-context.xml")
public class CollectorsTest extends AbstractUnitBaseTest {

	@Test
	public void shouldCoverAllSolrFields() {

		Collection<EntrySolrFieldCollector> list = SolrEntryDocumentFactory.getCollectors();

		Assert.assertTrue(!list.isEmpty());
		Assert.assertEquals(12, list.size());
	}
}
