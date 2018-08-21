package org.nextprot.api.etl.service.impl;

import org.apache.log4j.Logger;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.BeanService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.etl.service.StatementTransformerService;
import org.nextprot.api.etl.service.impl.StatementETLServiceImpl.ReportBuilder;
import org.nextprot.api.isoform.mapper.domain.impl.SequenceVariant;
import org.nextprot.api.isoform.mapper.service.IsoformMappingService;
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

    private static final Logger LOGGER = Logger.getLogger(StatementTranformerServiceImpl.class);

    @Autowired
    private IsoformService isoformService;

    @Autowired
    private IsoformMappingService isoformMappingService;

    @Autowired
    private BeanService beanService;

    @Override
    public Set<Statement> transformStatements(NextProtSource source, Set<Statement> rawStatements, ReportBuilder report) {

        // TODO: additionnal field should be defined outside nextprot-api
        if (source == NextProtSource.GlyConnect) {
            rawStatements = copyRawStatementsAddStatementIdAndEntryAccessionFieldsHACK(rawStatements, report);
        }

        Set<Statement> mappedStatementsToLoad = transformComposedStatements(rawStatements, report);
        LOGGER.info("Composed statement categories are " + mappedStatementsToLoad.stream()
                .map(s -> s.getValue(StatementField.ANNOTATION_CATEGORY))
                .distinct()
                .collect(Collectors.toSet()));

        Set<Statement> simpleRawStatements = getSimpleRawStatements(rawStatements);
        Set<String> simpleStatementCategories = simpleRawStatements.stream()
                .map(s -> s.getValue(StatementField.ANNOTATION_CATEGORY))
                .distinct()
                .collect(Collectors.toSet());

        LOGGER.info("Simple statement categories are " + simpleStatementCategories);
        if (simpleStatementCategories.contains(AnnotationCategory.PHENOTYPIC_VARIATION.getDbAnnotationTypeName())) {
            throw new NextProtException("Not expecting phenotypic variation at this stage.");
        }

        mappedStatementsToLoad.addAll(transformSimpleRawStatementsToMappedStatements(simpleRawStatements));

        return mappedStatementsToLoad;
    }

    // TODO: additionnal field should be defined outside nextprot-api
    private Set<Statement> copyRawStatementsAddStatementIdAndEntryAccessionFieldsHACK(Set<Statement> statements, ReportBuilder report) {

        Set<Statement> statementSet = new HashSet<>();
        Set<Statement> invalidStatements = new HashSet<>();

        statements.forEach(rs -> {
            if (rs.getValue(StatementField.NEXTPROT_ACCESSION) != null) {
                statementSet.add(new StatementBuilder()
                        .addMap(rs)
                        .addField(StatementField.ENTRY_ACCESSION, rs.getValue(StatementField.NEXTPROT_ACCESSION))
                        .addField(StatementField.RESOURCE_TYPE, "database")
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

    /**
     * <h3> What is a composed stmt ?</h3>
     * 1. reference (via fields SUBJECT_STATEMENT_IDS or OBJECT_STATEMENT_IDS) a subject that is other(s) stmt(s) (ex: variant)
     * 2. (optionally) refers to an object that is another stmt
     *
     * <h3>Example</h3>
     * MSH6-p.Ser144Ile decreases mismatch repair (BED CAVA-VP011468)
     * <p>
     * The sentence above has 3 stmts:
     * 1. stmt VARIANT: MSH6-p.Ser144Ile
     * 2. stmt GO: mismatch repair
     * 3. complex stmt: decreases (refers stmt 1 and object 2)
     **/
    private Set<Statement> transformComposedStatements(Set<Statement> rawStatements, ReportBuilder report) {

        Set<Statement> composedStatements = new HashSet<>();

        Map<String, Statement> sourceStatementsById = rawStatements.stream()
                .collect(Collectors.toMap(Statement::getStatementId, Function.identity()));

        for (Statement originalStatement : rawStatements) {

            if ((originalStatement.getSubjectStatementIds() != null) && (!originalStatement.getSubjectStatementIds().isEmpty())) {

                String[] subjectStatemendIds = originalStatement.getSubjectStatementIdsArray();
                Set<Statement> subjectStatements = getSubjects(subjectStatemendIds, sourceStatementsById);

                subjectStatements.forEach(s -> s.processed());
                originalStatement.processed();

                String entryAccession = subjectStatements.iterator().next().getValue(StatementField.ENTRY_ACCESSION);

                boolean isIsoSpecific = false;
                String isoformName = validateSubject(subjectStatements);
                String isoformSpecificAccession = null;

                if (isSubjectIsoSpecific(subjectStatements)) {
                    if (isoformName != null) {
                        isIsoSpecific = true;
                        String featureName = subjectStatements.iterator().next().getValue(StatementField.ANNOTATION_NAME);
                        isoformSpecificAccession = getIsoAccession(featureName, entryAccession);
                    } else {
                        throw new NextProtException("Something wrong occured when checking for iso specificity");
                    }
                }

                composedStatements.addAll(transformStatements(originalStatement, sourceStatementsById, subjectStatements, entryAccession, isIsoSpecific, isoformSpecificAccession, report));
            }
        }
        return composedStatements;
    }

    private Set<Statement> transformSimpleRawStatementsToMappedStatements(Set<Statement> simpleRawStatements) {

        return simpleRawStatements.stream()
                .map(statement -> {
                    String accession = statement.getValue(StatementField.NEXTPROT_ACCESSION);
                    Optional isoSpecificAccession = Optional.empty();

                    if (accession != null && accession.contains("-")) { //It is iso specific for example NX_P19544-4 means only specifc to iso 4
                        isoSpecificAccession = Optional.of(accession);
                    }

                    TargetIsoformSet targetIsoformForNormalAnnotation =
                            StatementTransformationUtil.computeTargetIsoformsForNormalAnnotation(statement, isoformService, isoformMappingService, isoSpecificAccession);

                    return StatementBuilder.createNew().addMap(statement)
                            .addField(StatementField.TARGET_ISOFORMS, targetIsoformForNormalAnnotation.serializeToJsonString())
                            .removeField(StatementField.STATEMENT_ID)
                            .removeField(StatementField.NEXTPROT_ACCESSION)
                            .buildWithAnnotationHash();
                })
                .collect(Collectors.toSet());
    }

    private Set<Statement> getSimpleRawStatements(Set<Statement> rawStatements) {
        return rawStatements.stream().filter(s -> !s.isProcessed()).collect(Collectors.toSet());
    }

    private String getIsoAccession(String featureName, String entryAccession) {

        SequenceVariant sv;
        try {
            sv = SequenceVariant.variant(featureName, beanService);
        } catch (Exception e) {
            throw new NextProtException(e);
        }

        return sv.getIsoform().getIsoformAccession();
    }

    private Map<String, List<Statement>> getSubjectsTransformed(Map<String, Statement> sourceStatementsById, Set<Statement> subjectStatements, String nextprotAcession, boolean isIsoSpecific) {

        //In case of entry variants have the target isoform property filled
        Map<String, List<Statement>> variantsOnIsoform = new HashMap<>();

        List<Statement> result = StatementTransformationUtil.getPropagatedStatementsForEntry(isoformMappingService, subjectStatements, nextprotAcession);
        variantsOnIsoform.put(nextprotAcession, result);

        return variantsOnIsoform;
    }

    Set<Statement> transformStatements(Statement originalStatement, Map<String, Statement> sourceStatementsById, Set<Statement> subjectStatements, String nextprotAcession, boolean isIsoSpecific, String isoSpecificAccession, ReportBuilder report) {

        Set<Statement> statementsToLoad = new HashSet<>();

        //In case of entry variants have the target isoform property filled
        Map<String, List<Statement>> subjectsTransformedByEntryOrIsoform = getSubjectsTransformed(sourceStatementsById, subjectStatements, nextprotAcession, isIsoSpecific);

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
                targetIsoformsForObject = StatementTransformationUtil.computeTargetIsoformsForNormalAnnotation(objectStatement, isoformService, isoformMappingService, Optional.empty()).serializeToJsonString();
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


    /**
     * Returns an exception if there are mixes between subjects
     *
     * @param subjects
     * @return
     */
    private static String validateSubject(Set<Statement> subjects) {

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
    private static boolean isSubjectIsoSpecific(Set<Statement> subjects) {
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

    private static Set<Statement> getSubjects(String[] subjectIds, Map<String, Statement> sourceStatementsById) {
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

    public Set<Statement> findSourceStatementsWhereOriginalStatementIsUsedAsSubject(Statement originalStatement, Set<Statement> sourceStatementsWithAModifiedSubject) {
        return sourceStatementsWithAModifiedSubject.stream().filter(sm -> sm.getSubjectStatementIds().contains(originalStatement.getStatementId())).collect(Collectors.toSet());
    }

    public IsoformMappingService getIsoformMappingService() {
        return isoformMappingService;
    }

    public void setIsoformMappingService(IsoformMappingService isoformMappingService) {
        this.isoformMappingService = isoformMappingService;
    }

    public IsoformService getIsoformService() {
        return isoformService;
    }

    public void setIsoformService(IsoformService isoformService) {
        this.isoformService = isoformService;
    }
}
