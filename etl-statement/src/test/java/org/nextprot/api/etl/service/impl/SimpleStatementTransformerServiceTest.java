package org.nextprot.api.etl.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.nextprot.api.etl.domain.IsoformPositions;
import org.nextprot.api.etl.service.SimpleStatementTransformerService;
import org.nextprot.api.etl.service.StatementIsoformPositionService;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementBuilder;
import org.nextprot.commons.statements.TargetIsoformSet;
import org.nextprot.commons.statements.TargetIsoformStatementPosition;
import org.nextprot.commons.statements.specs.StatementField;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.nextprot.commons.statements.specs.CoreStatementField.*;

public class SimpleStatementTransformerServiceTest {

	@InjectMocks
	private SimpleStatementTransformerService simpleStatementTransformerService = new SimpleStatementTransformerServiceImpl();

	@Mock
	private StatementIsoformPositionService statementIsoformPositionService;

	private Statement rawSCN9Aiso3Met932LeuSubject;
	private Statement rawSCN9Aiso3Val991LeuSubject;

	@Before
	public void init() {

		MockitoAnnotations.initMocks(this);

		rawSCN9Aiso3Met932LeuSubject = new StatementBuilder()
				.addField(ENTRY_ACCESSION, "NX_Q15858")
				.addField(ANNOTATION_NAME, "SCN9A-iso3-p.Met932Leu")
				.addField(ANNOTATION_CATEGORY, "variant")
				.build();

		rawSCN9Aiso3Val991LeuSubject = new StatementBuilder()
				.addField(ENTRY_ACCESSION, "NX_Q15858")
				.addField(ANNOTATION_NAME, "SCN9A-iso3-p.Val991Leu")
				.addField(ANNOTATION_CATEGORY, "variant")
				.build();

		mockStatementIsoformPositionService(rawSCN9Aiso3Met932LeuSubject, expectedSCN9Aiso3Met932LeuIsoformPositions());
		mockStatementIsoformPositionService(rawSCN9Aiso3Val991LeuSubject, expectedSCN9Aiso3Val991LeuIsoformPositions());
	}

	// FIXME: redundant with transformStatement ?
	@Test
	public void shouldTransformSubject() {

		Statement transformSubject = simpleStatementTransformerService.transformSubject(rawSCN9Aiso3Met932LeuSubject);

		assertEqualsStatements(expectedSCN9Aiso3Met932LeuStatement(), transformSubject);
	}

	// FIXME: redundant with transformSubject ?
	@Test
	public void shouldTransformStatement() {

		Optional<Statement> transformSubject = simpleStatementTransformerService.transformStatement(rawSCN9Aiso3Met932LeuSubject);

		Assert.assertTrue(transformSubject.isPresent());
		assertEqualsStatements(expectedSCN9Aiso3Met932LeuStatement(), transformSubject.get());
	}

	@Test
	public void shouldTransformSubjects() {

		List<Statement> rawSubjects = Arrays.asList(rawSCN9Aiso3Met932LeuSubject, rawSCN9Aiso3Val991LeuSubject);

		List<Statement> transformSubjects = simpleStatementTransformerService.transformSubjects(rawSubjects);

		List<Statement> expectedTransformedSubjects = Arrays.asList(
				expectedSCN9Aiso3Met932LeuStatement(),
				expectedSCN9Aiso3Val991LeuStatement()
		);

		IntStream.range(0, 2).forEach(i -> assertEqualsStatements(expectedTransformedSubjects.get(i), transformSubjects.get(i)));
	}

	private void mockStatementIsoformPositionService(Statement statement, IsoformPositions isoformPositions) {

		Mockito.when(statementIsoformPositionService.computeIsoformPositionsForNormalAnnotation(statement))
				.thenReturn(isoformPositions);
	}

	private void assertEqualsStatements(Statement expected, Statement statement) {

		for (StatementField field : statement.keySet()) {

			if (statement.hasField(field.getName())
					&& !field.equals(ANNOTATION_ID)
					&& !field.equals(RAW_STATEMENT_ID)
					&& !field.equals(STATEMENT_ID)) {
				Assert.assertEquals(expected.getValue(field), statement.getValue(field));
			}
		}
	}

	public static Statement expectedSCN9Aiso3Met932LeuStatement() {

		return new StatementBuilder()
				.addField(ENTRY_ACCESSION, "NX_Q15858")
				.addField(ANNOTATION_NAME, "SCN9A-iso3-p.Met932Leu")
				.addField(ANNOTATION_CATEGORY, "variant")
				.addField(ISOFORM_CANONICAL, "NX_Q15858-1")
				.addField(LOCATION_BEGIN, "943")
				.addField(LOCATION_END, "943")
				.addField(LOCATION_BEGIN_MASTER, "98972")
				.addField(LOCATION_END_MASTER, "98974")
				.addField(TARGET_ISOFORMS, "[{\"isoformAccession\":\"NX_Q15858-1\",\"specificity\":\"UNKNOWN\",\"begin\":943,\"end\":943,\"name\":\"SCN9A-iso1-p.Met943Leu\"},{\"isoformAccession\":\"NX_Q15858-2\",\"specificity\":\"UNKNOWN\",\"begin\":943,\"end\":943,\"name\":\"SCN9A-iso2-p.Met943Leu\"},{\"isoformAccession\":\"NX_Q15858-3\",\"specificity\":\"UNKNOWN\",\"begin\":932,\"end\":932,\"name\":\"SCN9A-iso3-p.Met932Leu\"},{\"isoformAccession\":\"NX_Q15858-4\",\"specificity\":\"UNKNOWN\",\"begin\":932,\"end\":932,\"name\":\"SCN9A-iso4-p.Met932Leu\"}]")
				.build();
	}

	public static Statement expectedSCN9Aiso3Val991LeuStatement() {

		return new StatementBuilder()
				.addField(ENTRY_ACCESSION, "NX_Q15858")
				.addField(ANNOTATION_NAME, "SCN9A-iso3-p.Val991Leu")
				.addField(ANNOTATION_CATEGORY, "variant")
				.addField(ISOFORM_CANONICAL, "NX_Q15858-1")
				.addField(LOCATION_BEGIN, "1002")
				.addField(LOCATION_END, "1002")
				.addField(LOCATION_BEGIN_MASTER, "103256")
				.addField(LOCATION_END_MASTER, "103258")
				.addField(TARGET_ISOFORMS, "[{\"isoformAccession\":\"NX_Q15858-1\",\"specificity\":\"UNKNOWN\",\"begin\":1002,\"end\":1002,\"name\":\"SCN9A-iso1-p.Val1002Leu\"},{\"isoformAccession\":\"NX_Q15858-2\",\"specificity\":\"UNKNOWN\",\"begin\":1002,\"end\":1002,\"name\":\"SCN9A-iso2-p.Val1002Leu\"},{\"isoformAccession\":\"NX_Q15858-3\",\"specificity\":\"UNKNOWN\",\"begin\":991,\"end\":991,\"name\":\"SCN9A-iso3-p.Val991Leu\"},{\"isoformAccession\":\"NX_Q15858-4\",\"specificity\":\"UNKNOWN\",\"begin\":991,\"end\":991,\"name\":\"SCN9A-iso4-p.Val991Leu\"}]")
				.build();
	}

	private IsoformPositions expectedSCN9Aiso3Met932LeuIsoformPositions() {

		IsoformPositions isoformPositions = new IsoformPositions();
		TargetIsoformSet targetIsoforms = new TargetIsoformSet();
		targetIsoforms.add(new TargetIsoformStatementPosition("NX_Q15858-1", 943, 943, "UNKNOWN", "SCN9A-iso1-p.Met943Leu"));
		targetIsoforms.add(new TargetIsoformStatementPosition("NX_Q15858-2", 943, 943, "UNKNOWN", "SCN9A-iso2-p.Met943Leu"));
		targetIsoforms.add(new TargetIsoformStatementPosition("NX_Q15858-3", 932, 932, "UNKNOWN", "SCN9A-iso3-p.Met932Leu"));
		targetIsoforms.add(new TargetIsoformStatementPosition("NX_Q15858-4", 932, 932, "UNKNOWN", "SCN9A-iso4-p.Met932Leu"));
		isoformPositions.setCanonicalIsoform("NX_Q15858-1");
		isoformPositions.setBeginPositionOfCanonicalOrIsoSpec(943);
		isoformPositions.setEndPositionOfCanonicalOrIsoSpec(943);
		isoformPositions.setMasterBeginPosition(98972);
		isoformPositions.setMasterEndPosition(98974);
		isoformPositions.setTargetIsoformSet(targetIsoforms);

		return isoformPositions;
	}

	private IsoformPositions expectedSCN9Aiso3Val991LeuIsoformPositions() {

		IsoformPositions isoformPositions = new IsoformPositions();
		TargetIsoformSet targetIsoforms = new TargetIsoformSet();
		targetIsoforms.add(new TargetIsoformStatementPosition("NX_Q15858-1", 1002, 1002, "UNKNOWN", "SCN9A-iso1-p.Val1002Leu"));
		targetIsoforms.add(new TargetIsoformStatementPosition("NX_Q15858-2", 1002, 1002, "UNKNOWN", "SCN9A-iso2-p.Val1002Leu"));
		targetIsoforms.add(new TargetIsoformStatementPosition("NX_Q15858-3", 991, 991, "UNKNOWN", "SCN9A-iso3-p.Val991Leu"));
		targetIsoforms.add(new TargetIsoformStatementPosition("NX_Q15858-4", 991, 991, "UNKNOWN", "SCN9A-iso4-p.Val991Leu"));
		isoformPositions.setCanonicalIsoform("NX_Q15858-1");
		isoformPositions.setBeginPositionOfCanonicalOrIsoSpec(1002);
		isoformPositions.setEndPositionOfCanonicalOrIsoSpec(1002);
		isoformPositions.setMasterBeginPosition(103256);
		isoformPositions.setMasterEndPosition(103258);
		isoformPositions.setTargetIsoformSet(targetIsoforms);

		return isoformPositions;
	}
}