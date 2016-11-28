package com.nextprot.api.annotation.builder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.commons.constants.QualityQualifier;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementBuilder;
import org.nextprot.commons.statements.StatementField;
import org.nextprot.commons.statements.constants.AnnotationType;

import com.nextprot.api.annotation.builder.statement.TargetIsoformSerializer;

public class EntryAnnotationBuilderTest extends AnnotationBuilderBastUnitTest{

	@AfterClass
	public static void tearDown() {
		AnnotationBuilder.STRICT = false;
	}

	@Test
	public void shouldFindCorrectPublicationId() {

		Statement sb1 = StatementBuilder.createNew()
				.addField(StatementField.REFERENCE_DATABASE, "PubMed")
				.addField(StatementField.REFERENCE_ACCESSION, "123").build();
		
		AnnotationBuilder ab = EntryAnnotationBuilder.newBuilder(terminologyService, publicationService, mainNamesService);
		AnnotationEvidence evidence = new AnnotationEvidence();
		ab.setEvidenceResourceId(evidence, sb1);
		Assert.assertEquals(evidence.getResourceId(), 999);
		
	}

	
	
	@Test(expected = NextProtException.class)
	public void shouldThrowAnExceptionIfInModeStrictAndPublicationIsNotFound() {

		AnnotationBuilder.STRICT = true;
		Statement sb1 = StatementBuilder.createNew()
				.addField(StatementField.REFERENCE_DATABASE, "PubMed")
				.addField(StatementField.REFERENCE_ACCESSION, "000").build();
		AnnotationBuilder ab = EntryAnnotationBuilder.newBuilder(terminologyService, publicationService, mainNamesService);
		AnnotationEvidence evidence = new AnnotationEvidence();
		ab.setEvidenceResourceId(evidence, sb1);
		
	}
	
	@Override
	protected EntryAnnotationBuilder newAnnotationBuilder() {
		return EntryAnnotationBuilder.newBuilder(terminologyService, publicationService, mainNamesService);
	}
	
	
	
	@Test
	public void shouldReturnOneSingleAnnotationIfTheInfoIsTheSameAndItIsComingFromDifferentSources() {
		
		Statement sb1 = StatementBuilder.createNew().
				addCompulsaryFields("NX_P01308", "NX_P01308", "go-cellular-component", QualityQualifier.GOLD)
   		  .addCvTerm("go-xxx", "nucleus", "go-cellular-component-cv")
    	  .addField(StatementField.REFERENCE_DATABASE, "PubMed")
    	  .addField(StatementField.REFERENCE_ACCESSION, "123")
    	  .addField(StatementField.TARGET_ISOFORMS, TargetIsoformSerializer.serializeToJsonString(new HashSet<>()))
    	  .addField(StatementField.EVIDENCE_CODE, "ECO:00001")
			.addField(StatementField.ASSIGNED_BY, "TUTU")
    	  .addSourceInfo("CAVA-VP0920190912", "BioEditor").buildWithAnnotationHash(AnnotationType.ENTRY);
		
		Statement sb2 = StatementBuilder.createNew().
				addCompulsaryFields("NX_P01308", "NX_P01308", "go-cellular-component", QualityQualifier.GOLD)
		    	  .addField(StatementField.REFERENCE_DATABASE, "PubMed")
		    	  .addField(StatementField.REFERENCE_ACCESSION, "123")
		    	  .addField(StatementField.TARGET_ISOFORMS, TargetIsoformSerializer.serializeToJsonString(new HashSet<>()))
		    	  .addCvTerm("go-xxx", "nucleus", "go-cellular-component-cv")
				.addField(StatementField.EVIDENCE_CODE, "ECO:00001")
				.addField(StatementField.ASSIGNED_BY, "TOTO")
				.addSourceInfo("HPA2222", "HPA").buildWithAnnotationHash(AnnotationType.ENTRY);
		
		
		List<Statement> statements = Arrays.asList(sb1, sb2);

		Annotation annotation = newAnnotationBuilder().buildAnnotation("NX_P01308", statements);

		Assert.assertEquals(annotation.getAPICategory(), AnnotationCategory.GO_CELLULAR_COMPONENT);
		Assert.assertEquals(annotation.getEvidences().size(), 2);
		Assert.assertEquals(annotation.getEvidences().get(0).getEvidenceCodeName(), "eco-name-1");

	}


	@Test(expected = NextProtException.class)
	public void shouldReturnAnExceptionIf2AnnotationsAreExpectedInsteadOfOne() {
		
		Statement sb1 = StatementBuilder.createNew().
				addCompulsaryFields("NX_P01308", "NX_P01308", "go-cellular-component", QualityQualifier.GOLD)
		    	  .addField(StatementField.REFERENCE_DATABASE, "PubMed")
		    	  .addField(StatementField.REFERENCE_ACCESSION, "123")
		    	  .addField(StatementField.TARGET_ISOFORMS, TargetIsoformSerializer.serializeToJsonString(new HashSet<>()))
		    	  .buildWithAnnotationHash(AnnotationType.ENTRY);
		
		Statement sb2 = StatementBuilder.createNew().
				addCompulsaryFields("NX_P99999", "NX_P99999", "go-cellular-component", QualityQualifier.GOLD)
		    	  .addField(StatementField.REFERENCE_DATABASE, "PubMed")
		    	  .addField(StatementField.REFERENCE_ACCESSION, "123")
		    	  .addField(StatementField.TARGET_ISOFORMS, TargetIsoformSerializer.serializeToJsonString(new HashSet<>()))
		    	  .buildWithAnnotationHash(AnnotationType.ENTRY);
	
		List<Statement> statements = Arrays.asList(sb1, sb2);
		
		newAnnotationBuilder().buildAnnotation("NX_P01308", statements);
		
	}
	
	
	@Test
	public void shouldReturnCorrectEcoName() {
		
		Statement sb1 = StatementBuilder.createNew().
				addCompulsaryFields("NX_P01308", "NX_P01308-1", "go-cellular-component", QualityQualifier.GOLD)
    	  .addField(StatementField.EVIDENCE_CODE, "ECO:00001")
    	  .addField(StatementField.REFERENCE_DATABASE, "PubMed")
    	  .addField(StatementField.REFERENCE_ACCESSION, "123")
    	  .addField(StatementField.TARGET_ISOFORMS, TargetIsoformSerializer.serializeToJsonString(new HashSet<>()))
    	  .buildWithAnnotationHash(AnnotationType.ENTRY);
		
		List<Statement> statements = Arrays.asList(sb1);

		Annotation annotation = newAnnotationBuilder().buildAnnotation("NX_P01308-1", statements);

		Assert.assertEquals(annotation.getAPICategory(), AnnotationCategory.GO_CELLULAR_COMPONENT);
		Assert.assertEquals(annotation.getEvidences().size(), 1);
		Assert.assertEquals("eco-name-1", annotation.getEvidences().get(0).getEvidenceCodeName());

	}


}
