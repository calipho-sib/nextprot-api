package org.nextprot.api.solr.indexation.impl.service;

import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.domain.ProteinExistence;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.OverviewService;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.solr.indexation.SolrEntryFieldCollectorService;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.AnnotationSolrFieldCollector;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.CVSolrFieldCollector;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.ChromosomeSolrFieldCollector;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.ExpressionSolrFieldCollector;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.FilterAndPropertiesFieldsCollector;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.IdentifierSolrFieldCollector;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.InteractionSolrFieldCollector;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.NamesSolrFieldCollector;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.OverviewSolrFieldCollector;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.PeptideSolrFieldCollector;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.PublicationsSolrFieldCollector;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.XrefSolrFieldCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;

import static org.mockito.Matchers.anyString;
import static org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.integrationtest.diff.OverviewFieldBuilderDiffTest.mockOverview;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({"unit", "unit-schema-nextprot"})
@DirtiesContext
@ContextConfiguration({"classpath:spring/commons-context.xml"})
public class SolrEntryFieldCollectorServiceImplTest {

	// Class under test
	private SolrEntryFieldCollectorService solrEntryFieldCollectorServiceImpl;

	@Mock
	private OverviewService overviewService;
	@Mock
	private PublicationService publicationService;
	@Autowired
	private TerminologyService terminologyService;
	@Mock
	private AnnotationService annotationService;
	@Mock
	private FilterAndPropertiesFieldsCollector filterAndPropertiesFieldsCollector;
	@Autowired
	private IsoformService isoformService;
	@Autowired
	private ChromosomeSolrFieldCollector chromosomeSolrFieldCollector;
	@Mock
	private CVSolrFieldCollector cvSolrFieldCollector;
	@Mock
	private ExpressionSolrFieldCollector expressionSolrFieldCollector;
	@Autowired
	private IdentifierSolrFieldCollector identifierSolrFieldCollector;
	@Mock
	private InteractionSolrFieldCollector interactionSolrFieldCollector;
	@Mock
	private PeptideSolrFieldCollector peptideSolrFieldCollector;
	@Mock
	private XrefSolrFieldCollector xrefSolrFieldCollector;

	@Before
	public void init() {

		MockitoAnnotations.initMocks(this);
		OverviewSolrFieldCollector overviewSolrFieldCollector = new OverviewSolrFieldCollector(overviewService);
		NamesSolrFieldCollector namesSolrFieldCollector = new NamesSolrFieldCollector(overviewService);

		Overview overview = mockOverview(ProteinExistence.PROTEIN_LEVEL);
		Mockito.when(overviewService.findOverviewByEntry(anyString())).thenReturn(overview);

		PublicationsSolrFieldCollector publicationsSolrFieldCollector = new PublicationsSolrFieldCollector(publicationService, overviewService);
		//FilterAndPropertiesFieldsCollector filterAndPropertiesFieldsCollector = new FilterAndPropertiesFieldsCollector(entryReportStatsService, entryPropertiesService);

		AnnotationSolrFieldCollector annotationSolrFieldCollector = new AnnotationSolrFieldCollector(annotationService, terminologyService, isoformService, overviewService);


		solrEntryFieldCollectorServiceImpl = new SolrEntryFieldCollectorServiceImpl(Arrays.asList(
				annotationSolrFieldCollector,
				chromosomeSolrFieldCollector,
				cvSolrFieldCollector,
				expressionSolrFieldCollector,
				filterAndPropertiesFieldsCollector,
				identifierSolrFieldCollector,
				interactionSolrFieldCollector,
				namesSolrFieldCollector,
				overviewSolrFieldCollector,
				peptideSolrFieldCollector,
				publicationsSolrFieldCollector,
				xrefSolrFieldCollector
		));
	}

	// TODO: USELESS TEST
	@Test
	public void buildSolrDoc() {

		// MSH6
		SolrInputDocument doc = solrEntryFieldCollectorServiceImpl.buildSolrDoc("NX_P52701", true);
	}
}