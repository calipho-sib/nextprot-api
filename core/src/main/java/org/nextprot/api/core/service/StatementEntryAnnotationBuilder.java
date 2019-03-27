package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.TargetIsoformSet;
import org.nextprot.commons.statements.TargetIsoformStatementPosition;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.nextprot.commons.statements.specs.CoreStatementField.TARGET_ISOFORMS;

public class StatementEntryAnnotationBuilder extends StatementAnnotationBuilder {

	protected StatementEntryAnnotationBuilder(TerminologyService terminologyService, PublicationService publicationService, MainNamesService mainNamesService, DbXrefService dbXrefService) {
		super(terminologyService, publicationService,mainNamesService, dbXrefService);
	}

	
	public static StatementEntryAnnotationBuilder newBuilder(TerminologyService terminologyService, PublicationService publicationService, MainNamesService mainNamesService, DbXrefService dbXrefService) {
		return new StatementEntryAnnotationBuilder(terminologyService, publicationService, mainNamesService, dbXrefService);
	}

	@Override
	void setIsoformName(Annotation annotation, String statement) {
		// nothing to do here
	}

	@Override
	void setIsoformTargeting(Annotation annotation, Statement statement) {
		
		List<AnnotationIsoformSpecificity> targetingIsoforms = new ArrayList<>();
		Set<TargetIsoformStatementPosition> tispSet = TargetIsoformSet.deSerializeFromJsonString(statement.getValue(TARGET_ISOFORMS));

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
