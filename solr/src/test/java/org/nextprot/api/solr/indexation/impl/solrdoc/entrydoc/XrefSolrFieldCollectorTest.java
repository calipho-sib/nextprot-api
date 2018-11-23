package org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.dbunit.AbstractUnitBaseTest;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Ignore
@ActiveProfiles({"dev"})
@ContextConfiguration("classpath:spring/commons-context.xml")
public class XrefSolrFieldCollectorTest extends AbstractUnitBaseTest {

    // Class under test
    @Autowired
    private XrefSolrFieldCollector collector;

    @Test
    public void testNX_Q9P2G1() {

        Map<EntrySolrField, Object> fields = new HashMap<>();
	    collector.collect(fields, "NX_Q9P2G1", true);

	    Set<EntrySolrField> fieldKeys = fields.keySet();

	    Assert.assertEquals(3, fieldKeys.size());
	    Assert.assertTrue(collector.getCollectedFields().containsAll(fieldKeys));
    }
}
