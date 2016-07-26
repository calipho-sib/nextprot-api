package com.nextprot.api.annotation.builder;

import java.util.List;

import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementField;

public class EntryAnnotationBuilder extends AnnotationBuilder<Annotation>{

	public static EntryAnnotationBuilder newBuilder() {
		return new EntryAnnotationBuilder();
	}

	@Override
	void setIsoformName(Annotation annotation, String statement) {
		// nothing to do here
	}

	@Override
	void setPosition(Annotation annotation, Statement statement) {
		System.err.println(statement.getValue(StatementField.TARGET_ISOFORMS));
		
	}

	@Override
	public List<Annotation> buildProteoformIsoformAnnotations(String accession, List<Statement> subjects, List<Statement> proteoformStatements) {
		return super.buildProteoformIsoformAnnotations(accession, subjects, proteoformStatements, Annotation.class);
	}

	@Override
	public Annotation buildAnnotation(String isoformName, List<Statement> statements) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Annotation> buildAnnotationList(String isoformName, List<Statement> flatStatements) {
		// TODO Auto-generated method stub
		return null;
	}


}
