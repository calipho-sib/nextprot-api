package org.nextprot.api.core.service;

import com.google.common.base.Supplier;
import org.apache.log4j.Logger;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.constants.IdentifierOffset;
import org.nextprot.api.commons.constants.PropertyApiModel;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.app.StatementSource;
import org.nextprot.api.core.domain.BioObject;
import org.nextprot.api.core.domain.BioObject.BioType;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.ExperimentalContext;
import org.nextprot.api.core.domain.MainNames;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationEvidenceProperty;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.nextprot.api.core.domain.annotation.AnnotationVariant;
import org.nextprot.api.core.service.annotation.AnnotationUtils;
import org.nextprot.api.core.utils.ExperimentalContextUtil;
import org.nextprot.commons.constants.QualityQualifier;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.specs.CustomStatementField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.nextprot.api.commons.constants.AnnotationCategory.DISEASE_RELATED_VARIANT;
import static org.nextprot.api.commons.constants.IdentifierOffset.NXFLAT_ANNOTATION_ID_COUNTER;
import static org.nextprot.commons.statements.specs.CoreStatementField.*;

abstract class StatementAnnotationBuilder implements Supplier<Annotation> {

    protected static final Logger LOGGER = Logger.getLogger(StatementAnnotationBuilder.class);

    protected TerminologyService terminologyService;
    protected PublicationService publicationService;
    protected MainNamesService mainNamesService;
    protected DbXrefService dbXrefService;
    protected ExperimentalContextService experimentalContextService;

    private final Set<AnnotationCategory> ANNOT_CATEGORIES_WITHOUT_EVIDENCES = new HashSet<>(Arrays.asList(AnnotationCategory.MAMMALIAN_PHENOTYPE, AnnotationCategory.PROTEIN_PROPERTY));

    protected StatementAnnotationBuilder(TerminologyService terminologyService, PublicationService publicationService, MainNamesService mainNamesService, DbXrefService dbXrefService, ExperimentalContextService experimentalContextService ) {
        this.terminologyService = terminologyService;
        this.publicationService = publicationService;
        this.mainNamesService = mainNamesService;
        this.dbXrefService = dbXrefService;
        this.experimentalContextService = experimentalContextService;
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

    public List<Annotation> buildProteoformIsoformAnnotations(String accession, List<Statement> subjects, List<Statement> proteoformStatements) {

        List<Annotation> annotations = new ArrayList<>();

        Map<String, List<Statement>> subjectsByAnnotationId = subjects.stream().collect(Collectors.groupingBy(rs -> rs.getValue(ANNOTATION_ID)));

        Map<String, List<Statement>> impactStatementsBySubject = proteoformStatements.stream().collect(Collectors.groupingBy(r -> r.getValue(SUBJECT_ANNOTATION_IDS)));

        impactStatementsBySubject.keySet().forEach(subjectComponentsIdentifiers -> {

            String[] subjectComponentsIdentifiersArray = subjectComponentsIdentifiers.split(",");
            Set<Annotation> subjectVariants = new TreeSet<>(Comparator.comparing(Annotation::getAnnotationName));

            for (String subjectComponentIdentifier : subjectComponentsIdentifiersArray) {

                List<Statement> subjectVariant = subjectsByAnnotationId.get(subjectComponentIdentifier);

                if ((subjectVariant == null) || (subjectVariant.isEmpty())) {
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

    private List<AnnotationEvidence> buildAnnotationEvidences(List<Statement> Statements, long annotationId) {

        Map<String, AnnotationEvidence> evidencesMap = Statements.stream()
                .map(s -> buildAnnotationEvidence(s))
                .filter(e -> e.getResourceId() != -2)
                .collect(Collectors.toMap(ev -> buildAnnotationEvidenceKey(ev),
                        ev -> ev,
                        (ev1, ev2) -> (ev1.getQualityQualifier().equals(QualityQualifier.GOLD.name())) ? ev1 : ev2));

        return evidencesMap.values().stream()
                .peek(e -> {
                    e.setAnnotationId(annotationId);
                    e.setEvidenceId(IdentifierOffset.EVIDENCE_ID_COUNTER_FOR_STATEMENTS.incrementAndGet());
                })
                .collect(Collectors.toList());
    }

    private String buildAnnotationEvidenceKey(AnnotationEvidence evidence) {

        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(evidence.getResourceId()).append(".")
                .append(evidence.getExperimentalContextId()).append(".")
                .append(evidence.isNegativeEvidence()).append(".")
                .append(evidence.getAssignedBy()).append(".")
                .append(evidence.getEvidenceCodeAC())
                .toString();

        String psimiAC = evidence.getProperties().get(PropertyApiModel.NAME_PSIMI_AC);
        if (psimiAC != null) {
            keyBuilder.append(psimiAC);
        }

        return keyBuilder.toString();
    }

    private AnnotationEvidence buildAnnotationEvidence(Statement s) {
    	
        AnnotationEvidence evidence = new AnnotationEvidence();
        if (s.getValue(RESOURCE_TYPE) == null) {
            throw new NextProtException("resource type undefined");
        }
        evidence.setResourceType(s.getValue(RESOURCE_TYPE));
        evidence.setResourceAssociationType("evidence");
        evidence.setQualityQualifier(s.getValue(EVIDENCE_QUALITY));
        
        setResourceId(s, evidence);

        AnnotationEvidenceProperty evidenceProperty = addPropertyIfPresent(s.getValue(EVIDENCE_INTENSITY), "intensity");
        AnnotationEvidenceProperty expContextSubjectProteinOrigin = addPropertyIfPresent(s.getValue(ANNOTATION_SUBJECT_SPECIES), "subject-protein-origin");
        AnnotationEvidenceProperty expContextObjectProteinOrigin = addPropertyIfPresent(s.getValue(ANNOTATION_OBJECT_SPECIES), "object-protein-origin");

        // PSIMI_ID custom statement field becomes a property with name = PropertyApiModel.NAME_PSIMI_AC
        String psimiAC = s.getValue(new CustomStatementField("PSIMI_ID"));
        CvTerm t = terminologyService.findCvTermByAccession(psimiAC);
        String psimiCvName = t == null ? null : t.getName();
        AnnotationEvidenceProperty psimiIdProperty = addPropertyIfPresent(psimiAC, PropertyApiModel.NAME_PSIMI_AC);
        AnnotationEvidenceProperty psimiTermProperty = addPropertyIfPresent(psimiCvName, PropertyApiModel.NAME_PSIMI_CV_NAME);

        // Bgee statement custom fields, EXPRESSION_LEVEL, SCORE, STAGE_ID, STAGE_NAME
        String expressionLevel = s.getValue(new CustomStatementField("EXPRESSION_LEVEL"));
        String expressionScore = s.getValue(new CustomStatementField("SCORE"));
        String stage_ac = s.getValue(new CustomStatementField("STAGE_ID"));
        AnnotationEvidenceProperty expressionLevelProperty = addPropertyIfPresent(expressionLevel, PropertyApiModel.NAME_EXPRESSION_LEVEL);
        AnnotationEvidenceProperty expressionScoreProperty = addPropertyIfPresent(expressionScore, PropertyApiModel.NAME_EXPRESSION_SCORE);

        // IntAct statement custom fields, NUMBER_OF_EXPERIMENTS
        String expNb = s.getValue(new CustomStatementField("NUMBER_OF_EXPERIMENTS"));
        AnnotationEvidenceProperty expNbProperty = addPropertyIfPresent(expNb, PropertyApiModel.NAME_NUMBER_EXPERIMENTS);

        //Set properties which are not null
        evidence.setProperties(Stream.of(evidenceProperty, expContextSubjectProteinOrigin, expContextObjectProteinOrigin,
                psimiIdProperty, psimiTermProperty, expressionLevelProperty, expressionScoreProperty, expNbProperty)
                .filter(p -> p != null)
                .collect(Collectors.toList())
        );

        String statementEvidenceCode = s.getValue(EVIDENCE_CODE);
        evidence.setEvidenceCodeAC(statementEvidenceCode);
        if (statementEvidenceCode != null) {
            CvTerm term = terminologyService.findCvTermByAccessionOrThrowRuntimeException(statementEvidenceCode);
            evidence.setEvidenceCodeName(term.getName());
        }
        
        evidence.setAssignedBy(s.getValue(ASSIGNED_BY));
        evidence.setAssignmentMethod(s.getValue(ASSIGMENT_METHOD));
        evidence.setEvidenceCodeOntology("evidence-code-ontology-cv");
        evidence.setNegativeEvidence("true".equalsIgnoreCase(s.getValue(IS_NEGATIVE)));
        evidence.setNote(s.getValue(EVIDENCE_NOTE));

        // Set experimental context ID
        // Note that this currently concerns for bgee, bioeditor VA and cellosaurus statements
        // All the other annotations with inherent experimental contexts are inherited from NP1 data model itself
        if("Bgee".equals(s.getValue(SOURCE))) {
        	String tissue_ac = s.getValue(ANNOT_CV_TERM_ACCESSION);
        	String eco_ac = s.getValue(EVIDENCE_CODE);
        	String md5 = ExperimentalContextUtil.computeMd5ForBgee(tissue_ac, stage_ac, eco_ac);
            ExperimentalContext ec = experimentalContextService.findExperimentalContextByMd5(md5);
            if(ec != null) {
                long ecId = ec.getContextId();
                evidence.setExperimentalContextId(ecId);
            } else {
            	String msg = "Bgee EC with " + tissue_ac + " " + stage_ac + " " + eco_ac + " " + md5 + " not found";
            	LOGGER.error(msg);
            	throw new NextProtException(msg);
            }
        } else if (StatementSource.BioEditor.getSourceName().equals(s.getValue(SOURCE))) {
            String diseaseAc = s.getValue(new CustomStatementField("DISEASE_ACC"));
            String tissueAc = s.getValue(new CustomStatementField("TISSUE_ACC"));
            String cellLineAc = s.getValue(new CustomStatementField("CELL_LINE_ACC"));
            if (org.apache.commons.lang.StringUtils.isNotBlank(tissueAc)
                    || org.apache.commons.lang.StringUtils.isNotBlank(cellLineAc)
                    || org.apache.commons.lang.StringUtils.isNotBlank(diseaseAc)) {
                String md5 = ExperimentalContextUtil.computeMd5ForBioeditorVAs(tissueAc, cellLineAc, diseaseAc, s.getValue(EVIDENCE_CODE));
                ExperimentalContext ec = experimentalContextService.findExperimentalContextByMd5(md5);
                if (ec != null) {
                    evidence.setExperimentalContextId(ec.getContextId());
                } else {
                    String msg = "BioEditor EC with " + tissueAc + " " + cellLineAc + " " + diseaseAc + " "
                            + s.getValue(EVIDENCE_CODE) + " " + md5 + " not found";
                    LOGGER.error(msg);
                    throw new NextProtException(msg);
                }
            }
        } else if (StatementSource.Cellosaurus.getSourceName().equals(s.getValue(SOURCE))) {
            String diseaseAcc = s.getValue(new CustomStatementField("DISEASE_ACC"));
            String cellLineAc = s.getValue(new CustomStatementField("CELL_LINE_ACC"));
                String md5 = ExperimentalContextUtil.computeMd5ForCellosaurus(diseaseAcc, cellLineAc, s.getValue(EVIDENCE_CODE));
                ExperimentalContext ec = experimentalContextService.findExperimentalContextByMd5(md5);
                if (ec != null) {
                    evidence.setExperimentalContextId(ec.getContextId());
                } else {
                    String msg = "Cellosaurus EC with " + diseaseAcc + " " + cellLineAc + " " + md5 + " not found";
                    LOGGER.error(msg);
                    throw new NextProtException(msg);
                }
        }

        return evidence;
    }

    private void setResourceId(Statement s, AnnotationEvidence evidence) {

        String resourceType = evidence.getResourceType();

        if (resourceType.equals("publication")) {
            evidence.setResourceId(findPublicationId(s));
        } else if (resourceType.equals("database")) {
            evidence.setResourceId(findXrefId(s));
            evidence.setResourceAccession(s.getValue(REFERENCE_ACCESSION));
            evidence.setResourceDb(s.getValue(REFERENCE_DATABASE));
        } else {
            throw new NextProtException("Cannot set resource id: resource type " + resourceType + " is not supported");
        }
    }

    abstract void setIsoformName(Annotation annotation, String statement);

    abstract void setIsoformTargeting(Annotation annotation, Statement statement);

    protected void setVariantAttributes(Annotation annotation, Statement variantStatement) {

        String original = variantStatement.getValue(VARIANT_ORIGINAL_AMINO_ACID);
        String variant = variantStatement.getValue(VARIANT_VARIATION_AMINO_ACID);
        AnnotationVariant annotationVariant = new AnnotationVariant(original, variant.equals("-") ? "" : variant);
        annotation.setVariant(annotationVariant);

    }

    long findPublicationId(Statement statement) {

        String referenceDB = statement.getValue(REFERENCE_DATABASE);
        String referenceAC = statement.getValue(REFERENCE_ACCESSION);

        Publication publication = publicationService.findPublicationByDatabaseAndAccession(referenceDB, referenceAC);
        if (publication == null) {
            String message = "can 't find publication db:" + referenceDB + " acc:" + referenceAC;

            LOGGER.error(message);

            throw new NextProtException(message);
        }

        return publication.getPublicationId();
    }

    long findXrefId(Statement statement) {
        String referenceDB = statement.getValue(REFERENCE_DATABASE);
        String referenceAC = statement.getValue(REFERENCE_ACCESSION);
        return dbXrefService.findXrefId(referenceDB, referenceAC);
    }

    protected Annotation buildAnnotation(String isoformName, List<Statement> flatStatements) {
        List<Annotation> annotations = buildAnnotationList(isoformName, flatStatements);
        if (annotations.isEmpty() || annotations.size() > 1) {
            throw new NextProtException("Expecting 1 annotation but found " + annotations.size() + " from " + flatStatements.size());
        }
        return annotations.get(0);
    }

    public List<Annotation> buildAnnotationList(String isoformName, List<Statement> flatStatements) {

        List<Annotation> annotations = new ArrayList<>();
        Map<String, List<Statement>> flatStatementsByAnnotationHash = flatStatements.stream().collect(Collectors.groupingBy(rs -> rs.getValue(ANNOTATION_ID)));

        flatStatementsByAnnotationHash.forEach((key, statements) -> {
            Annotation annotation = get();

            Statement firstStatement = statements.get(0);

            annotation.setAnnotationHash(firstStatement.getValue(ANNOTATION_ID));
            annotation.setAnnotationId(NXFLAT_ANNOTATION_ID_COUNTER.incrementAndGet());

            AnnotationCategory category = AnnotationCategory.getDecamelizedAnnotationTypeName(StringUtils.camelToKebabCase(firstStatement.getValue(ANNOTATION_CATEGORY)));
            annotation.setAnnotationCategory(category);

            if (category.equals(AnnotationCategory.VARIANT) || category.equals(AnnotationCategory.MUTAGENESIS)) {
                setVariantAttributes(annotation, firstStatement);
            }
            setIsoformTargeting(annotation, firstStatement);

            setIsoformName(annotation, isoformName);

            annotation.setDescription(firstStatement.getValue(ANNOT_DESCRIPTION));
            if (AnnotationCategory.DISEASE.equals(category)) {
                CvTerm cvTerm = terminologyService.findCvTermByAccession(firstStatement.getValue(ANNOT_CV_TERM_ACCESSION));
                annotation.setDescription(
                        cvTerm.getName() + " (" + cvTerm.getAccession()+ ") [" + cvTerm.getOntologyDisplayName() + ":" + cvTerm.getAccession() + "]: " +
                        cvTerm.getDescription() + " The disease is caused by mutations affecting the gene represented in this entry.");
            }
            String cvTermAccession = firstStatement.getValue(ANNOT_CV_TERM_ACCESSION);

            //Set the evidences if not Mammalian phenotype or Protein Property https://issues.isb-sib.ch/browse/BIOEDITOR-466
            if (!ANNOT_CATEGORIES_WITHOUT_EVIDENCES.contains(category)) {
                annotation.setEvidences(buildAnnotationEvidences(statements, annotation.getAnnotationId()));

                //TODO Remove this when you are able to do XREFs
                if (((annotation.getEvidences() == null) || ((annotation.getEvidences().isEmpty()))) && (category.equals(AnnotationCategory.VARIANT) || category.equals(AnnotationCategory.MUTAGENESIS))) {
                    annotation.setQualityQualifier("GOLD");//All variants from BED are GOLD, and this is a special case when we don't have evidences for VDs.
                } else {
                    annotation.setQualityQualifier(AnnotationUtils.computeAnnotationQualityBasedOnEvidences(annotation.getEvidences()).name());
                }

            } else {

                //Case of Protein propert and mammalian phenotypes
                annotation.setEvidences(new ArrayList<>());

                boolean foundGold = statements.stream().anyMatch(s -> s.getValue(EVIDENCE_QUALITY).equalsIgnoreCase("GOLD"));
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
                    annotation.setCvTermName(firstStatement.getValue(ANNOT_CV_TERM_NAME));
                    annotation.setCvApiName(firstStatement.getValue(ANNOT_CV_TERM_TERMINOLOGY));
                }
            }

            annotation.setAnnotationHash(firstStatement.getValue(ANNOTATION_ID));
            annotation.setAnnotationName(firstStatement.getValue(ANNOTATION_NAME));

            //Check this with PAM (does it need to be a human readable stuff)
            annotation.setUniqueName(firstStatement.getValue(ANNOTATION_ID)); //Does it need a name?

            String bioObjectAnnotationHash = firstStatement.getValue(OBJECT_ANNOTATION_IDS);
            String bioObjectAccession = firstStatement.getValue(BIOLOGICAL_OBJECT_ACCESSION);

            if ((bioObjectAnnotationHash != null && !bioObjectAnnotationHash.isEmpty()) ||
                    (bioObjectAccession != null && !bioObjectAccession.isEmpty())) {

                annotation.setBioObject(newBioObject(firstStatement, annotation.getAPICategory()));
            }

            // For interaction mappings, add the interacting region from statement as a property
            if (AnnotationCategory.INTERACTION_MAPPING.equals(annotation.getAPICategory())) {
                AnnotationProperty annotationProperty = new AnnotationProperty();
                annotationProperty.setAnnotationId(annotation.getAnnotationId());
                annotationProperty.setName("mapping-sequence");
                annotationProperty.setValue(firstStatement.getValue(new CustomStatementField("MAPPING_SEQUENCE")));
                annotation.addProperty(annotationProperty);
                // Adds the description
                annotation.setDescription("Interaction with " + annotation.getBioObject().getPropertyValue("geneName"));

            } else if (AnnotationCategory.BINARY_INTERACTION.equals(annotation.getAPICategory())) {
                String p1 = firstStatement.getEntryAccession();
                Optional<String> isoformSpecificOptional = firstStatement.getOptionalIsoformAccession();
                if (isoformSpecificOptional.isPresent()) {
                    p1 = isoformSpecificOptional.get();
                }
                String p2 = annotation.getBioObject().getAccession();
                AnnotationProperty annotationProperty = new AnnotationProperty();
                annotationProperty.setAnnotationId(annotation.getAnnotationId());
                annotationProperty.setName("selfInteraction");
                annotationProperty.setValue(String.valueOf(p1.equals(p2)));
                annotation.addProperty(annotationProperty);
            }

            annotations.add(annotation);
        });

        return annotations;
    }

    private BioObject newBioObject(Statement statement, AnnotationCategory annotationCategory) {

        String bioObjectAnnotationHash = statement.getValue(OBJECT_ANNOTATION_IDS);
        String bioObjectAccession = statement.getValue(BIOLOGICAL_OBJECT_ACCESSION);
        String bioObjectType = statement.getValue(BIOLOGICAL_OBJECT_TYPE);
        String bioObjectName = statement.getValue(BIOLOGICAL_OBJECT_NAME);
        String bioObjectDb = statement.getValue(BIOLOGICAL_OBJECT_DATABASE);

        BioObject bioObject;

        // Both binary interaction and interaction mapping annotations are handled in the same way
        if (AnnotationCategory.BINARY_INTERACTION.equals(annotationCategory) || AnnotationCategory.INTERACTION_MAPPING.equals(annotationCategory)) {
            String url;
            if (!bioObjectAccession.startsWith("NX_")) {
                if (AnnotationCategory.INTERACTION_MAPPING.equals(annotationCategory)) {
                    throw new NextProtException("Interaction mapping only expects to be a nextprot entry starting with NX_ but found "
                            + bioObjectAccession + " with type " + bioObjectType);
                }
                if (bioObjectDb == null) {
                    throw new NextProtException("Cannot create a binary interaction from statement " + statement +
                            ": database is null for BioObject " + bioObjectAccession);
                }
                if (!bioObjectDb.equals("UniProtKB")) {
                    throw new NextProtException("Cannot create a binary interaction from statement " + statement +
                            ": database is not UniProtKB for BioObject " + bioObjectAccession);
                }

                bioObject = BioObject.external(BioType.PROTEIN, bioObjectDb);

                url = "http://www.uniprot.org/uniprot/" + bioObjectAccession;

            } else {
                // Here, it's supposed to be neXtProt accession
                MainNames mainNames = mainNamesService.findIsoformOrEntryMainName(bioObjectAccession)
                                                      .orElseThrow(() -> new NextProtException("Cannot create a binary interaction from statement " +
                                                              statement + ": unknown isoform accession " + bioObjectAccession));
                url = "https://www.nextprot.org/entry/" + bioObjectAccession + "/interactions";
                bioObjectName = ( mainNames.getGeneNameList() != null && !mainNames.getGeneNameList().isEmpty()) ? mainNames.getGeneNameList().get(0) : bioObjectName;

                if (BioType.PROTEIN.name().equalsIgnoreCase(bioObjectType)) {

                    bioObject = BioObject.internal(BioType.PROTEIN);

                    bioObject.putPropertyNameValue("proteinName", mainNames.getName());

                }
                // add the property isoformName as well, see how it's done in BinaryInteraction2Annotation.newBioObject()
                else if (BioType.PROTEIN_ISOFORM.name().equalsIgnoreCase(bioObjectType)) {

                    bioObject = BioObject.internal(BioType.PROTEIN_ISOFORM);

                    bioObject.putPropertyNameValue("isoformName", mainNames.getName());

                } else {
                    throw new NextProtException("Binary Interaction only expects to be a nextprot or an isoform entry but found " + bioObjectAccession + " with type " + bioObjectType);
                }
            }
            bioObject.putPropertyNameValue("url", url);
            bioObject.setAccession(bioObjectAccession);
            bioObject.putPropertyNameValue("geneName", bioObjectName == null || bioObjectName == "" ? "-" : bioObjectName);

        } else if (AnnotationCategory.PHENOTYPIC_VARIATION.equals(annotationCategory)
                || DISEASE_RELATED_VARIANT.equals(annotationCategory)) {

            bioObject = BioObject.internal(BioType.ENTRY_ANNOTATION);
            bioObject.setAnnotationHash(bioObjectAnnotationHash);

        } else {
            throw new NextProtException("Category not expected for bioobject " + annotationCategory);
        }

        return bioObject;
    }
}
