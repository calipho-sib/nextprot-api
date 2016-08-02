package com.nextprot.api.annotation.builder;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import org.dbunit.dataset.DataSetException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.annotation.IsoformAnnotation;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.commons.constants.QualityQualifier;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementBuilder;
import org.nextprot.commons.statements.StatementField;
import org.nextprot.commons.statements.StatementUtil;
import org.nextprot.commons.statements.constants.AnnotationType;

public class IsoformAnnotationBuilderTest {
	
	@Mock
	private TerminologyService terminologyService;
	
    @Before
    public void init() throws FileNotFoundException, DataSetException {

        MockitoAnnotations.initMocks(this);

		CvTerm cvterm = new CvTerm();
		cvterm.setName("eco-name-1");
		Mockito.when(terminologyService.findCvTermByAccession(Matchers.anyString())).thenReturn(cvterm);

    }
	
	@Test
	public void shouldReturnOneSingleAnnotationIfTheInfoIsTheSameAndItIsComingFromDifferentSources() {
		
		Statement sb1 = StatementBuilder.createNew().
				addCompulsaryFields("NX_P01308", "NX_P01308-1", "go-cellular-component", QualityQualifier.GOLD)
   		  .addCvTerm("go-xxx", "nucleus", "go-cellular-component-cv")
    	  .addField(StatementField.EVIDENCE_CODE, "ECO:00001")
   		  .addSourceInfo("CAVA-VP0920190912", "BioEditor").build();
		
		Statement sb2 = StatementBuilder.createNew().
				addCompulsaryFields("NX_P01308", "NX_P01308-1", "go-cellular-component", QualityQualifier.GOLD)
				.addCvTerm("go-xxx", "nucleus", "go-cellular-component-cv")
				.addField(StatementField.EVIDENCE_CODE, "ECO:00001")
				.addSourceInfo("HPA2222", "HPA").build();
		
		
		List<Statement> statements = Arrays.asList(sb1, sb2);
		StatementUtil.computeAndSetAnnotationIdsForRawStatements(statements, AnnotationType.ISOFORM);
		

		IsoformAnnotation annotation = IsoformAnnotationBuilder.newBuilder(terminologyService).buildAnnotation("NX_P01308-1", statements);

		assertEquals(annotation.getAPICategory(), AnnotationCategory.GO_CELLULAR_COMPONENT);
		assertEquals(annotation.getEvidences().size(), 2);
		assertEquals(annotation.getEvidences().get(0).getEvidenceCodeName(), "eco-name-1");

	}


	@Test(expected = NextProtException.class)
	public void shouldReturnAnExceptionIf2AnnotationsAreExpectedInsteadOfOne() {
		
		Statement sb1 = StatementBuilder.createNew().
				addCompulsaryFields("NX_P01308", "NX_P01308-1", "go-cellular-component", QualityQualifier.GOLD).build();
   	
		Statement sb2 = StatementBuilder.createNew().
				addCompulsaryFields("NX_P99999", "NX_P99999-1", "go-cellular-component", QualityQualifier.GOLD).build();
	
		List<Statement> statements = Arrays.asList(sb1, sb2);
		StatementUtil.computeAndSetAnnotationIdsForRawStatements(statements, AnnotationType.ISOFORM);
		
		IsoformAnnotationBuilder.newBuilder(terminologyService).buildAnnotation("NX_P01308-1", statements);
		
	}
	
	
	@Test
	public void shouldReturnCorrectEcoName() {
		
		Statement sb1 = StatementBuilder.createNew().
				addCompulsaryFields("NX_P01308", "NX_P01308-1", "go-cellular-component", QualityQualifier.GOLD)
    	  .addField(StatementField.EVIDENCE_CODE, "ECO:00001").build();
		
		List<Statement> statements = Arrays.asList(sb1);
		StatementUtil.computeAndSetAnnotationIdsForRawStatements(statements, AnnotationType.ISOFORM);

		IsoformAnnotation annotation = IsoformAnnotationBuilder.newBuilder(terminologyService).buildAnnotation("NX_P01308-1", statements);

		assertEquals(annotation.getAPICategory(), AnnotationCategory.GO_CELLULAR_COMPONENT);
		assertEquals(annotation.getEvidences().size(), 1);
		assertEquals("eco-name-1", annotation.getEvidences().get(0).getEvidenceCodeName());

	}


}
