package org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.nextprot.api.commons.dbunit.AbstractUnitBaseTest;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.publication.GlobalPublicationStatistics;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Matchers.anyLong;

@ActiveProfiles({"dev"})
@ContextConfiguration("classpath:spring/solr-context.xml")
public class PublicationsSolrFieldCollectorTest extends AbstractUnitBaseTest {

    @Autowired
    private EntryBuilderService entryBuilderService;

    // Class under test
    private PublicationsSolrFieldCollector publicationsSolrFieldCollector;

    @Mock
    private PublicationService publicationService;

    @Before
    public void init() {

        MockitoAnnotations.initMocks(this);
        publicationsSolrFieldCollector = new PublicationsSolrFieldCollector(publicationService);

        GlobalPublicationStatistics.PublicationStatistics publicationStatistics =
                new GlobalPublicationStatistics.PublicationStatistics();

        Mockito.when(publicationService.getPublicationStatistics(anyLong())).thenReturn(publicationStatistics);
    }

    @Test
    public void testPublicationsSolrFieldCollector() {

    	Entry entry = entryBuilderService.build(EntryConfig.newConfig("NX_Q9P2G1").withPublications().withOverview());

        Map<EntrySolrField, Object> fields = new HashMap<>();
        publicationsSolrFieldCollector.collect(fields, entry, true);

	    Set<EntrySolrField> fieldKeys = fields.keySet();

	    Assert.assertEquals(5, fieldKeys.size());
	    Assert.assertTrue(publicationsSolrFieldCollector.getCollectedFields().containsAll(fieldKeys));
	    Assert.assertTrue(fields.get(EntrySolrField.PUBLICATIONS) instanceof List);
	    Assert.assertTrue(!((List)fields.get(EntrySolrField.PUBLICATIONS)).isEmpty());
    }
}
