package com.nextprot.api.annotation.builder;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.annotation.IsoformAnnotation;
import org.nextprot.commons.constants.QualityQualifier;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementBuilder;
import org.nextprot.commons.statements.StatementField;
import org.nextprot.commons.statements.StatementUtil;
import org.nextprot.commons.statements.constants.AnnotationType;

public class IsoformAnnotationBuilderTest extends AnnotationBuilderBastUnitTest{

	@Override
	protected IsoformAnnotationBuilder newAnnotationBuilder(){
		return IsoformAnnotationBuilder.newBuilder(terminologyService, publicationService);
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
		

		IsoformAnnotation annotation = newAnnotationBuilder().buildAnnotation("NX_P01308-1", statements);

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
		
		newAnnotationBuilder().buildAnnotation("NX_P01308-1", statements);
		
	}
	
	
	@Test
	public void shouldReturnCorrectEcoName() {
		
		Statement sb1 = StatementBuilder.createNew().
				addCompulsaryFields("NX_P01308", "NX_P01308-1", "go-cellular-component", QualityQualifier.GOLD)
    	  .addField(StatementField.EVIDENCE_CODE, "ECO:00001").build();
		
		List<Statement> statements = Arrays.asList(sb1);
		StatementUtil.computeAndSetAnnotationIdsForRawStatements(statements, AnnotationType.ISOFORM);

		IsoformAnnotation annotation = newAnnotationBuilder().buildAnnotation("NX_P01308-1", statements);

		assertEquals(annotation.getAPICategory(), AnnotationCategory.GO_CELLULAR_COMPONENT);
		assertEquals(annotation.getEvidences().size(), 1);
		assertEquals("eco-name-1", annotation.getEvidences().get(0).getEvidenceCodeName());

	}


}
