package org.nextprot.api.core.service;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.commons.constants.QualityQualifier;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementBuilder;
import org.nextprot.commons.statements.TargetIsoformSet;
import org.nextprot.commons.statements.specs.CustomStatementField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import static org.nextprot.commons.statements.specs.CoreStatementField.*;

public class EntryAnnotationBuilderTest extends AnnotationBuilderBastUnitTest {

	@Test
	public void shouldFindCorrectPublicationId() {

		Statement sb1 = new StatementBuilder().addField(REFERENCE_DATABASE, "PubMed")
				.addField(REFERENCE_ACCESSION, "123").build();

		StatementAnnotationBuilder ab = StatementEntryAnnotationBuilder.newBuilder(terminologyService,
				publicationService, mainNamesService, dbXrefService);
		ab.findPublicationId(sb1);
	}

	@Test(expected = NextProtException.class)
	public void shouldThrowAnExceptionIfInModeStrictAndPublicationIsNotFound() {

		Statement sb1 = new StatementBuilder().addField(REFERENCE_DATABASE, "PubMed")
				.addField(REFERENCE_ACCESSION, "000").build();
		StatementAnnotationBuilder ab = StatementEntryAnnotationBuilder.newBuilder(terminologyService,
				publicationService, mainNamesService, dbXrefService);
		ab.findPublicationId(sb1);
	}

	@Override
	protected StatementAnnotationBuilder newAnnotationBuilder() {
		return StatementEntryAnnotationBuilder.newBuilder(terminologyService, publicationService, mainNamesService,
				dbXrefService);
	}

	@Test
	public void shouldReturnOneSingleAnnotationIfTheInfoIsTheSameAndItIsComingFromDifferentSources() {

		Statement sb1 = new StatementBuilder()
				.addCompulsoryFields("NX_P01308", "NX_P01308", "go-cellular-component", QualityQualifier.GOLD)
				.addCvTerm("go-xxx", "nucleus", "go-cellular-component-cv").addField(REFERENCE_DATABASE, "PubMed")
				.addField(REFERENCE_ACCESSION, "123").addField(RESOURCE_TYPE, "publication")
				.addTargetIsoformsField(new TargetIsoformSet()).addField(EVIDENCE_CODE, "ECO:00001")
				.addField(ASSIGNED_BY, "TUTU").addSourceInfo("CAVA-VP0920190912", "BioEditor").withAnnotationHash()
				.build();

		Statement sb2 = new StatementBuilder()
				.addCompulsoryFields("NX_P01308", "NX_P01308", "go-cellular-component", QualityQualifier.GOLD)
				.addField(REFERENCE_DATABASE, "PubMed").addField(REFERENCE_ACCESSION, "123")
				.addField(RESOURCE_TYPE, "publication").addTargetIsoformsField(new TargetIsoformSet())
				.addCvTerm("go-xxx", "nucleus", "go-cellular-component-cv").addField(EVIDENCE_CODE, "ECO:00001")
				.addField(ASSIGNED_BY, "TOTO").addSourceInfo("HPA2222", "HPA").withAnnotationHash().build();

		List<Statement> statements = Arrays.asList(sb1, sb2);

		Annotation annotation = newAnnotationBuilder().buildAnnotation("NX_P01308", statements);

		Assert.assertEquals(annotation.getAPICategory(), AnnotationCategory.GO_CELLULAR_COMPONENT);
		Assert.assertEquals(annotation.getEvidences().size(), 2);
		Assert.assertEquals(annotation.getEvidences().get(0).getEvidenceCodeName(), "eco-name-1");

	}

	@Test(expected = NextProtException.class)
	public void shouldReturnAnExceptionIf2AnnotationsAreExpectedInsteadOfOne() {

		Statement sb1 = new StatementBuilder()
				.addCompulsoryFields("NX_P01308", "NX_P01308", "go-cellular-component", QualityQualifier.GOLD)
				.addField(REFERENCE_DATABASE, "PubMed").addField(REFERENCE_ACCESSION, "123")
				.addField(RESOURCE_TYPE, "publication").addTargetIsoformsField(new TargetIsoformSet())
				.withAnnotationHash().build();

		Statement sb2 = new StatementBuilder()
				.addCompulsoryFields("NX_P99999", "NX_P99999", "go-cellular-component", QualityQualifier.GOLD)
				.addField(REFERENCE_DATABASE, "PubMed").addField(REFERENCE_ACCESSION, "123")
				.addTargetIsoformsField(new TargetIsoformSet()).withAnnotationHash().build();

		List<Statement> statements = Arrays.asList(sb1, sb2);

		newAnnotationBuilder().buildAnnotation("NX_P01308", statements);

	}

	@Test
	public void shouldReturnCorrectEcoName() {

		Statement sb1 = new StatementBuilder()
				.addCompulsoryFields("NX_P01308", "NX_P01308-1", "go-cellular-component", QualityQualifier.GOLD)
				.addField(EVIDENCE_CODE, "ECO:00001").addField(REFERENCE_DATABASE, "PubMed")
				.addField(REFERENCE_ACCESSION, "123").addField(RESOURCE_TYPE, "publication")
				.addTargetIsoformsField(new TargetIsoformSet()).withAnnotationHash().build();

		List<Statement> statements = Arrays.asList(sb1);

		Annotation annotation = newAnnotationBuilder().buildAnnotation("NX_P01308-1", statements);

		Assert.assertEquals(annotation.getAPICategory(), AnnotationCategory.GO_CELLULAR_COMPONENT);
		Assert.assertEquals(annotation.getEvidences().size(), 1);
		Assert.assertEquals("eco-name-1", annotation.getEvidences().get(0).getEvidenceCodeName());

	}

	@Test
	public void shouldReturnPsimiAsEvidenceProperty() { // T01

		StatementAnnotationBuilder ab = StatementEntryAnnotationBuilder.newBuilder(terminologyService,
				publicationService, mainNamesService, dbXrefService);

		Statement sb1 = new StatementBuilder()
				.addCompulsoryFields("NX_P38398", "NX_P38398", "binary-interaction", QualityQualifier.GOLD)
				.addField(EVIDENCE_CODE, "ECO:0000353").addField(REFERENCE_DATABASE, "PubMed")
				.addField(new CustomStatementField("PSIMI_ID"), "MI:0943").addField(ASSIGNED_BY, "ENYO")
				.addTargetIsoformsField(new TargetIsoformSet()).addField(REFERENCE_ACCESSION, "123")
				.addField(RESOURCE_TYPE, "publication").withAnnotationHash().build();

		List<Statement> statements = Arrays.asList(sb1);

		Annotation annotation = ab.buildAnnotation("NX_P38398", statements);

		Assert.assertEquals(annotation.getAPICategory(), AnnotationCategory.BINARY_INTERACTION);
		Assert.assertEquals(annotation.getEvidences().size(), 1);
		Assert.assertEquals(annotation.getEvidences().get(0).getProperties().get("psimiId"), "MI:0943");
	}

	@Test
	public void shouldReturn2DifferentEvidencesFor1Publication1PPi2Psimi() { // T02

		StatementAnnotationBuilder ab = StatementEntryAnnotationBuilder.newBuilder(terminologyService,
				publicationService, mainNamesService, dbXrefService);

		Statement sb_psimi1 = new StatementBuilder()
				.addCompulsoryFields("NX_P38398", "NX_P38398", "binary-interaction", QualityQualifier.GOLD)
				.addField(EVIDENCE_CODE, "ECO:0000353").addField(REFERENCE_DATABASE, "PubMed")
				.addField(new CustomStatementField("PSIMI_ID"), "MI:0943").addField(ASSIGNED_BY, "ENYO")
				.addTargetIsoformsField(new TargetIsoformSet()).addField(REFERENCE_ACCESSION, "123")
				.addField(RESOURCE_TYPE, "publication").withAnnotationHash().build();

		Statement sb_psimi2 = new StatementBuilder()
				.addCompulsoryFields("NX_P38398", "NX_P38398", "binary-interaction", QualityQualifier.GOLD)
				.addField(EVIDENCE_CODE, "ECO:0000353").addField(REFERENCE_DATABASE, "PubMed")
				.addField(new CustomStatementField("PSIMI_ID"), "MI:0003").addField(ASSIGNED_BY, "ENYO")
				.addTargetIsoformsField(new TargetIsoformSet()).addField(REFERENCE_ACCESSION, "123")
				.addField(RESOURCE_TYPE, "publication").withAnnotationHash().build();

		List<Statement> statements = Arrays.asList(sb_psimi1, sb_psimi2);

		Annotation annotation = ab.buildAnnotation("NX_P38398", statements);

		Assert.assertEquals(annotation.getAPICategory(), AnnotationCategory.BINARY_INTERACTION);
		Assert.assertEquals(annotation.getEvidences().size(), 2);
		Assert.assertEquals(annotation.getEvidences().get(0).getProperties().get("psimiId"), "MI:0943");
		Assert.assertEquals(annotation.getEvidences().get(1).getProperties().get("psimiId"), "MI:0003");
	}

	@Test
	public void shouldReturn2DifferentEvidencesForIntactAndEnyoWithPsimi() { // T03

		StatementAnnotationBuilder ab = StatementEntryAnnotationBuilder.newBuilder(terminologyService,
				publicationService, mainNamesService, dbXrefService);

		Statement sb_intact = new StatementBuilder()
				.addCompulsoryFields("NX_P38398", "NX_P38398", "binary-interaction", QualityQualifier.GOLD)
				.addField(EVIDENCE_CODE, "ECO:0000353").addField(REFERENCE_DATABASE, "PubMed")
				.addField(ASSIGNED_BY, "IntAct").addTargetIsoformsField(new TargetIsoformSet())
				.addField(REFERENCE_ACCESSION, "123").addField(RESOURCE_TYPE, "publication").withAnnotationHash()
				.build();

		Statement sb_enyo = new StatementBuilder()
				.addCompulsoryFields("NX_P38398", "NX_P38398", "binary-interaction", QualityQualifier.GOLD)
				.addField(EVIDENCE_CODE, "ECO:0000353").addField(REFERENCE_DATABASE, "PubMed")
				.addField(new CustomStatementField("PSIMI_ID"), "MI:0003").addField(ASSIGNED_BY, "ENYO")
				.addTargetIsoformsField(new TargetIsoformSet()).addField(REFERENCE_ACCESSION, "123")
				.addField(RESOURCE_TYPE, "publication").withAnnotationHash().build();

		List<Statement> statements = Arrays.asList(sb_intact, sb_enyo);

		Annotation annotation = ab.buildAnnotation("NX_P38398", statements);

		Assert.assertEquals(annotation.getAPICategory(), AnnotationCategory.BINARY_INTERACTION);
		Assert.assertEquals(annotation.getEvidences().size(), 2);
		Assert.assertEquals(annotation.getEvidences().get(0).getProperties().get("psimiId"), null);
		Assert.assertEquals(annotation.getEvidences().get(1).getProperties().get("psimiId"), "MI:0003");
	}

	@Test
	public void shouldReturn3DifferentEvidencesForIntactAndEnyoWith2Psimi() { // T04

		StatementAnnotationBuilder ab = StatementEntryAnnotationBuilder.newBuilder(terminologyService,
				publicationService, mainNamesService, dbXrefService);

		Statement sb_intact = new StatementBuilder()
				.addCompulsoryFields("NX_P38398", "NX_P38398", "binary-interaction", QualityQualifier.GOLD)
				.addField(EVIDENCE_CODE, "ECO:0000353").addField(REFERENCE_DATABASE, "PubMed")
				.addField(ASSIGNED_BY, "IntAct").addTargetIsoformsField(new TargetIsoformSet())
				.addField(REFERENCE_ACCESSION, "123").addField(RESOURCE_TYPE, "publication").withAnnotationHash()
				.build();

		Statement sb_enyo_psimi1 = new StatementBuilder()
				.addCompulsoryFields("NX_P38398", "NX_P38398", "binary-interaction", QualityQualifier.GOLD)
				.addField(EVIDENCE_CODE, "ECO:0000353").addField(REFERENCE_DATABASE, "PubMed")
				.addField(new CustomStatementField("PSIMI_ID"), "MI:0003").addField(ASSIGNED_BY, "ENYO")
				.addTargetIsoformsField(new TargetIsoformSet()).addField(REFERENCE_ACCESSION, "123")
				.addField(RESOURCE_TYPE, "publication").withAnnotationHash().build();

		Statement sb_enyo_psimi2 = new StatementBuilder()
				.addCompulsoryFields("NX_P38398", "NX_P38398", "binary-interaction", QualityQualifier.GOLD)
				.addField(EVIDENCE_CODE, "ECO:0000353").addField(REFERENCE_DATABASE, "PubMed")
				.addField(new CustomStatementField("PSIMI_ID"), "MI:0008").addField(ASSIGNED_BY, "ENYO")
				.addTargetIsoformsField(new TargetIsoformSet()).addField(REFERENCE_ACCESSION, "123")
				.addField(RESOURCE_TYPE, "publication").withAnnotationHash().build();

		List<Statement> statements = Arrays.asList(sb_enyo_psimi1, sb_enyo_psimi2, sb_intact);

		Annotation annotation = ab.buildAnnotation("NX_P38398", statements);

		Assert.assertEquals(annotation.getAPICategory(), AnnotationCategory.BINARY_INTERACTION);
		Assert.assertEquals(annotation.getEvidences().size(), 3);
		Assert.assertEquals(annotation.getEvidences().get(2).getProperties().get("psimiId"), "MI:0003");
		Assert.assertEquals(annotation.getEvidences().get(0).getProperties().get("psimiId"), "MI:0008");
		Assert.assertEquals(annotation.getEvidences().get(1).getProperties().get("psimiId"), null);
	}

	@Test
	public void shouldReturn2DifferentEvidencesForNextprotAndEnyoWithPsimi() { // T053

		StatementAnnotationBuilder ab = StatementEntryAnnotationBuilder.newBuilder(terminologyService,
				publicationService, mainNamesService, dbXrefService);

		Statement sb_nextprot = new StatementBuilder()
				.addCompulsoryFields("NX_P38398", "NX_P38398", "binary-interaction", QualityQualifier.GOLD)
				.addField(EVIDENCE_CODE, "ECO:0000353").addField(REFERENCE_DATABASE, "PubMed")
				.addField(ASSIGNED_BY, "Nextprot").addTargetIsoformsField(new TargetIsoformSet())
				.addField(REFERENCE_ACCESSION, "123").addField(RESOURCE_TYPE, "publication").withAnnotationHash()
				.build();

		Statement sb_enyo = new StatementBuilder()
				.addCompulsoryFields("NX_P38398", "NX_P38398", "binary-interaction", QualityQualifier.GOLD)
				.addField(EVIDENCE_CODE, "ECO:0000353").addField(REFERENCE_DATABASE, "PubMed")
				.addField(new CustomStatementField("PSIMI_ID"), "MI:0003").addField(ASSIGNED_BY, "ENYO")
				.addTargetIsoformsField(new TargetIsoformSet()).addField(REFERENCE_ACCESSION, "123")
				.addField(RESOURCE_TYPE, "publication").withAnnotationHash().build();

		List<Statement> statements = Arrays.asList(sb_enyo, sb_nextprot);

		Annotation annotation = ab.buildAnnotation("NX_P38398", statements);

		Assert.assertEquals(annotation.getAPICategory(), AnnotationCategory.BINARY_INTERACTION);
		Assert.assertEquals(annotation.getEvidences().size(), 2);
		Assert.assertEquals(annotation.getEvidences().get(1).getProperties().get("psimiId"), "MI:0003");
		Assert.assertEquals(annotation.getEvidences().get(0).getProperties().get("psimiId"), null);
	}

	@Test
	public void shouldReturn3DifferentEvidencesForNextprotAndEnyoWith2Psimi() { // T054

		StatementAnnotationBuilder ab = StatementEntryAnnotationBuilder.newBuilder(terminologyService,
				publicationService, mainNamesService, dbXrefService);

		Statement sb_nextprot = new StatementBuilder()
				.addCompulsoryFields("NX_P38398", "NX_P38398", "binary-interaction", QualityQualifier.GOLD)
				.addField(EVIDENCE_CODE, "ECO:0000353").addField(REFERENCE_DATABASE, "PubMed")
				.addField(ASSIGNED_BY, "Nextprot").addTargetIsoformsField(new TargetIsoformSet())
				.addField(REFERENCE_ACCESSION, "123").addField(RESOURCE_TYPE, "publication").withAnnotationHash()
				.build();

		Statement sb_enyo_psimi1 = new StatementBuilder()
				.addCompulsoryFields("NX_P38398", "NX_P38398", "binary-interaction", QualityQualifier.GOLD)
				.addField(EVIDENCE_CODE, "ECO:0000353").addField(REFERENCE_DATABASE, "PubMed")
				.addField(new CustomStatementField("PSIMI_ID"), "MI:0003").addField(ASSIGNED_BY, "ENYO")
				.addTargetIsoformsField(new TargetIsoformSet()).addField(REFERENCE_ACCESSION, "123")
				.addField(RESOURCE_TYPE, "publication").withAnnotationHash().build();

		Statement sb_enyo_psimi2 = new StatementBuilder()
				.addCompulsoryFields("NX_P38398", "NX_P38398", "binary-interaction", QualityQualifier.GOLD)
				.addField(EVIDENCE_CODE, "ECO:0000353").addField(REFERENCE_DATABASE, "PubMed")
				.addField(new CustomStatementField("PSIMI_ID"), "MI:0008").addField(ASSIGNED_BY, "ENYO")
				.addTargetIsoformsField(new TargetIsoformSet()).addField(REFERENCE_ACCESSION, "123")
				.addField(RESOURCE_TYPE, "publication").withAnnotationHash().build();

		List<Statement> statements = Arrays.asList(sb_enyo_psimi1, sb_enyo_psimi2, sb_nextprot);

		Annotation annotation = ab.buildAnnotation("NX_P38398", statements);

		Assert.assertEquals(annotation.getAPICategory(), AnnotationCategory.BINARY_INTERACTION);
		Assert.assertEquals(annotation.getEvidences().size(), 3);
		Assert.assertEquals(annotation.getEvidences().get(2).getProperties().get("psimiId"), "MI:0003");
		Assert.assertEquals(annotation.getEvidences().get(0).getProperties().get("psimiId"), "MI:0008");
		Assert.assertEquals(annotation.getEvidences().get(1).getProperties().get("psimiId"), null);
	}

}
