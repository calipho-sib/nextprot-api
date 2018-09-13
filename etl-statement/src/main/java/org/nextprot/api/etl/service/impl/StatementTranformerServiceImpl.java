package org.nextprot.api.etl.service.impl;

import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.etl.service.StatementTransformerService;
import org.nextprot.api.etl.service.impl.StatementETLServiceImpl.ReportBuilder;
import org.nextprot.api.isoform.mapper.service.IsoformMappingService;
import org.nextprot.api.isoform.mapper.service.SequenceFeatureFactoryService;
import org.nextprot.api.isoform.mapper.utils.SequenceVariantUtils;
import org.nextprot.commons.statements.*;
import org.nextprot.commons.statements.constants.NextProtSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StatementTranformerServiceImpl implements StatementTransformerService {

    private static Logger LOGGER = Logger.getLogger(StatementTranformerServiceImpl.class);

    @Autowired
    private IsoformService isoformService;

    @Autowired
    private IsoformMappingService isoformMappingService;

    @Autowired
    private SequenceFeatureFactoryService sequenceFeatureFactoryService;

    @Override
    public Set<Statement> transformStatements(NextProtSource source, Set<Statement> rawStatements, ReportBuilder report) {

        // TODO: additionnal field should be defined outside nextprot-api
        if (source == NextProtSource.GlyConnect) {
            rawStatements = setAdditionalFieldsForGlyConnectStatementsHACK(rawStatements, report);
        }

        return new StatementTransformer(rawStatements, report).transform();
    }

    // TODO: additionnal field should be defined outside nextprot-api
    private Set<Statement> setAdditionalFieldsForGlyConnectStatementsHACK(Set<Statement> statements, ReportBuilder report) {

        Set<Statement> statementSet = new HashSet<>();
        Set<Statement> invalidStatements = new HashSet<>();

        statements.forEach(rs -> {
            if (rs.getValue(StatementField.NEXTPROT_ACCESSION) != null) {
                statementSet.add(new StatementBuilder()
                        .addMap(rs)
                        .addField(StatementField.ENTRY_ACCESSION, rs.getValue(StatementField.NEXTPROT_ACCESSION))
                        .addField(StatementField.RESOURCE_TYPE, "database")
                        .addField(StatementField.ANNOTATION_NAME, buildAnnotationNameForGlyConnect(rs))
                        .build());
            } else {
                invalidStatements.add(rs);
            }
        });

        if (!invalidStatements.isEmpty()) {
            report.addWarning("Undefined neXtProt accessions: skipping " + invalidStatements.size() + " statements");
        }

        report.addInfo("Created " + statementSet.size() + "/" + (statements.size()) + " statements with additionnal fields (ENTRY_ACCESSION, STATEMENT_ID)");

        return statementSet;
    }

    private String buildAnnotationNameForGlyConnect(Statement statement) {

        return statement.getValue(StatementField.NEXTPROT_ACCESSION) +
                "." + statement.getValue(StatementField.ANNOT_CV_TERM_ACCESSION) +
                "_" + statement.getValue(StatementField.LOCATION_BEGIN);
    }
    ////// END TODO

    public void setIsoformMappingService(IsoformMappingService isoformMappingService) {
        this.isoformMappingService = isoformMappingService;
    }

    public void setIsoformService(IsoformService isoformService) {
        this.isoformService = isoformService;
    }

    public void setSequenceFeatureFactoryService(SequenceFeatureFactoryService sequenceFeatureFactoryService) {
        this.sequenceFeatureFactoryService = sequenceFeatureFactoryService;
    }

    class StatementTransformer {

        private final Set<Statement> rawStatements;
        private final ReportBuilder report;
        private final Map<String, Statement> sourceStatementsById;

        StatementTransformer(Set<Statement> rawStatements, ReportBuilder report) {

            Preconditions.checkNotNull(rawStatements);
            Preconditions.checkNotNull(report);

            if (rawStatements.isEmpty()) {
                throw new NextProtException("missing raw statements");
            }
            this.rawStatements = rawStatements;
            this.report = report;
            this.sourceStatementsById = rawStatements.stream()
                    .collect(Collectors.toMap(Statement::getStatementId, Function.identity()));
        }

        public Set<Statement> transform() {

            Set<Statement> mappedStatements = new HashSet<>();
            Set<String> trackedStatementIds = new HashSet<>();

            for (Statement statement : rawStatements) {

                if (isTripletStatement(statement)) {

                    mappedStatements.addAll(transformTripletStatement(statement, trackedStatementIds));
                }
                else if (!trackedStatementIds.contains(statement.getValue(StatementField.STATEMENT_ID))) {

                    transformSimpleStatement(statement).ifPresent(s -> mappedStatements.add(s));
                }
            }

            return mappedStatements;
        }

        /**
         * <h3> Triplet stmt</h3>
         * 1. reference (via fields SUBJECT_STATEMENT_IDS or OBJECT_STATEMENT_IDS) a subject that is other(s) stmt(s) (ex: variant)
         * 2. (optionally) refers to an object that is another stmt
         *
         * <h3>Example</h3>
         * MSH6-p.Ser144Ile decreases mismatch repair (BED CAVA-VP011468)
         * <p>
         * The sentence above has 3 stmts:
         * 1. stmt SUBJECT (ex: VARIANT: MSH6-p.Ser144Ile)
         * 2. stmt OBJECT (ex: GO: mismatch repair)
         * 3. a stmt VERB (ex: stmt 1. decreases stmt 2.)
         **/
        private Set<Statement> transformTripletStatement(Statement originalStatement, Set<String> trackedStatementIds) {

            if (!isTripletStatement(originalStatement)) {
                throw new IllegalStateException("should be a triplet type statement: " + originalStatement);
            }

            Set<Statement> subjectStatements = getSubjects(originalStatement.getSubjectStatementIdsArray());
            trackedStatementIds.addAll(subjectStatements.stream().map(statement -> statement.getValue(StatementField.STATEMENT_ID)).collect(Collectors.toList()));
            trackedStatementIds.add(originalStatement.getValue(StatementField.STATEMENT_ID));

            String entryAccession = subjectStatements.iterator().next().getValue(StatementField.ENTRY_ACCESSION);

            String isoformName = getSubjectIsoformName(subjectStatements);
            if (isoformName == null) {
                throw new NextProtException("Isoform name is not defined, something wrong occurred when checking for iso specificity");
            }

            String isoformSpecificAccession = null;
            boolean isIsoSpecific = isSubjectIsoSpecific(subjectStatements);

            if (isIsoSpecific) {

                Statement subject = subjectStatements.iterator().next();
                String featureName = subject.getValue(StatementField.ANNOTATION_NAME);
                String featureType = subject.getValue(StatementField.ANNOTATION_CATEGORY);
                isoformSpecificAccession = getIsoAccession(featureName, featureType);
            }

            return transformTripletStatement(originalStatement, subjectStatements, entryAccession, isIsoSpecific, isoformSpecificAccession);
        }

        private Optional<Statement> transformSimpleStatement(Statement simpleStatement) {

            String category = simpleStatement.getValue(StatementField.ANNOTATION_CATEGORY);

            if (category.equals(AnnotationCategory.PHENOTYPIC_VARIATION.getDbAnnotationTypeName())) {
                throw new NextProtException("Not expecting phenotypic variation at this stage.");
            }

            TargetIsoformSet tis = StatementTransformationUtil.computeTargetIsoformsForNormalAnnotation(simpleStatement, isoformService, isoformMappingService);

            if (tis.isEmpty()) {

                LOGGER.warn("Skipping statement "+simpleStatement.getValue(StatementField.ANNOTATION_NAME) + " (source="+simpleStatement.getValue(StatementField.ASSIGNED_BY)+")");
                return Optional.empty();
            }

            return Optional.of(StatementBuilder.createNew()
                    .addMap(simpleStatement)
                    .addField(StatementField.TARGET_ISOFORMS, tis.serializeToJsonString())
                    .removeField(StatementField.STATEMENT_ID)
                    .removeField(StatementField.NEXTPROT_ACCESSION)
                    .buildWithAnnotationHash());
        }

        private Set<Statement> getSubjects(String[] subjectIds) {

            Set<Statement> variants = new HashSet<>();
            for (String subjectId : subjectIds) {
                Statement subjectStatement = sourceStatementsById.get(subjectId);
                if (subjectStatement == null) {
                    throw new NextProtException("Subject " + subjectId + " not present in the given list");
                }
                variants.add(subjectStatement);
            }
            return variants;
        }

        private boolean isTripletStatement(Statement statement) {

            return statement.getSubjectStatementIds() != null && !statement.getSubjectStatementIds().isEmpty();
        }

        private String getIsoAccession(String featureName, String featureType) {

            try {
                return sequenceFeatureFactoryService.newSequenceFeature(featureName, featureType).getIsoform().getIsoformAccession();
            } catch (Exception e) {
                throw new NextProtException(e);
            }
        }

        private Map<String, List<Statement>> getSubjectsTransformed(Set<Statement> subjectStatements, String nextprotAccession) {

            //In case of entry variants have the target isoform property filled
            Map<String, List<Statement>> variantsOnIsoform = new HashMap<>();

            List<Statement> result = StatementTransformationUtil.getPropagatedStatementsForEntry(isoformMappingService, subjectStatements, nextprotAccession);
            variantsOnIsoform.put(nextprotAccession, result);

            return variantsOnIsoform;
        }

        private Set<Statement> transformTripletStatement(Statement originalStatement, Set<Statement> subjectStatements, String nextprotAccession,
                                           boolean isIsoSpecific, String isoSpecificAccession) {

            Set<Statement> statementsToLoad = new HashSet<>();

            //In case of entry variants have the target isoform property filled
            Map<String, List<Statement>> subjectsTransformedByEntryOrIsoform = getSubjectsTransformed(subjectStatements, nextprotAccession);

            for (Map.Entry<String, List<Statement>> entry : subjectsTransformedByEntryOrIsoform.entrySet()) {

                List<Statement> subjects = entry.getValue();

                if (subjects.isEmpty()) {
                    report.addWarning("Empty subjects are not allowed for " + entry.getKey() + " skipping... case for 1 variant");
                    continue;
                }

                String targetIsoformsForObject;
                String targetIsoformsForPhenotype;

                String entryAccession = subjects.get(0).getValue(StatementField.ENTRY_ACCESSION);

                List<Isoform> isoforms = isoformService.findIsoformsByEntryName(entryAccession);
                NPreconditions.checkNotEmpty(isoforms, "Isoforms should not be null for " + entryAccession);

                List<String> isoformNames = isoforms.stream().map(Isoform::getIsoformAccession).collect(Collectors.toList());

                TargetIsoformSet targetIsoformsForPhenotypeSet = StatementTransformationUtil.computeTargetIsoformsForProteoformAnnotation(subjects, isIsoSpecific, isoSpecificAccession, isoformNames);
                targetIsoformsForPhenotype = targetIsoformsForPhenotypeSet.serializeToJsonString();

                Set<TargetIsoformStatementPosition> targetIsoformsForObjectSet = new TreeSet<>();

                //Load objects
                Statement phenotypeIsoStatement;
                Statement objectIsoStatement = null;
                Statement objectStatement = sourceStatementsById.get(originalStatement.getObjectStatementId());

                if (isIsoSpecific) {//If it is iso specific
                    for (TargetIsoformStatementPosition tisp : targetIsoformsForPhenotypeSet) {
                        targetIsoformsForObjectSet.add(new TargetIsoformStatementPosition(tisp.getIsoformAccession(), tisp.getSpecificity(), null));
                    }
                    targetIsoformsForObject = new TargetIsoformSet(targetIsoformsForObjectSet).serializeToJsonString();
                } else {
                    targetIsoformsForObject = StatementTransformationUtil.computeTargetIsoformsForNormalAnnotation(objectStatement, isoformService, isoformMappingService).serializeToJsonString();
                }

                if (objectStatement != null) {

                    objectStatement.processed();
                    objectIsoStatement = StatementBuilder.createNew().addMap(objectStatement)
                            .addField(StatementField.TARGET_ISOFORMS, targetIsoformsForObject)
                            .buildWithAnnotationHash();

                    phenotypeIsoStatement = StatementBuilder.createNew().addMap(originalStatement)
                            .addField(StatementField.TARGET_ISOFORMS, targetIsoformsForPhenotype)
                            .addSubjects(subjects).addObject(objectIsoStatement)
                            .removeField(StatementField.STATEMENT_ID)
                            .removeField(StatementField.SUBJECT_STATEMENT_IDS)
                            .removeField(StatementField.OBJECT_STATEMENT_IDS)
                            .buildWithAnnotationHash();
                } else {

                    phenotypeIsoStatement = StatementBuilder.createNew().addMap(originalStatement)
                            .addField(StatementField.TARGET_ISOFORMS, targetIsoformsForPhenotype) // in case of entry
                            .addSubjects(subjects)
                            .removeField(StatementField.STATEMENT_ID)
                            .removeField(StatementField.SUBJECT_STATEMENT_IDS)
                            .removeField(StatementField.OBJECT_STATEMENT_IDS)
                            .buildWithAnnotationHash();
                }

                //Load subjects
                statementsToLoad.addAll(subjects);

                //Load VPs
                statementsToLoad.add(phenotypeIsoStatement);

                //Load objects
                if (objectIsoStatement != null) {
                    statementsToLoad.add(objectIsoStatement);
                }
            }

            return statementsToLoad;
        }

        private String getSubjectIsoformName(Set<Statement> subjects) {

            Set<String> isoforms = subjects.stream()
                    .map(s -> s.getValue(StatementField.NEXTPROT_ACCESSION) + "-" + SequenceVariantUtils.getIsoformName(s.getValue(StatementField.ANNOTATION_NAME)))
                    .collect(Collectors.toSet());

            if (isoforms.size() != 1) {
                throw new NextProtException("Mixing iso numbers for subjects is not allowed");
            }
            String isoform = isoforms.iterator().next();
            if (isoform == null) {
                throw new NextProtException("Not iso specific subjects are not allowed on isOnSameIsoform");
            }

            return isoform;
        }

        /**
         * Returns an exception if there are mixes between subjects
         *
         * @param subjects
         * @return
         */
        private boolean isSubjectIsoSpecific(Set<Statement> subjects) {

            long isoSpecificSize = subjects.stream()
                    .filter(s -> SequenceVariantUtils.isIsoSpecific(s.getValue(StatementField.ANNOTATION_NAME)))
                    .count();

            if (isoSpecificSize == 0) {
                return false;
            } else if (isoSpecificSize == subjects.size()) {
                return true;
            } else {
                throw new NextProtException("Mixing iso specific subjects with non-iso specific variants is not allowed");
            }
        }
    }
}
