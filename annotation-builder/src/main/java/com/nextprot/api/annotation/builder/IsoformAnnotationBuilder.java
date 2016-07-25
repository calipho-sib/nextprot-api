package com.nextprot.api.annotation.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.BioGenericObject;
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
				IsoformAnnotation variant = buildAnnotation(isoformAccession, subjectVariant);
				subjectVariants.add(variant);
			}

			// Impact annotations
			List<Statement> impactStatements = impactStatementsBySubject.get(subjectComponentsIdentifiers);
			List<IsoformAnnotation> impactAnnotations = buildAnnotationList(isoformAccession, impactStatements);
			impactAnnotations.stream().forEach(ia -> {
				
				String name = subjectVariants.stream().map(v -> v.getAnnotationUniqueName()).collect(Collectors.joining(" + ")).toString();
				
				ia.setSubjectName(isoformAccession + " " + name);
				ia.setSubjectComponents(Arrays.asList(subjectComponentsIdentifiersArray));
			});

			annotations.addAll(impactAnnotations);

		});

		return annotations;

	}
	
	static IsoformAnnotation buildAnnotation(String isoformName, List<Statement> flatStatements) {
		List<IsoformAnnotation> annotations = buildAnnotationList(isoformName, flatStatements);
		if(annotations.isEmpty() || annotations.size() > 1){
			throw new NextProtException("Expecting 1 annotation but found " + annotations.size() + " from " + flatStatements.size());
		}
		return annotations.get(0);
	}
	
	public static List<IsoformAnnotation> buildAnnotationList(String isoformName, List<Statement> flatStatements) {

		List<IsoformAnnotation> annotations = new ArrayList<>();
		Map<String, List<Statement>> flatStatementsByAnnotationHash = flatStatements.stream().collect(Collectors.groupingBy(rs -> rs.getValue(StatementField.ANNOTATION_ID)));

		flatStatementsByAnnotationHash.keySet().forEach(annotationHash -> {

			IsoformAnnotation isoAnnotation = new IsoformAnnotation();
			List<Statement> statements = flatStatementsByAnnotationHash.get(annotationHash);

			isoAnnotation.setEvidences(buildAnnotationEvidences(statements));

			Statement statement = statements.get(0);

			AnnotationCategory category = AnnotationCategory.getDecamelizedAnnotationTypeName(StringUtils.camelToKebabCase(statement.getValue(StatementField.ANNOTATION_CATEGORY)));
			isoAnnotation.setCategory(category);

			if (category.equals(AnnotationCategory.VARIANT))
				setVariantAttributes(isoAnnotation, statement);

			isoAnnotation.setIsoformName(isoformName);
			isoAnnotation.setCvTermName(statement.getValue(StatementField.ANNOT_CV_TERM_NAME));

			isoAnnotation.setDescription(statement.getValue(StatementField.ANNOT_DESCRIPTION));
			isoAnnotation.setCvTermAccessionCode(statement.getValue(StatementField.ANNOT_CV_TERM_ACCESSION));
			// TODO this should be called terminology I guess! not setCVApiName
			isoAnnotation.setCvApiName(statement.getValue(StatementField.ANNOT_CV_TERM_TERMINOLOGY));

			isoAnnotation.setAnnotationHash(statement.getValue(StatementField.ANNOTATION_ID));
			isoAnnotation.setAnnotationUniqueName(statement.getValue(StatementField.ANNOTATION_NAME));
			
			String boah = statement.getValue(StatementField.OBJECT_ANNOTATION_IDS);
			String boa = statement.getValue(StatementField.BIOLOGICAL_OBJECT_ACCESSION);
			String bot = statement.getValue(StatementField.BIOLOGICAL_OBJECT_TYPE);

			if ((boah != null) && (boah.length() > 0) || (boa != null && (boa.length() > 0))) {

				BioGenericObject bioObject = new BioGenericObject();
				bioObject.setAccession(boa); // In case of interactions
				bioObject.setType(bot);
				bioObject.setAnnotationHash(boah); // In case of phenotypes
				isoAnnotation.setBioObject(bioObject);

			}

			annotations.add(isoAnnotation);

		});
		return annotations;
	}

}
