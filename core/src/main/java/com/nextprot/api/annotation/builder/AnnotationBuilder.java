package com.nextprot.api.annotation.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.BioGenericObject;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationEvidenceProperty;
import org.nextprot.api.core.domain.annotation.AnnotationVariant;
import org.nextprot.api.core.utils.AnnotationUtils;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementField;

abstract class AnnotationBuilder<T extends Annotation> {

	protected static final Logger LOGGER = Logger.getLogger(AnnotationBuilder.class);

	private static AnnotationEvidenceProperty addPropertyIfPresent(String propertyValue, String propertyName) {
		if (propertyValue != null) {
			AnnotationEvidenceProperty prop = new AnnotationEvidenceProperty();
			prop.setPropertyName(propertyName);
			prop.setPropertyValue(propertyValue);
			return prop;
		}
		return null;
	}

	protected abstract T newAnnotation ();

	public List<T> buildProteoformIsoformAnnotations (String accession, List<Statement> subjects, List<Statement> proteoformStatements){
		
		List<T> annotations = new ArrayList<>();

		Map<String, List<Statement>> subjectsByAnnotationId = subjects.stream().collect(Collectors.groupingBy(rs -> rs.getValue(StatementField.ANNOTATION_ID)));

		Map<String, List<Statement>> impactStatementsBySubject = proteoformStatements.stream().collect(Collectors.groupingBy(r -> r.getValue(StatementField.SUBJECT_ANNOTATION_IDS)));

		impactStatementsBySubject.keySet().forEach(subjectComponentsIdentifiers -> {
			
			String[] subjectComponentsIdentifiersArray = subjectComponentsIdentifiers.split(",");
			Set<T> subjectVariants = new TreeSet<T>(new Comparator<T>(){
				@Override
				public int compare(T o1, T o2) {
					return o1.getAnnotationName().compareTo(o2.getAnnotationName());
				}
			});

			for(String subjectComponentIdentifier : subjectComponentsIdentifiersArray){

				List<Statement> subjectVariant = subjectsByAnnotationId.get(subjectComponentIdentifier);
				
				if((subjectVariant == null) || (subjectVariant.isEmpty())){
					throw new NextProtException("Not found any subject  identifier:" + subjectComponentIdentifier);
				}
				T variant = buildAnnotation(accession, subjectVariant);
				subjectVariants.add(variant);
			}

			// Impact annotations
			List<Statement> impactStatements = impactStatementsBySubject.get(subjectComponentsIdentifiers);
			List<T> impactAnnotations = buildAnnotationList(accession, impactStatements);
			impactAnnotations.stream().forEach(ia -> {
				
				String name = subjectVariants.stream().map(v -> v.getAnnotationName()).collect(Collectors.joining(" + ")).toString();
				
				ia.setSubjectName(name);
				ia.setSubjectComponents(Arrays.asList(subjectComponentsIdentifiersArray));
			});

			annotations.addAll(impactAnnotations);

		});
		
		return annotations;
		
	}

	protected static List<AnnotationEvidence> buildAnnotationEvidences(List<Statement> Statements) {
		return Statements.stream().map(s -> {
			AnnotationEvidence evidence = new AnnotationEvidence();
			evidence.setResourceAssociationType("evidence");
			evidence.setQualityQualifier(s.getValue(StatementField.EVIDENCE_QUALITY));

			AnnotationEvidenceProperty evidenceProperty = addPropertyIfPresent(s.getValue(StatementField.EVIDENCE_INTENSITY), "intensity");
			AnnotationEvidenceProperty expContextProperty = addPropertyIfPresent(s.getValue(StatementField.ANNOTATION_SUBJECT_SPECIES), "protein-origin");
			AnnotationEvidenceProperty sourceAccession =addPropertyIfPresent(s.getValue(StatementField.ANNOT_SOURCE_ACCESSION), "source-accession");

			//Set properties which are not null
			evidence.setProperties(
					Arrays.asList(evidenceProperty, expContextProperty, sourceAccession)
						.stream().filter(p -> p != null)
						.collect(Collectors.toList())
						);
			
			
			 evidence.setEvidenceCodeAC(s.getValue(StatementField.EVIDENCE_CODE));
			 
			 //TODO should this be hardcoded in the db for all ECOs or should we request a service at this stage?
			 String statementEvidenceCode = s.getValue(StatementField.EVIDENCE_CODE);
			 if(statementEvidenceCode != null && statementEvidenceCode.equals("ECO:0000006")){
				 evidence.setEvidenceCodeName("experimental evidence");
				 evidence.setEvidenceCodeOntology("evidence-code-ontology-cv");
			 }else if(statementEvidenceCode.equals("ECO:0000250")){
				 evidence.setEvidenceCodeName("sequence similarity");
				 evidence.setEvidenceCodeOntology("evidence-code-ontology-cv");
			 }else if(statementEvidenceCode != null){
				 throw new NextProtException("Not expecting " + statementEvidenceCode + " at this stage");
			 }
			 
			 evidence.setNote(s.getValue(StatementField.EVIDENCE_NOTE));
			
			//TODO create experimental contexts!

			return evidence;
		}).collect(Collectors.toList());

	}


	abstract void setIsoformName(T annotation, String statement);

	abstract void setIsoformTargeting(T annotation, Statement statement);

	protected void setVariantAttributes(T annotation, Statement variantStatement) {

		String original = variantStatement.getValue(StatementField.VARIANT_ORIGINAL_AMINO_ACID);
		String variant = variantStatement.getValue(StatementField.VARIANT_VARIATION_AMINO_ACID);
		AnnotationVariant annotationVariant = new AnnotationVariant(original, variant);
		annotation.setVariant(annotationVariant);

	}
	

	protected T buildAnnotation(String isoformName, List<Statement> flatStatements) {
		List<T> annotations = buildAnnotationList(isoformName, flatStatements);
		if(annotations.isEmpty() || annotations.size() > 1){
			throw new NextProtException("Expecting 1 annotation but found " + annotations.size() + " from " + flatStatements.size());
		}
		return annotations.get(0);
	}
	
	public List<T> buildAnnotationList(String isoformName, List<Statement> flatStatements) {

		List<T> annotations = new ArrayList<>();
		Map<String, List<Statement>> flatStatementsByAnnotationHash = flatStatements.stream().collect(Collectors.groupingBy(rs -> rs.getValue(StatementField.ANNOTATION_ID)));

		flatStatementsByAnnotationHash.entrySet().forEach(entry -> {

			T annotation = newAnnotation();
			
			List<Statement> statements = entry.getValue();
			
			annotation.setEvidences(buildAnnotationEvidences(statements));

			Statement statement = statements.get(0);

			annotation.setAnnotationHash(statement.getValue(StatementField.ANNOTATION_ID));
			annotation.setAnnotationName(statement.getValue(StatementField.ANNOTATION_NAME));

			AnnotationCategory category = AnnotationCategory.getDecamelizedAnnotationTypeName(StringUtils.camelToKebabCase(statement.getValue(StatementField.ANNOTATION_CATEGORY)));
			annotation.setCategory(category);

			if(category.equals(AnnotationCategory.VARIANT) || category.equals(AnnotationCategory.MUTAGENESIS)){
				setVariantAttributes(annotation, statement);
			}
			setIsoformTargeting(annotation, statement);

			setIsoformName(annotation, isoformName);
			annotation.setCvTermName(statement.getValue(StatementField.ANNOT_CV_TERM_NAME));

			annotation.setDescription(statement.getValue(StatementField.ANNOT_DESCRIPTION));
			annotation.setCvTermAccessionCode(statement.getValue(StatementField.ANNOT_CV_TERM_ACCESSION));
			// TODO this should be called terminology I guess! not setCVApiName
			annotation.setCvApiName(statement.getValue(StatementField.ANNOT_CV_TERM_TERMINOLOGY));

			annotation.setAnnotationHash(statement.getValue(StatementField.ANNOTATION_ID));
			annotation.setAnnotationName(statement.getValue(StatementField.ANNOTATION_NAME));
	
			//Check this with PAM (does it need to be a human readable stuff)
			annotation.setUniqueName(statement.getValue(StatementField.ANNOTATION_ID)); //Does it need a name?
			
			String boah = statement.getValue(StatementField.OBJECT_ANNOTATION_IDS);
			String boa = statement.getValue(StatementField.BIOLOGICAL_OBJECT_ACCESSION);
			String bot = statement.getValue(StatementField.BIOLOGICAL_OBJECT_TYPE);

			if ((boah != null) && (boah.length() > 0) || (boa != null && (boa.length() > 0))) {

				BioGenericObject bioObject = new BioGenericObject();
				bioObject.setAccession(boa); // In case of interactions
				bioObject.setType(bot);
				bioObject.setAnnotationHash(boah); // In case of phenotypes
				annotation.setBioObject(bioObject);

			}

			annotation.setQualityQualifier(AnnotationUtils.computeAnnotationQualityBasedOnEvidences(annotation.getEvidences()).name());

			annotations.add(annotation);
			

		});
		return annotations;
	}

}
