package com.nextprot.api.annotation.builder;

import java.util.ArrayList;
import java.util.List;

import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementField;
import org.nextprot.commons.statements.TargetIsoformStatement;
import org.nextprot.commons.statements.TargetIsoformStatementPosition;

import com.nextprot.api.annotation.builder.statement.TargetIsoformSerializer;

public class EntryAnnotationBuilder extends AnnotationBuilder<Annotation> {

	public static EntryAnnotationBuilder newBuilder() {
		return new EntryAnnotationBuilder();
	}

	@Override
	void setIsoformName(Annotation annotation, String statement) {
		// nothing to do here
	}

	@Override
	void setIsoformTargeting(Annotation annotation, Statement statement) {
		
		List<AnnotationIsoformSpecificity> targetingIsoforms = new ArrayList<AnnotationIsoformSpecificity>();
		TargetIsoformStatement tis = TargetIsoformSerializer.deSerializeFromJsonString(statement.getValue(StatementField.TARGET_ISOFORMS));

		for (String isoform : tis.keySet()) {
			
			TargetIsoformStatementPosition tisp = tis.get(isoform);

			AnnotationIsoformSpecificity ais = new AnnotationIsoformSpecificity();
			ais.setIsoformName(isoform);
			ais.setFirstPosition(tisp.getBegin());
			ais.setLastPosition(tisp.getEnd());
			ais.setSpecificity("UNKNOWN");//TODO check this with Pascale

			targetingIsoforms.add(ais);

		}
		
		annotation.addTargetingIsoforms(targetingIsoforms);

	}

	@Override
	protected Annotation newAnnotation() {
		return new Annotation();
	}

}
