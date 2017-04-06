package com.nextprot.api.annotation.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.constants.IdentifierOffset;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.BioObject;
import org.nextprot.api.core.domain.BioObject.BioType;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationEvidenceProperty;
import org.nextprot.api.core.domain.annotation.AnnotationVariant;
import org.nextprot.api.core.service.MainNamesService;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.utils.annot.AnnotationUtils;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementField;

import com.google.common.base.Supplier;

abstract class AnnotationBuilder<T extends Annotation> implements Supplier<T> {

	protected static final Logger LOGGER = Logger.getLogger(AnnotationBuilder.class);

	protected TerminologyService terminologyService = null;
	protected PublicationService publicationService = null;
	protected MainNamesService mainNamesService = null;

	/**
	 * Flag that indicates that the build should throw an Exception at the first error or just log silently
	 */
	static boolean STRICT = false;

	
	private final Set<AnnotationCategory> ANNOT_CATEGORIES_WITHOUT_EVIDENCES = new HashSet<>(Arrays.asList(AnnotationCategory.MAMMALIAN_PHENOTYPE, AnnotationCategory.PROTEIN_PROPERTY));
	
	protected AnnotationBuilder(TerminologyService terminologyService, PublicationService publicationService, MainNamesService mainNamesService){
		this.terminologyService = terminologyService;
		this.publicationService = publicationService;
		this.mainNamesService = mainNamesService;
	}
	
	
	private static AnnotationEvidenceProperty addPropertyIfPresent(String propertyValue, String propertyName) {
		if (propertyValue != null) {
			AnnotationEvidenceProperty prop = new AnnotationEvidenceProperty();
			prop.setPropertyName(propertyName);
			prop.setPropertyValue(propertyValue);
			return prop;
		}
		return null;
	}

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
				ia.setSubjectComponents(Arrays.asList(subjectComponentsIdentifiersArray));
			});

			annotations.addAll(impactAnnotations);

		});
		
		return annotations;
		
	}

	protected List<AnnotationEvidence> buildAnnotationEvidences(List<Statement> Statements) {

		//Ensures there is no repeated evidence!
		Set<AnnotationEvidence> evidencesSet = Statements.stream().map(s -> {
			AnnotationEvidence evidence = new AnnotationEvidence();
			
			evidence.setResourceType("database");//TODO to be checked with Amos and Lydie
			
			evidence.setResourceAssociationType("evidence");
			evidence.setQualityQualifier(s.getValue(StatementField.EVIDENCE_QUALITY));
			
			
			setEvidenceResourceId(evidence, s);

			AnnotationEvidenceProperty evidenceProperty = addPropertyIfPresent(s.getValue(StatementField.EVIDENCE_INTENSITY), "intensity");
			AnnotationEvidenceProperty expContextSubjectProteinOrigin = addPropertyIfPresent(s.getValue(StatementField.ANNOTATION_SUBJECT_SPECIES), "subject-protein-origin");
			AnnotationEvidenceProperty expContextObjectProteinOrigin = addPropertyIfPresent(s.getValue(StatementField.ANNOTATION_OBJECT_SPECIES), "object-protein-origin");

			//Set properties which are not null
			evidence.setProperties(
					Arrays.asList(evidenceProperty, expContextSubjectProteinOrigin, expContextObjectProteinOrigin)
						.stream().filter(p -> p != null)
						.collect(Collectors.toList())
						);
			
			
			 
			 String statementEvidenceCode = s.getValue(StatementField.EVIDENCE_CODE);
			 evidence.setEvidenceCodeAC(statementEvidenceCode);
			 evidence.setAssignedBy(s.getValue(StatementField.ASSIGNED_BY));
			 evidence.setAssignmentMethod(s.getValue(StatementField.ASSIGMENT_METHOD));
			 evidence.setResourceType(s.getValue(StatementField.RESOURCE_TYPE));
			 evidence.setEvidenceCodeOntology("evidence-code-ontology-cv");
			 evidence.setNegativeEvidence("true".equalsIgnoreCase(s.getValue(StatementField.IS_NEGATIVE)));
			 
			 if(statementEvidenceCode != null){
				 CvTerm term = terminologyService.findCvTermByAccession(statementEvidenceCode);
				 if(term != null){
					 evidence.setEvidenceCodeName(term.getName());
				 }else {
					 throw new NextProtException("Not found " + statementEvidenceCode + " in the database");
				 }
			 }
			 
			 evidence.setNote(s.getValue(StatementField.EVIDENCE_NOTE));
			
			//TODO create experimental contexts!
			 
			return evidence;
		}).collect(Collectors.toSet());
		

		//Ensures there is no repeated evidence!
		evidencesSet.forEach(e -> {
			long generatedEvidenceId = IdentifierOffset.EVIDENCE_ID_COUNTER_FOR_STATEMENTS.incrementAndGet();
			e.setEvidenceId(generatedEvidenceId);
		});
		
		List<AnnotationEvidence> evidencesFiltered = evidencesSet.stream().filter(e -> e.getResourceId() != -2).collect(Collectors.toList());
		if(evidencesFiltered.size() < evidencesSet.size()){
			int total = evidencesSet.size();
			int removed = total - evidencesFiltered.size();
			LOGGER.debug("Removed " + removed + " evidence because no resource id from a total of " + total);
		}
		
		return new ArrayList<>(evidencesFiltered);

	}


	abstract void setIsoformName(T annotation, String statement);

	abstract void setIsoformTargeting(T annotation, Statement statement);

	protected void setVariantAttributes(T annotation, Statement variantStatement) {

		String original = variantStatement.getValue(StatementField.VARIANT_ORIGINAL_AMINO_ACID);
		String variant = variantStatement.getValue(StatementField.VARIANT_VARIATION_AMINO_ACID);
		AnnotationVariant annotationVariant = new AnnotationVariant(original, variant);
		annotation.setVariant(annotationVariant);

	}
	


	void setEvidenceResourceId(AnnotationEvidence evidence, Statement statement) {

		String referenceDB = statement.getValue(StatementField.REFERENCE_DATABASE);
		String referenceAC = statement.getValue(StatementField.REFERENCE_ACCESSION);
		
		//If it's a publication
		if("PubMed".equalsIgnoreCase(referenceDB)){
			if(referenceDB != null){
				String pubmedId = referenceAC;
				Publication publication = publicationService.findPublicationByDatabaseAndAccession("PubMed", pubmedId);
				if (publication == null) {
					//Set -1 if not exists. Should never be the case 
					evidence.setResourceId((Long) throwErrorOrReturn("can 't find publication " + pubmedId, -1L));
				}
				else {
					evidence.setResourceId(publication.getPublicationId());
				}
			}
			
		} else if("DOI".equalsIgnoreCase(referenceDB)){ 
			
			//Should work with DOI: 10.1038/npjgenmed.2016.1
			//See https://issues.isb-sib.ch/browse/NEXTPROT-1369
			Publication publication = publicationService.findPublicationByDatabaseAndAccession("PubMed", referenceAC);
			if (publication == null) {
				//Set -1 if not exists. Should never be the case 
				evidence.setResourceId((Long) throwErrorOrReturn("can 't find publication with DOI " + referenceAC, -1L));
			}
			else {
				evidence.setResourceId(publication.getPublicationId());
			}
			
			
		} else {
			
			evidence.setResourceId(-2);
			//evidence.setResourceId(dbXrefService.findDbXrefIdByDatabaseAndAccession(referenceDB, referenceAC));
		}
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

			T annotation = get();
			
			List<Statement> statements = entry.getValue();
			

			Statement firstStatement = statements.get(0);

			annotation.setAnnotationHash(firstStatement.getValue(StatementField.ANNOTATION_ID));
			//annotation.setAnnotationName(firstStatement.getValue(StatementField.ANNOTATION_NAME));

			AnnotationCategory category = AnnotationCategory.getDecamelizedAnnotationTypeName(StringUtils.camelToKebabCase(firstStatement.getValue(StatementField.ANNOTATION_CATEGORY)));
			annotation.setAnnotationCategory(category);

			if(category.equals(AnnotationCategory.VARIANT) || category.equals(AnnotationCategory.MUTAGENESIS)){
				setVariantAttributes(annotation, firstStatement);
			}
			setIsoformTargeting(annotation, firstStatement);

			setIsoformName(annotation, isoformName);

			annotation.setDescription(firstStatement.getValue(StatementField.ANNOT_DESCRIPTION));

			String cvTermAccession = firstStatement.getValue(StatementField.ANNOT_CV_TERM_ACCESSION);

			//Set the evidences if not Mammalian phenotype or Protein Property https://issues.isb-sib.ch/browse/BIOEDITOR-466
			if(!ANNOT_CATEGORIES_WITHOUT_EVIDENCES.contains(category)){
				annotation.setEvidences(buildAnnotationEvidences(statements));
				
				//TODO Remove this when you are able to do XREFs
				if(((annotation.getEvidences() == null) || ((annotation.getEvidences().isEmpty()))) && (category.equals(AnnotationCategory.VARIANT) || category.equals(AnnotationCategory.MUTAGENESIS))){
					annotation.setQualityQualifier("GOLD");//All variants from BED are GOLD, and this is a special case when we don't have evidences for VDs.
				}else {
					annotation.setQualityQualifier(AnnotationUtils.computeAnnotationQualityBasedOnEvidences(annotation.getEvidences()).name());
				}
				
			}else {
				
				//Case of Protein propert and mammalian phenotypes
				annotation.setEvidences(new ArrayList<AnnotationEvidence>());
				
				boolean foundGold = statements.stream().anyMatch(s -> s.getValue(StatementField.EVIDENCE_QUALITY).equalsIgnoreCase("GOLD"));
				if(foundGold){
					annotation.setQualityQualifier("GOLD");
				}else {
					annotation.setQualityQualifier("SILVER");
				}
			}

			if(cvTermAccession != null && !cvTermAccession.isEmpty()){

				annotation.setCvTermAccessionCode(cvTermAccession);

				CvTerm cvTerm = terminologyService.findCvTermByAccession(cvTermAccession);
				if(cvTerm != null){
					annotation.setCvTermName(cvTerm.getName());
					annotation.setCvApiName(cvTerm.getOntology());
					annotation.setCvTermDescription(cvTerm.getDescription());

					if(category.equals(AnnotationCategory.PROTEIN_PROPERTY)){
						//according to https://issues.isb-sib.ch/browse/BIOEDITOR-466
						annotation.setDescription(cvTerm.getDescription());
					}else if(category.equals(AnnotationCategory.MAMMALIAN_PHENOTYPE)){
						annotation.setDescription("Relative to modification-effect annotations");
					}
					
				}else {
					LOGGER.error("cv term was expected to be found " + cvTermAccession);
					annotation.setCvTermName(firstStatement.getValue(StatementField.ANNOT_CV_TERM_NAME));
					annotation.setCvApiName(firstStatement.getValue(StatementField.ANNOT_CV_TERM_TERMINOLOGY));
				}
							
			}

			annotation.setAnnotationHash(firstStatement.getValue(StatementField.ANNOTATION_ID));
			annotation.setAnnotationName(firstStatement.getValue(StatementField.ANNOTATION_NAME));
	
			//Check this with PAM (does it need to be a human readable stuff)
			annotation.setUniqueName(firstStatement.getValue(StatementField.ANNOTATION_ID)); //Does it need a name?
			
			String bioObjectAnnotationHash = firstStatement.getValue(StatementField.OBJECT_ANNOTATION_IDS);
			String bioObjectAccession = firstStatement.getValue(StatementField.BIOLOGICAL_OBJECT_ACCESSION);
			String bot = firstStatement.getValue(StatementField.BIOLOGICAL_OBJECT_TYPE);

			if ((bioObjectAnnotationHash != null) && (bioObjectAnnotationHash.length() > 0) || (bioObjectAccession != null && (bioObjectAccession.length() > 0))) {

				BioObject bioObject = null;

				if (AnnotationCategory.BINARY_INTERACTION.equals(annotation.getAPICategory())) {
					if(bioObjectAccession.startsWith("NX_") && BioType.PROTEIN.name().equalsIgnoreCase(bot)){
						// note that if we handle BioType.PROTEIN_ISOFORM in the future, we should
						// add the property isoformName as well, see how it's done in BinaryInteraction2Annotation.newBioObject()
						bioObject = BioObject.internal(BioType.PROTEIN);
						bioObject.setAccession(bioObjectAccession);						
						bioObject.putPropertyNameValue("geneName", firstStatement.getValue(StatementField.BIOLOGICAL_OBJECT_NAME));
						String proteinName = (String)mainNamesService.findIsoformOrEntryMainName().get(bioObjectAccession).getName();
						bioObject.putPropertyNameValue("proteinName", proteinName);
						bioObject.putPropertyNameValue("url", "https://www.nextprot.org/entry/" + bioObjectAccession + "/interactions");
						
					}else {
						throw new NextProtException("Binary Interaction only expects to be a nextprot entry NX_ and found " + bioObjectAccession + " with type " + bot);
					}
					
				}else if (AnnotationCategory.PHENOTYPIC_VARIATION.equals(annotation.getAPICategory())) {
						bioObject = BioObject.internal(BioType.ENTRY_ANNOTATION);
						bioObject.setAnnotationHash(bioObjectAnnotationHash);
				}else {
					throw new NextProtException("Category not expected for bioobject " + annotation.getAPICategory());
				}

				annotation.setBioObject(bioObject);
			}

			annotations.add(annotation);
			
			

		});
		return annotations;
	}
	
	
	
	
	private Object throwErrorOrReturn(String message, Object returnObject){

		LOGGER.error(message);
		if(STRICT){
			throw new NextProtException(message);
		}else return returnObject;

	}

}
