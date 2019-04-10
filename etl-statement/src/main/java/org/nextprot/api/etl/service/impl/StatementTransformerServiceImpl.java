package org.nextprot.api.etl.service.impl;

import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.etl.NextProtSource;
import org.nextprot.api.etl.service.StatementTransformerService;
import org.nextprot.api.etl.service.impl.StatementETLServiceImpl.ReportBuilder;
import org.nextprot.api.isoform.mapper.service.IsoformMappingService;
import org.nextprot.api.isoform.mapper.service.SequenceFeatureFactoryService;
import org.nextprot.api.isoform.mapper.utils.SequenceVariantUtils;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementBuilder;
import org.nextprot.commons.statements.TargetIsoformSet;
import org.nextprot.commons.statements.TargetIsoformStatementPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.nextprot.commons.statements.specs.CoreStatementField.*;

@Service
public class StatementTransformerServiceImpl implements StatementTransformerService {

    private static Logger LOGGER = Logger.getLogger(StatementTransformerServiceImpl.class);

	@Autowired
    private IsoformService isoformService;

    @Autowired
    private IsoformMappingService isoformMappingService;

    @Autowired
    private SequenceFeatureFactoryService sequenceFeatureFactoryService;

    @Override
    public Collection<Statement> transformStatements(NextProtSource source, Collection<Statement> rawStatements, ReportBuilder report) {

        return new StatementTransformer(rawStatements, report).transform();
    }

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

        private final Collection<Statement> rawStatements;
        private final ReportBuilder report;
        private final Map<String, Statement> sourceStatementsById;
        private final Set<String> trackedStatementIds;

        StatementTransformer(Collection<Statement> rawStatements, ReportBuilder report) {

            Preconditions.checkNotNull(rawStatements);
            Preconditions.checkNotNull(report);

            if (rawStatements.isEmpty()) {
                throw new NextProtException("missing raw statements");
            }
            this.rawStatements = rawStatements;
            this.report = report;
            this.sourceStatementsById = rawStatements.stream()
                    .collect(Collectors.toMap(Statement::getStatementId, Function.identity()));
            trackedStatementIds = new HashSet<>();
        }

        private Set<Statement> transform() {

            Set<Statement> mappedStatements = new HashSet<>();
            trackedStatementIds.clear();

            for (Statement statement : rawStatements) {

                if (isTripletStatement(statement)) {

                    mappedStatements.addAll(transformTripletStatement(statement));
                }
                else if (!trackedStatementIds.contains(statement.getValue(STATEMENT_ID))) {

                    transformSimpleStatement(statement).ifPresent(s -> mappedStatements.add(s));
                    trackedStatementIds.add(statement.getValue(STATEMENT_ID));
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
        private Set<Statement> transformTripletStatement(Statement originalStatement) {

            if (!isTripletStatement(originalStatement)) {
                throw new IllegalStateException("should be a triplet type statement: " + originalStatement);
            }

            Set<Statement> subjectStatements = getSubjects(originalStatement.getSubjectStatementIdsArray());
            trackStatementIds(originalStatement, subjectStatements);

            Statement subjectStatement = subjectStatements.iterator().next();
            String firstSubjectEntryAccession = subjectStatement.getValue(ENTRY_ACCESSION);
            String firstSubjectIsoformName = getFirstSubjectIsoformName(subjectStatements);

            if (firstSubjectIsoformName == null) {
                throw new NextProtException("Isoform name is not defined, something wrong occurred when checking for iso specificity");
            }

            String isoformSpecificAccession = null;
            boolean isIsoSpecific = isSubjectIsoSpecific(subjectStatements);

            if (isIsoSpecific) {

                isoformSpecificAccession = getIsoAccession(subjectStatement);
            }

            return transformTripletStatement(originalStatement, subjectStatements, firstSubjectEntryAccession, isIsoSpecific, isoformSpecificAccession);
        }

        private void trackStatementIds(Statement originalStatement, Set<Statement> subjectStatements) {

            trackedStatementIds.addAll(subjectStatements.stream()
                    .map(statement -> statement.getValue(STATEMENT_ID))
                    .collect(Collectors.toList()));
            trackedStatementIds.add(originalStatement.getValue(STATEMENT_ID));
        }

        private Optional<Statement> transformSimpleStatement(Statement simpleStatement) {

            String category = simpleStatement.getValue(ANNOTATION_CATEGORY);

            if (category.equals(AnnotationCategory.PHENOTYPIC_VARIATION.getDbAnnotationTypeName())) {
                throw new NextProtException("Not expecting phenotypic variation at this stage.");
            }

            StatementTransformationUtil.IsoformPositions isoformPositions =
                    StatementTransformationUtil.computeTargetIsoformsForNormalAnnotation(simpleStatement, isoformService, isoformMappingService);

            if (!isoformPositions.hasTargetIsoforms()) {

                LOGGER.warn("Skipping statement "+simpleStatement.getValue(ANNOTATION_NAME) + " (source="+simpleStatement.getValue(ASSIGNED_BY)+")");
                return Optional.empty();
            }

            StatementBuilder builder = new StatementBuilder(simpleStatement)
                    .addField(RAW_STATEMENT_ID, simpleStatement.getStatementId());

            if (isoformPositions.hasExactPositions()) {
                builder.addField(LOCATION_BEGIN, String.valueOf(isoformPositions.getBeginPositionOfCanonicalOrIsoSpec()))
                        .addField(LOCATION_END, String.valueOf(isoformPositions.getEndPositionOfCanonicalOrIsoSpec()))
                        .addField(LOCATION_BEGIN_MASTER, String.valueOf(isoformPositions.getMasterBeginPosition()))
                        .addField(LOCATION_END_MASTER, String.valueOf(isoformPositions.getMasterEndPosition()));
            }

            return Optional.of(builder
                    .addField(ISOFORM_CANONICAL, isoformPositions.getCanonicalIsoform())
                    .addField(TARGET_ISOFORMS, isoformPositions.getTargetIsoformSet().serializeToJsonString())
                    .withAnnotationHash()
                    .build());
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

        private String getIsoAccession(Statement statement) {

            String featureName = statement.getValue(ANNOTATION_NAME);
            String featureType = statement.getValue(ANNOTATION_CATEGORY);

            try {
                return sequenceFeatureFactoryService.newSequenceFeature(featureName, featureType).getIsoform().getIsoformAccession();
            } catch (Exception e) {
                throw new NextProtException(e);
            }
        }

        // TODO: WTF ??? do we really need a map to store just a key -> value ??? NO!!!
        private Map<String, List<Statement>> getSubjectsTransformed(Set<Statement> subjectStatements, String nextprotAccession) {

            Map<String, List<Statement>> variantsOnIsoform = new HashMap<>();

            List<Statement> result = StatementTransformationUtil.getPropagatedStatementVariantsForEntry(isoformMappingService, subjectStatements, nextprotAccession);
            variantsOnIsoform.put(nextprotAccession, result);

            return variantsOnIsoform;
        }

        private Set<Statement> transformTripletStatement(Statement originalStatement, Set<Statement> subjectStatementSet, String nextprotAccession,
                                           boolean isIsoSpecific, String isoSpecificAccession) {

            Set<Statement> statementsToLoad = new HashSet<>();

            //In case of entry variants have the target isoform property filled
            for (Map.Entry<String, List<Statement>> entry : getSubjectsTransformed(subjectStatementSet, nextprotAccession).entrySet()) {

                List<Statement> subjectStatements = entry.getValue();

                if (subjectStatements.isEmpty()) {
                    report.addWarning("Empty subjects are not allowed for " + entry.getKey() + " skipping... case for 1 variant");
                    continue;
                }

                String targetIsoformsForObject;
                String targetIsoformsForPhenotype;

                String entryAccession = subjectStatements.get(0).getValue(ENTRY_ACCESSION);

                List<Isoform> isoforms = isoformService.findIsoformsByEntryName(entryAccession);
                NPreconditions.checkNotEmpty(isoforms, "Isoforms should not be null for " + entryAccession);

                List<String> isoformNames = isoforms.stream().map(Isoform::getIsoformAccession).collect(Collectors.toList());

                TargetIsoformSet targetIsoformsForPhenotypeSet = StatementTransformationUtil.computeTargetIsoformsForProteoformAnnotation(subjectStatements, isIsoSpecific, isoSpecificAccession, isoformNames);
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
                    targetIsoformsForObject = StatementTransformationUtil.computeTargetIsoformsForNormalAnnotation(objectStatement, isoformService, isoformMappingService)
                            .getTargetIsoformSet().serializeToJsonString();
                }

                if (objectStatement != null) {

                    trackedStatementIds.add(objectStatement.getValue(STATEMENT_ID));
                    objectIsoStatement = new StatementBuilder(objectStatement)
                            .addField(TARGET_ISOFORMS, targetIsoformsForObject)
                            .addField(RAW_STATEMENT_ID, objectStatement.getStatementId())
                            .withAnnotationHash()
                            .build();

                    phenotypeIsoStatement = new StatementBuilder(originalStatement)
                            .addField(TARGET_ISOFORMS, targetIsoformsForPhenotype)
                            .addSubjects(subjectStatements)
                            .addObject(objectIsoStatement)
                            .removeField(STATEMENT_ID)
                            .removeField(SUBJECT_STATEMENT_IDS)
                            .removeField(OBJECT_STATEMENT_IDS)
                            .addField(RAW_STATEMENT_ID, originalStatement.getStatementId())
                            .withAnnotationHash()
                            .build();
                } else {

                    phenotypeIsoStatement = new StatementBuilder(originalStatement)
                            .addField(TARGET_ISOFORMS, targetIsoformsForPhenotype) // in case of entry
                            .addSubjects(subjectStatements)
                            .removeField(STATEMENT_ID)
                            .removeField(SUBJECT_STATEMENT_IDS)
                            .removeField(OBJECT_STATEMENT_IDS)
                            .addField(RAW_STATEMENT_ID, originalStatement.getStatementId())
                            .withAnnotationHash()
                            .build();
                }

                //Load subjects
                statementsToLoad.addAll(subjectStatements);

                //Load VPs
                statementsToLoad.add(phenotypeIsoStatement);

                //Load objects
                if (objectIsoStatement != null) {
                    statementsToLoad.add(objectIsoStatement);
                }
            }

            return statementsToLoad;
        }

        private String getFirstSubjectIsoformName(Set<Statement> subjects) {

            Set<String> isoforms = subjects.stream()
                    .map(s -> s.getValue(NEXTPROT_ACCESSION) + "-" + SequenceVariantUtils.getIsoformName(s.getValue(ANNOTATION_NAME)))
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
                    .filter(s -> SequenceVariantUtils.isIsoSpecific(s.getValue(ANNOTATION_NAME)))
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
