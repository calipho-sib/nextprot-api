package org.nextprot.api.core.service;

import com.google.common.base.Supplier;
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
import org.nextprot.api.core.service.annotation.AnnotationUtils;
import org.nextprot.api.core.service.impl.DbXrefServiceImpl;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementField;

import java.util.*;
import java.util.stream.Collectors;

abstract class StatementAnnotationBuilder implements Supplier<Annotation> {

	protected static final Logger LOGGER = Logger.getLogger(StatementAnnotationBuilder.class);

	protected TerminologyService terminologyService;
	protected PublicationService publicationService;
	protected MainNamesService mainNamesService;
	protected DbXrefService dbXrefService;

	private final Set<AnnotationCategory> ANNOT_CATEGORIES_WITHOUT_EVIDENCES = new HashSet<>(Arrays.asList(AnnotationCategory.MAMMALIAN_PHENOTYPE, AnnotationCategory.PROTEIN_PROPERTY));
	
	protected StatementAnnotationBuilder(TerminologyService terminologyService, PublicationService publicationService, MainNamesService mainNamesService, DbXrefService dbXrefService){
		this.terminologyService = terminologyService;
		this.publicationService = publicationService;
		this.mainNamesService = mainNamesService;
		this.dbXrefService= dbXrefService;
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

	public List<Annotation> buildProteoformIsoformAnnotations (String accession, List<Statement> subjects, List<Statement> proteoformStatements){
		
		List<Annotation> annotations = new ArrayList<>();

		Map<String, List<Statement>> subjectsByAnnotationId = subjects.stream().collect(Collectors.groupingBy(rs -> rs.getValue(StatementField.ANNOTATION_ID)));

		Map<String, List<Statement>> impactStatementsBySubject = proteoformStatements.stream().collect(Collectors.groupingBy(r -> r.getValue(StatementField.SUBJECT_ANNOTATION_IDS)));

		impactStatementsBySubject.keySet().forEach(subjectComponentsIdentifiers -> {
			
			String[] subjectComponentsIdentifiersArray = subjectComponentsIdentifiers.split(",");
			Set<Annotation> subjectVariants = new TreeSet<>(Comparator.comparing(Annotation::getAnnotationName));

			for(String subjectComponentIdentifier : subjectComponentsIdentifiersArray){

				List<Statement> subjectVariant = subjectsByAnnotationId.get(subjectComponentIdentifier);
				
				if((subjectVariant == null) || (subjectVariant.isEmpty())){
					throw new NextProtException("Not found any subject  identifier:" + subjectComponentIdentifier);
				}
                Annotation variant = buildAnnotation(accession, subjectVariant);
				subjectVariants.add(variant);
			}

			// Impact annotations
			List<Statement> impactStatements = impactStatementsBySubject.get(subjectComponentsIdentifiers);
			List<Annotation> impactAnnotations = buildAnnotationList(accession, impactStatements);
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
		Set<AnnotationEvidence> evidencesSet = Statements.stream()
                .map(s -> {
                    AnnotationEvidence evidence = new AnnotationEvidence();

                    evidence.setResourceType(s.getValue(StatementField.RESOURCE_TYPE));
                    evidence.setResourceAssociationType("evidence");
                    evidence.setQualityQualifier(s.getValue(StatementField.EVIDENCE_QUALITY));

                    setResourceId(s, evidence);

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
                     evidence.setEvidenceCodeOntology("evidence-code-ontology-cv");
                     evidence.setNegativeEvidence("true".equalsIgnoreCase(s.getValue(StatementField.IS_NEGATIVE)));

                     if(statementEvidenceCode != null){
                         CvTerm term = terminologyService.findCvTermByAccessionOrThrowRuntimeException(statementEvidenceCode);
                         evidence.setEvidenceCodeName(term.getName());
                     }

                     evidence.setNote(s.getValue(StatementField.EVIDENCE_NOTE));

                    //TODO create experimental contexts!

                    return evidence;
                })
                .collect(Collectors.toSet());
		

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

    private void setResourceId(Statement s, AnnotationEvidence evidence) {

        String resourceType = evidence.getResourceType();

        if (resourceType != null) {
            if (resourceType.equals("publication")) {
                evidence.setResourceId(findPublicationId(s));
            }
            else if (resourceType.equals("database")) {
                evidence.setResourceId(findXrefId(s));
                evidence.setResourceAccession(s.getValue(StatementField.REFERENCE_ACCESSION));
                evidence.setResourceDb(s.getValue(StatementField.REFERENCE_DATABASE));
            }
            else {
                throw new NextProtException("resource type "+ resourceType + " not supported");
            }
        }
        else {
            throw new NextProtException("resource type undefined");
        }
    }

    abstract void setIsoformName(Annotation annotation, String statement);

	abstract void setIsoformTargeting(Annotation annotation, Statement statement);

	protected void setVariantAttributes(Annotation annotation, Statement variantStatement) {

		String original = variantStatement.getValue(StatementField.VARIANT_ORIGINAL_AMINO_ACID);
		String variant = variantStatement.getValue(StatementField.VARIANT_VARIATION_AMINO_ACID);
		AnnotationVariant annotationVariant = new AnnotationVariant(original, variant.equals("-") ? "" : variant);
		annotation.setVariant(annotationVariant);

	}

    long findPublicationId(Statement statement) {

		String referenceDB = statement.getValue(StatementField.REFERENCE_DATABASE);
		String referenceAC = statement.getValue(StatementField.REFERENCE_ACCESSION);

        Publication publication = publicationService.findPublicationByDatabaseAndAccession(referenceDB, referenceAC);
		if (publication == null) {
            String message = "can 't find publication db:" + referenceDB + " acc:" + referenceAC;

            LOGGER.error(message);

            throw new NextProtException(message);
		}

        return publication.getPublicationId();
	}

    long findXrefId(Statement statement) {

        String referenceDB = statement.getValue(StatementField.REFERENCE_DATABASE);
        String referenceAC = statement.getValue(StatementField.REFERENCE_ACCESSION);

        try {
            return dbXrefService.findXrefId(referenceDB, referenceAC);
        } catch (DbXrefServiceImpl.MissingCvDatabaseException e) {

            LOGGER.error(e.getMessage());
            throw new NextProtException(e.getMessage());
        }
    }

	protected Annotation buildAnnotation(String isoformName, List<Statement> flatStatements) {
		List<Annotation> annotations = buildAnnotationList(isoformName, flatStatements);
		if(annotations.isEmpty() || annotations.size() > 1){
			throw new NextProtException("Expecting 1 annotation but found " + annotations.size() + " from " + flatStatements.size());
		}
		return annotations.get(0);
	}
	
	public List<Annotation> buildAnnotationList(String isoformName, List<Statement> flatStatements) {

		List<Annotation> annotations = new ArrayList<>();
		Map<String, List<Statement>> flatStatementsByAnnotationHash = flatStatements.stream().collect(Collectors.groupingBy(rs -> rs.getValue(StatementField.ANNOTATION_ID)));

		flatStatementsByAnnotationHash.forEach((key, statements) -> {

            Annotation annotation = get();

            Statement firstStatement = statements.get(0);

            annotation.setAnnotationHash(firstStatement.getValue(StatementField.ANNOTATION_ID));
            //annotation.setAnnotationName(firstStatement.getValue(StatementField.ANNOTATION_NAME));

            AnnotationCategory category = AnnotationCategory.getDecamelizedAnnotationTypeName(StringUtils.camelToKebabCase(firstStatement.getValue(StatementField.ANNOTATION_CATEGORY)));
            annotation.setAnnotationCategory(category);

            if (category.equals(AnnotationCategory.VARIANT) || category.equals(AnnotationCategory.MUTAGENESIS)) {
                setVariantAttributes(annotation, firstStatement);
            }
            setIsoformTargeting(annotation, firstStatement);

            setIsoformName(annotation, isoformName);

            annotation.setDescription(firstStatement.getValue(StatementField.ANNOT_DESCRIPTION));

            String cvTermAccession = firstStatement.getValue(StatementField.ANNOT_CV_TERM_ACCESSION);

            //Set the evidences if not Mammalian phenotype or Protein Property https://issues.isb-sib.ch/browse/BIOEDITOR-466
            if (!ANNOT_CATEGORIES_WITHOUT_EVIDENCES.contains(category)) {
                annotation.setEvidences(buildAnnotationEvidences(statements));

                //TODO Remove this when you are able to do XREFs
                if (((annotation.getEvidences() == null) || ((annotation.getEvidences().isEmpty()))) && (category.equals(AnnotationCategory.VARIANT) || category.equals(AnnotationCategory.MUTAGENESIS))) {
                    annotation.setQualityQualifier("GOLD");//All variants from BED are GOLD, and this is a special case when we don't have evidences for VDs.
                } else {
                    annotation.setQualityQualifier(AnnotationUtils.computeAnnotationQualityBasedOnEvidences(annotation.getEvidences()).name());
                }

            } else {

                //Case of Protein propert and mammalian phenotypes
                annotation.setEvidences(new ArrayList<>());

                boolean foundGold = statements.stream().anyMatch(s -> s.getValue(StatementField.EVIDENCE_QUALITY).equalsIgnoreCase("GOLD"));
                if (foundGold) {
                    annotation.setQualityQualifier("GOLD");
                } else {
                    annotation.setQualityQualifier("SILVER");
                }
            }

            if (cvTermAccession != null && !cvTermAccession.isEmpty()) {

                annotation.setCvTermAccessionCode(cvTermAccession);

                CvTerm cvTerm = terminologyService.findCvTermByAccession(cvTermAccession);
                if (cvTerm != null) {
                    annotation.setCvTermName(cvTerm.getName());
                    annotation.setCvApiName(cvTerm.getOntology());
                    annotation.setCvTermDescription(cvTerm.getDescription());

                    if (category.equals(AnnotationCategory.PROTEIN_PROPERTY)) {
                        //according to https://issues.isb-sib.ch/browse/BIOEDITOR-466
                        annotation.setDescription(cvTerm.getDescription());
                    } else if (category.equals(AnnotationCategory.MAMMALIAN_PHENOTYPE)) {
                        annotation.setDescription("Relative to modification-effect annotations");
                    }

                } else {
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

            if ( (bioObjectAnnotationHash != null && !bioObjectAnnotationHash.isEmpty()) ||
                 (bioObjectAccession != null && !bioObjectAccession.isEmpty())) {

                annotation.setBioObject(newBioObject(firstStatement, annotation.getAPICategory()));
            }

            annotations.add(annotation);
        });

		return annotations;
	}

	private BioObject newBioObject(Statement firstStatement, AnnotationCategory annotationCategory) {

        String bioObjectAnnotationHash = firstStatement.getValue(StatementField.OBJECT_ANNOTATION_IDS);
        String bioObjectAccession = firstStatement.getValue(StatementField.BIOLOGICAL_OBJECT_ACCESSION);
        String bioObjectType = firstStatement.getValue(StatementField.BIOLOGICAL_OBJECT_TYPE);
        String bioObjectName = firstStatement.getValue(StatementField.BIOLOGICAL_OBJECT_NAME);

        BioObject bioObject;

        if (AnnotationCategory.BINARY_INTERACTION.equals(annotationCategory)) {

            if (!bioObjectAccession.startsWith("NX_")) {

                throw new NextProtException("Binary Interaction only expects to be a nextprot entry starting with NX_ but found " + bioObjectAccession + " with type " + bioObjectType);
            }

            if (BioType.PROTEIN.name().equalsIgnoreCase(bioObjectType)) {

                bioObject = BioObject.internal(BioType.PROTEIN);

                bioObject.putPropertyNameValue("proteinName", mainNamesService.findIsoformOrEntryMainName(bioObjectAccession)
                        .orElseThrow(() -> new NextProtException("Cannot create a binary interaction from statement " + firstStatement + ": unknown protein accession " + bioObjectAccession))
                        .getName());
            }
            // add the property isoformName as well, see how it's done in BinaryInteraction2Annotation.newBioObject()
            else if (BioType.PROTEIN_ISOFORM.name().equalsIgnoreCase(bioObjectType)) {

                bioObject = BioObject.internal(BioType.PROTEIN_ISOFORM);

                bioObject.putPropertyNameValue("isoformName", mainNamesService.findIsoformOrEntryMainName(bioObjectAccession)
                        .orElseThrow(() -> new NextProtException("Cannot create a binary interaction from statement " + firstStatement + ": unknown isoform accession " + bioObjectAccession))
                        .getName());
            }
            else {
                throw new NextProtException("Binary Interaction only expects to be a nextprot or an isoform entry but found " + bioObjectAccession + " with type " + bioObjectType);
            }

            bioObject.setAccession(bioObjectAccession);
            bioObject.putPropertyNameValue("geneName", bioObjectName);
            bioObject.putPropertyNameValue("url", "https://www.nextprot.org/entry/" + bioObjectAccession + "/interactions");
        }
        else if (AnnotationCategory.PHENOTYPIC_VARIATION.equals(annotationCategory)) {

            bioObject = BioObject.internal(BioType.ENTRY_ANNOTATION);
            bioObject.setAnnotationHash(bioObjectAnnotationHash);
        }
        else {
            throw new NextProtException("Category not expected for bioobject " + annotationCategory);
        }

        return bioObject;
    }
}
