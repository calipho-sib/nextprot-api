package com.nextprot.api.annotation.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementField;
import org.nextprot.commons.statements.TargetIsoformStatementPosition;

import com.nextprot.api.annotation.builder.statement.TargetIsoformSerializer;

public class EntryAnnotationBuilder extends AnnotationBuilder<Annotation> {

	protected EntryAnnotationBuilder(TerminologyService terminologyService, PublicationService publicationService) {
		super(terminologyService, publicationService);
	}

	public static EntryAnnotationBuilder newBuilder(TerminologyService terminologyService, PublicationService publicationService) {
		return new EntryAnnotationBuilder(terminologyService, publicationService);
	}

	@Override
	void setIsoformName(Annotation annotation, String statement) {
		// nothing to do here
	}

	@Override
	void setIsoformTargeting(Annotation annotation, Statement statement) {
		
		List<AnnotationIsoformSpecificity> targetingIsoforms = new ArrayList<AnnotationIsoformSpecificity>();
		Set<TargetIsoformStatementPosition> tispSet = TargetIsoformSerializer.deSerializeFromJsonString(statement.getValue(StatementField.TARGET_ISOFORMS));

		for (TargetIsoformStatementPosition tisp : tispSet) {
			
			AnnotationIsoformSpecificity ais = new AnnotationIsoformSpecificity();
			ais.setIsoformName(tisp.getIsoformAccession()); //TODO accession or name???
			ais.setFirstPosition(tisp.getBegin());
			ais.setLastPosition(tisp.getEnd());
			ais.setSpecificity(tisp.getSpecificity());
			ais.setName(tisp.getName());
			
			targetingIsoforms.add(ais);

		}
		
		annotation.addTargetingIsoforms(targetingIsoforms);

	}

	@Override
	protected Annotation newAnnotation() {
		return new Annotation();
	}

}
