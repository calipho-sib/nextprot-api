package org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.dbunit.AbstractUnitBaseTest;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.domain.ProteinExistence;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.OverviewService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.integrationtest.diff.OverviewFieldBuilderDiffTest.mockOverview;

@ActiveProfiles({"dev"})
@ContextConfiguration("classpath:spring/commons-context.xml")
public class AnnotationSolrFieldCollectorTest extends AbstractUnitBaseTest {

    // Class under test
    @Autowired
    private AnnotationSolrFieldCollector annotationSolrFieldCollector;

	@Mock
	private AnnotationService annotationService;

	@Autowired
	private TerminologyService terminologyService;

	@Autowired
	private IsoformService isoformService;

	@Mock
	private OverviewService overviewService;

	@Before
	public void init() {

		MockitoAnnotations.initMocks(this);

		Overview overview = mockOverview(ProteinExistence.PROTEIN_LEVEL);
		Mockito.when(overviewService.findOverviewByEntry(anyString())).thenReturn(overview);

		annotationSolrFieldCollector = new AnnotationSolrFieldCollector(annotationService, terminologyService,
				isoformService, overviewService);

		AnnotationEvidence evidence = mockAnnotationEvidence("ECO:0000318", true);
		Annotation annotation = mockAnnotation(AnnotationCategory.GO_MOLECULAR_FUNCTION, "ribonuclease activity", evidence);

		Mockito.when(annotationService.findAnnotations(anyString())).thenReturn(Arrays.asList(annotation));
	}

	@Ignore
	@Test
	public void testGetFunctionInfoWithCanonicalFirst() {

		List<String> FunctionInfoWithCanonicalFirst;

		FunctionInfoWithCanonicalFirst = annotationSolrFieldCollector.getFunctionInfoWithCanonicalFirst("NX_P19367",
				annotationService.findAnnotations("NX_P19367"));
		Assert.assertTrue(FunctionInfoWithCanonicalFirst.contains("cellular glucose homeostasis"));
	}

	@Test
	public void testNX_Q8TAA1GOFunctionDesc() {

		Map<EntrySolrField, Object> fields = new HashMap<>();

		annotationSolrFieldCollector.collect(fields, "NX_Q8TAA1", true);
		System.out.println(fields);
		//noinspection unchecked
		Assert.assertTrue(((List<String>)fields.get(EntrySolrField.FUNCTION_DESC.getName())).contains("Not ribonuclease activity"));
	}

	private static Annotation mockAnnotation(AnnotationCategory cat, String description, AnnotationEvidence evidence) {

		Annotation mock = Mockito.mock(Annotation.class);

		when(mock.getAPICategory()).thenReturn(cat);
		when(mock.getDescription()).thenReturn(description);
		when(mock.getEvidences()).thenReturn(Arrays.asList(evidence));

		return mock;
	}

	private static AnnotationEvidence mockAnnotationEvidence(String codeAC, boolean isNegative) {

		AnnotationEvidence evidence = new AnnotationEvidence();

		evidence.setEvidenceCodeAC(codeAC);
		evidence.setNegativeEvidence(isNegative);

		return evidence;
	}
}
