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
import org.nextprot.commons.constants.QualityQualifier;
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

		AnnotationEvidence ev1 = mockAnnotationEvidence("ECO:0000318", true);
		AnnotationEvidence ev2 = mockAnnotationEvidence("ECO:0000320", true);
		AnnotationEvidence ev3 = mockAnnotationEvidence("ECO:0000501", false);
		AnnotationEvidence ev4 = mockAnnotationEvidence("ECO:0000501", false);

		Annotation a1 = mockAnnotation("ribonuclease activity", AnnotationCategory.GO_MOLECULAR_FUNCTION, QualityQualifier.GOLD, ev1, ev2);
		Annotation a2 = mockAnnotation("nucleic acid binding", AnnotationCategory.GO_MOLECULAR_FUNCTION, QualityQualifier.SILVER, ev3);
		Annotation a3 = mockAnnotation("RNA phosphodiester bond hydrolysis", AnnotationCategory.GO_BIOLOGICAL_PROCESS, QualityQualifier.GOLD, ev4);

		Mockito.when(annotationService.findAnnotations(anyString())).thenReturn(Arrays.asList(a1, a2, a3));
	}

	@Ignore
	@Test
	public void testGetFunctionInfoWithCanonicalFirst() {

		List<String> FunctionInfoWithCanonicalFirst;

		FunctionInfoWithCanonicalFirst = annotationSolrFieldCollector.getFunctionInfoWithCanonicalFirst("NX_P19367",
				annotationService.findAnnotations("NX_P19367"));
		Assert.assertTrue(FunctionInfoWithCanonicalFirst.contains("cellular glucose homeostasis"));
	}

	//http://kant:8983/solr/npentries1gold/select?q=id%3ANX_Q8TAA1&fl=id+filters+function_desc&wt=json&indent=true
	// 1. check with monique if I have to change the description in https://api.nextprot.org/entry/NX_Q8TAA1/go-molecular-function.xml
	// 2. check how it is done in the generic-annotation-section.html element line 214 (https://www.nextprot.org/entry/NX_Q8TAA1/)
	// 3. change value 'function_desc' field in SolrInputDocument in SolrEntryDocumentFactory
	@Test
	public void testNX_Q8TAA1GOFunctionDesc() {

		Map<EntrySolrField, Object> fields = new HashMap<>();

		annotationSolrFieldCollector.collect(fields, "NX_Q8TAA1", true);

		@SuppressWarnings("unchecked")
		List<String> list = (List<String>) fields.get(EntrySolrField.FUNCTION_DESC);
		Assert.assertEquals(3, list.size());

		Assert.assertEquals("RNA phosphodiester bond hydrolysis", list.get(0));
		Assert.assertEquals("Not ribonuclease activity", list.get(1));
		Assert.assertEquals("nucleic acid binding", list.get(2));
	}

	private static Annotation mockAnnotation(String termName, AnnotationCategory cat, QualityQualifier quality, AnnotationEvidence... evidences) {

		Annotation mock = Mockito.mock(Annotation.class);

		when(mock.getAPICategory()).thenReturn(cat);
		when(mock.getCategory()).thenReturn(cat.getDbAnnotationTypeName());
		when(mock.getCvTermName()).thenReturn(termName);
		when(mock.getEvidences()).thenReturn(Arrays.asList(evidences));
		when(mock.getQualityQualifier()).thenReturn(quality.name());

		return mock;
	}

	private static AnnotationEvidence mockAnnotationEvidence(String codeAC, boolean isNegative) {

		AnnotationEvidence evidence = new AnnotationEvidence();

		evidence.setEvidenceCodeAC(codeAC);
		evidence.setNegativeEvidence(isNegative);

		return evidence;
	}
}
