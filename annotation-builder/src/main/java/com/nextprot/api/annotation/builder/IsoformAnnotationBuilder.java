package com.nextprot.api.annotation.builder;

import java.util.List;

import org.nextprot.api.core.domain.annotation.IsoformAnnotation;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementField;

public class IsoformAnnotationBuilder extends AnnotationBuilder<IsoformAnnotation> {

	private IsoformAnnotationBuilder() {
	};

	public static IsoformAnnotationBuilder newBuilder() {
		return new IsoformAnnotationBuilder();
	}

	@Override
	void setPosition(IsoformAnnotation annotation, Statement statement) {
		
		String locBegin = statement.getValue(StatementField.LOCATION_BEGIN);
		try {
			Integer locationBegin = Integer.valueOf(locBegin);
			annotation.setLocationBegin(locationBegin);
		} catch (Exception e) {
			LOGGER.warn("Did not convert begin position " + locBegin);
		}

		String locEnd = statement.getValue(StatementField.LOCATION_END);
		try {
			Integer locationEnd = Integer.valueOf(locEnd);
			annotation.setLocationEnd(locationEnd);
		} catch (Exception e) {
			LOGGER.warn("Did not convert end position " + locEnd);
		}
		
	}

	@Override
	void setIsoformName(IsoformAnnotation annotation, String isoformName) {
		annotation.setIsoformName(isoformName);
	}

	@Override
	public List<IsoformAnnotation> buildProteoformIsoformAnnotations(String accession, List<Statement> subjects, List<Statement> proteoformStatements) {
		return super.buildProteoformIsoformAnnotations(accession, subjects, proteoformStatements, IsoformAnnotation.class);
	}

	@Override
	public IsoformAnnotation buildAnnotation(String isoformName, List<Statement> statements) {
		return super.buildAnnotation(isoformName, statements, IsoformAnnotation.class);
	}

	@Override
	public List<IsoformAnnotation> buildAnnotationList(String isoformName, List<Statement> statements) {
		return super.buildAnnotationList(isoformName, statements, IsoformAnnotation.class);
	}

}
