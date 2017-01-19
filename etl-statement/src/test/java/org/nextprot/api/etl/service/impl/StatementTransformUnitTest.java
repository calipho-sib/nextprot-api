package org.nextprot.api.etl.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.junit.Test;
import org.nextprot.api.etl.service.impl.StatementETLServiceImpl.ReportBuilder;
import org.nextprot.api.etl.statement.StatementETLBaseUnitTest;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementField;

public class StatementTransformUnitTest extends StatementETLBaseUnitTest {

	private List<Statement> filterStatementsBy(Set<Statement> statements, StatementField field, String value){
		return statements.stream().filter(s -> value.equalsIgnoreCase(s.getValue(field))).collect(Collectors.toList());
	}
	
	@Test
	public void rawStatementsShouldBeWellConvertedToMappedStatements() {

		StatementsExtractorLocalMockImpl sle = new StatementsExtractorLocalMockImpl();
		Set<Statement> rawStatements = sle.getStatementsForSourceForGeneName(null, null, "msh2-multiple-mutant");

		Set<Statement> mappedStatements = statementETLServiceMocked.transformStatements(rawStatements, new ReportBuilder());

		int statementsCount = rawStatements.stream().map(s -> s.getValue(StatementField.STATEMENT_ID)).distinct().collect(Collectors.toList()).size();
		int annotationsCount = mappedStatements.stream().map(s -> s.getValue(StatementField.ANNOTATION_ID)).distinct().collect(Collectors.toList()).size();

		assertEquals(5, statementsCount);
		assertEquals(4, annotationsCount);

		Statement phenotypicVariationStatement = filterStatementsBy(mappedStatements, StatementField.ANNOTATION_CATEGORY, "phenotypic-variation").get(0);
		
		String[] subjectAnnotations = phenotypicVariationStatement.getValue(StatementField.SUBJECT_ANNOTATION_IDS).split(",");

		String referenceVarAnnotation1 = subjectAnnotations[0];
		String referenceVarAnnotation2 = subjectAnnotations[1];
		
		String variantAnnotA = filterStatementsBy(mappedStatements, StatementField.ANNOTATION_CATEGORY, "variant").get(0).getValue(StatementField.ANNOTATION_ID);
		String variantAnnotB = filterStatementsBy(mappedStatements, StatementField.ANNOTATION_CATEGORY, "variant").get(1).getValue(StatementField.ANNOTATION_ID);
		
		
		Set<String> refAnnots = new TreeSet<>(Arrays.asList(referenceVarAnnotation1, referenceVarAnnotation2));
		Set<String> varAnnots = new TreeSet<>(Arrays.asList(variantAnnotA, variantAnnotB));
		
		assertEquals(refAnnots.size(), 2);
		assertEquals(refAnnots, varAnnots);
		
	}


}
