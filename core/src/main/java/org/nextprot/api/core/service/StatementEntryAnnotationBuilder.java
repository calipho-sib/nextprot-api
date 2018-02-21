package org.nextprot.api.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementField;
import org.nextprot.commons.statements.TargetIsoformSet;
import org.nextprot.commons.statements.TargetIsoformStatementPosition;

public class StatementEntryAnnotationBuilder extends StatementAnnotationBuilder<Annotation> {

	protected StatementEntryAnnotationBuilder(TerminologyService terminologyService, PublicationService publicationService, MainNamesService mainNamesService) {
		super(terminologyService, publicationService,mainNamesService);
	}

	
	public static StatementEntryAnnotationBuilder newBuilder(TerminologyService terminologyService, PublicationService publicationService, MainNamesService mainNamesService) {
		return new StatementEntryAnnotationBuilder(terminologyService, publicationService, mainNamesService);
	}

	@Override
	void setIsoformName(Annotation annotation, String statement) {
		// nothing to do here
	}

	@Override
	void setIsoformTargeting(Annotation annotation, Statement statement) {
		
		List<AnnotationIsoformSpecificity> targetingIsoforms = new ArrayList<AnnotationIsoformSpecificity>();
		Set<TargetIsoformStatementPosition> tispSet = TargetIsoformSet.deSerializeFromJsonString(statement.getValue(StatementField.TARGET_ISOFORMS));

		for (TargetIsoformStatementPosition tisp : tispSet) {
			
			AnnotationIsoformSpecificity ais = new AnnotationIsoformSpecificity();
			String isoAc=tisp.getIsoformAccession();
			ais.setIsoformAccession(isoAc);
			ais.setFirstPosition(tisp.getBegin());
			ais.setLastPosition(tisp.getEnd());
			ais.setSpecificity(tisp.getSpecificity());
			ais.setName(tisp.getName());
			
			targetingIsoforms.add(ais);

		}
		
		annotation.addTargetingIsoforms(targetingIsoforms);

	}

	@Override
	public Annotation get() {
		return new Annotation();
	}

}
