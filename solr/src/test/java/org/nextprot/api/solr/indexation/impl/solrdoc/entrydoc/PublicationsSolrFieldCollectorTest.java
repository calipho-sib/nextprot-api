package org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.nextprot.api.core.domain.publication.GlobalPublicationStatistics;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.anyLong;

@ActiveProfiles({"build"})
@ContextConfiguration("classpath:spring/solr-context.xml")
public class PublicationsSolrFieldCollectorTest extends SolrDiffTest {

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

        Map<EntrySolrField, Object> fields = new HashMap<>();
        publicationsSolrFieldCollector.collect(fields, entryBuilderService.buildWithEverything("NX_Q9P2G1"), true);

        System.out.println(fields);
    }
}
