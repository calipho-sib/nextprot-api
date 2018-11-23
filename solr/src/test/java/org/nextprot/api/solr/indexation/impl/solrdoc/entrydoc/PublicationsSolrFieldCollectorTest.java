package org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.nextprot.api.commons.dbunit.AbstractUnitBaseTest;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.domain.ProteinExistence;
import org.nextprot.api.core.domain.publication.GlobalPublicationStatistics;
import org.nextprot.api.core.service.OverviewService;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.integrationtest.diff.OverviewFieldBuilderDiffTest.mockOverview;

@Ignore
@ActiveProfiles({"dev"})
@ContextConfiguration("classpath:spring/solr-context.xml")
public class PublicationsSolrFieldCollectorTest extends AbstractUnitBaseTest {

    // Class under test
    private PublicationsSolrFieldCollector publicationsSolrFieldCollector;

    @Mock
    private PublicationService publicationService;

	@Mock
	private OverviewService overviewService;

    @Before
    public void init() {

        MockitoAnnotations.initMocks(this);

	    Overview overview = mockOverview(ProteinExistence.PROTEIN_LEVEL);
	    Mockito.when(overviewService.findOverviewByEntry(anyString())).thenReturn(overview);

	    publicationsSolrFieldCollector = new PublicationsSolrFieldCollector(publicationService, overviewService);

        GlobalPublicationStatistics.PublicationStatistics publicationStatistics =
                new GlobalPublicationStatistics.PublicationStatistics();

        Mockito.when(publicationService.getPublicationStatistics(anyLong())).thenReturn(publicationStatistics);
    }

    @Test
    public void testPublicationsSolrFieldCollector() {

        Map<EntrySolrField, Object> fields = new HashMap<>();
        publicationsSolrFieldCollector.collect(fields, "NX_Q9P2G1", true);

	    Set<EntrySolrField> fieldKeys = fields.keySet();

	    Assert.assertEquals(5, fieldKeys.size());
	    Assert.assertTrue(publicationsSolrFieldCollector.getCollectedFields().containsAll(fieldKeys));
	    Assert.assertTrue(fields.get(EntrySolrField.PUBLICATIONS) instanceof List);
	    Assert.assertTrue(!((List)fields.get(EntrySolrField.PUBLICATIONS)).isEmpty());
    }
}
