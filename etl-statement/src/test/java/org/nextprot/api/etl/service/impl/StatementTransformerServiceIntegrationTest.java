package org.nextprot.api.etl.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nextprot.api.etl.StatementSourceEnum;
import org.nextprot.api.etl.service.StatementTransformerService;
import org.nextprot.api.etl.service.impl.SingleBatchStatementETLService.ReportBuilder;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.specs.StatementField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.nextprot.commons.statements.specs.CoreStatementField.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({"dev", "build"})
@ContextConfiguration("classpath:spring/core-context.xml")
public class StatementTransformerServiceIntegrationTest {

	@Autowired
	private StatementTransformerService statementTransformerService;

	@Test
	public void rawStatementsShouldBeWellConvertedToMappedStatements() throws IOException {

		StatementsExtractorLocalMockImpl sle = new StatementsExtractorLocalMockImpl();
		Collection<Statement> rawStatements = sle.getStatementsFromJsonFile(StatementSourceEnum.BioEditor, null, "msh2-multiple-mutant");

		Collection<Statement> mappedStatements =
				statementTransformerService.transformStatements(rawStatements, new ReportBuilder());

		int rawStatementsCount = (int) rawStatements.stream()
				.map(s -> s.getValue(STATEMENT_ID))
				.distinct()
				.count();
		int mappedStatementsCount = (int) rawStatements.stream()
				.map(s -> s.getValue(STATEMENT_ID))
				.distinct()
				.count();

		int annotationsCount = (int) mappedStatements.stream()
				.map(s -> s.getValue(ANNOTATION_ID))
				.distinct()
				.count();

		assertEquals(5, rawStatementsCount);
		assertEquals(5, mappedStatementsCount);
		assertEquals(4, annotationsCount);

		Statement phenotypicVariationStatement = filterStatementsBy(mappedStatements, ANNOTATION_CATEGORY, "phenotypic-variation").get(0);

		String[] subjectAnnotations = phenotypicVariationStatement.getValue(SUBJECT_ANNOTATION_IDS).split(",");

		String referenceVarAnnotation1 = subjectAnnotations[0];
		String referenceVarAnnotation2 = subjectAnnotations[1];

		String variantAnnotA = filterStatementsBy(mappedStatements, ANNOTATION_CATEGORY, "variant").get(0).getValue(ANNOTATION_ID);
		String variantAnnotB = filterStatementsBy(mappedStatements, ANNOTATION_CATEGORY, "variant").get(1).getValue(ANNOTATION_ID);


		Set<String> refAnnots = new TreeSet<>(Arrays.asList(referenceVarAnnotation1, referenceVarAnnotation2));
		Set<String> varAnnots = new TreeSet<>(Arrays.asList(variantAnnotA, variantAnnotB));

		assertEquals(refAnnots.size(), 2);
		assertEquals(refAnnots, varAnnots);
	}

	private List<Statement> filterStatementsBy(Collection<Statement> statements, StatementField field, String value){
		return statements.stream().filter(s -> value.equalsIgnoreCase(s.getValue(field))).collect(Collectors.toList());
	}
}
