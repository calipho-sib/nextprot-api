package com.nextprot.api.annotation.builder;

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
	protected IsoformAnnotation newAnnotation() {
		return new IsoformAnnotation();
	}

}
