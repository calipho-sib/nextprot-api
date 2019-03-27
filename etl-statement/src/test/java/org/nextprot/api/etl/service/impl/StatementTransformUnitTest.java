package org.nextprot.api.etl.service.impl;

import org.junit.Test;
import org.nextprot.api.etl.NextProtSource;
import org.nextprot.api.etl.service.impl.StatementETLServiceImpl.ReportBuilder;
import org.nextprot.api.etl.statement.StatementETLBaseUnitTest;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.specs.StatementField;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.nextprot.commons.statements.specs.CoreStatementField.*;

public class StatementTransformUnitTest extends StatementETLBaseUnitTest {

	private List<Statement> filterStatementsBy(Collection<Statement> statements, StatementField field, String value){
		return statements.stream().filter(s -> value.equalsIgnoreCase(s.getValue(field))).collect(Collectors.toList());
	}

	// TODO: try to understand why it failed
	@Test
	public void rawStatementsShouldBeWellConvertedToMappedStatements() throws IOException {

		StatementsExtractorLocalMockImpl sle = new StatementsExtractorLocalMockImpl();
		Collection<Statement> rawStatements = sle.getStatementsFromJsonFile(NextProtSource.BioEditor, null, "msh2-multiple-mutant");

		Collection<Statement> mappedStatements = statementETLServiceMocked.transformStatements(NextProtSource.BioEditor, rawStatements, new ReportBuilder());

		int rawStatementsCount = rawStatements.stream().map(s -> s.getValue(STATEMENT_ID)).distinct().collect(Collectors.toList()).size();
		int mappedStatementsCount = rawStatements.stream().map(s -> s.getValue(STATEMENT_ID)).distinct().collect(Collectors.toList()).size();
		int annotationsCount = mappedStatements.stream().map(s -> s.getValue(ANNOTATION_ID)).distinct().collect(Collectors.toList()).size();

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


}
