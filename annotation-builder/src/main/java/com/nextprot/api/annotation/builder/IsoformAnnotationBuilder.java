package com.nextprot.api.annotation.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.annotation.IsoformAnnotation;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementField;

public class IsoformAnnotationBuilder extends AnnotationBuilder {

	public static List<IsoformAnnotation> buildProteoformIsoformAnnotations(String isoformAccession, List<Statement> subjects, List<Statement> proteoformStatements) {

		List<IsoformAnnotation> annotations = new ArrayList<>();
		Map<String, List<Statement>> subjectsByAnnotationId = subjects.stream().collect(Collectors.groupingBy(rs -> rs.getValue(StatementField.ANNOTATION_ID)));

		Map<String, List<Statement>> impactStatementsBySubject = proteoformStatements.stream().collect(Collectors.groupingBy(r -> r.getValue(StatementField.SUBJECT_ANNOTATION_IDS)));

		impactStatementsBySubject.keySet().forEach(subjectComponentsIdentifiers -> {
			
			String[] subjectComponentsIdentifiersArray = subjectComponentsIdentifiers.split(",");
			Set<IsoformAnnotation> subjectVariants = new TreeSet<IsoformAnnotation>(new Comparator<IsoformAnnotation>(){
				@Override
				public int compare(IsoformAnnotation o1, IsoformAnnotation o2) {
					return o1.getAnnotationUniqueName().compareTo(o2.getAnnotationUniqueName());
				}
			}); 

			for(String subjectComponentIdentifier : subjectComponentsIdentifiersArray){

				List<Statement> subjectVariant = subjectsByAnnotationId.get(subjectComponentIdentifier);
				
				if((subjectVariant == null) || (subjectVariant.isEmpty())){
					throw new NextProtException("Not found any subject  identifier:" + subjectComponentIdentifier);
				}
				IsoformAnnotation variant = IsoformAnnotationBuilder.buildAnnotation(isoformAccession, subjectVariant);
				subjectVariants.add(variant);
			}

			// Impact annotations
			List<Statement> impactStatements = impactStatementsBySubject.get(subjectComponentsIdentifiers);
			List<IsoformAnnotation> impactAnnotations = IsoformAnnotationBuilder.buildAnnotationList(isoformAccession, impactStatements);
			impactAnnotations.stream().forEach(ia -> {
				
				String name = subjectVariants.stream().map(v -> v.getAnnotationUniqueName()).collect(Collectors.joining(" + ")).toString();
				
				ia.setSubjectName(isoformAccession + " " + name);
				ia.setSubjectComponents(Arrays.asList(subjectComponentsIdentifiersArray));
			});

			annotations.addAll(impactAnnotations);

		});

		return annotations;

	}
}
