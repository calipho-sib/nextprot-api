package org.nextprot.api.etl.service.impl;

import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.etl.service.SimpleStatementTransformerService;
import org.nextprot.api.etl.service.StatementIsoformPositionService;
import org.nextprot.api.etl.service.StatementTransformerService;
import org.nextprot.api.etl.service.impl.StatementETLServiceImpl.ReportBuilder;
import org.nextprot.api.etl.service.preprocess.impl.StatementPreProcessServiceImpl;
import org.nextprot.api.isoform.mapper.service.SequenceFeatureFactoryService;
import org.nextprot.api.isoform.mapper.utils.SequenceVariantUtils;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementBuilder;
import org.nextprot.commons.statements.TargetIsoformSet;
import org.nextprot.commons.statements.TargetIsoformStatementPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.nextprot.api.commons.constants.AnnotationCategory.INTERACTION_MAPPING;
import static org.nextprot.api.commons.constants.AnnotationCategory.PHENOTYPIC_VARIATION;
import static org.nextprot.commons.statements.specs.CoreStatementField.*;

@Service
public class StatementTransformerServiceImpl implements StatementTransformerService {

	protected final Logger LOGGER = Logger.getLogger(StatementTransformerServiceImpl.class);

	@Autowired
	private SequenceFeatureFactoryService sequenceFeatureFactoryService;

	@Autowired
	private SimpleStatementTransformerService simpleStatementTransformerService;

	@Autowired
	private StatementIsoformPositionService statementIsoformPositionService;

	@Override
	public Collection<Statement> transformStatements(Collection<Statement> rawStatements, ReportBuilder report) {

		return new StatementTransformer(rawStatements, report).transform();
	}

	class StatementTransformer {

		private final Collection<Statement> rawStatements;
		private final ReportBuilder report;
		private final Map<String, Statement> rawStatementsById;
		private final Set<String> trackedRawStatementIds;

		StatementTransformer(Collection<Statement> rawStatements, ReportBuilder report) {

			Preconditions.checkNotNull(rawStatements);
			Preconditions.checkNotNull(report);

			if (rawStatements.isEmpty()) {
				throw new NextProtException("missing raw statements");
			}
			this.rawStatements = rawStatements;
			this.report = report;
			this.rawStatementsById = rawStatements.stream()
					.collect(Collectors.toMap(Statement::getStatementId, Function.identity()));
			trackedRawStatementIds = new HashSet<>();
		}

		private Set<Statement> transform() {

			Set<Statement> mappedStatements = new HashSet<>();
			trackedRawStatementIds.clear();

			int cpt = 0;
			for (Statement rawStatement : rawStatements) {
				LOGGER.info("Transforming statement " + rawStatement.toJsonString());
				cpt++;
				//System.out.println("Handling rawStatement " + cpt + " / " + this.rawStatements.size());
				if (isPhenotypicVariation(rawStatement)) {
					mappedStatements.addAll(transformPhenotypicVariationStatement(rawStatement));
				} else if (!trackedRawStatementIds.contains(rawStatement.getStatementId())) { // single
					simpleStatementTransformerService.transformStatement(rawStatement).ifPresent(s -> mappedStatements.add(s));
					trackedRawStatementIds.add(rawStatement.getStatementId());
				}
			}

			return mappedStatements;
		}

		/**
		 * <h3> Phenotypic variation stmt</h3>
		 *
		 * <h3>Example</h3>
		 * MSH6-p.Ser144Ile decreases mismatch repair (BED CAVA-VP011468)
		 * <p>
		 * The sentence above has 3 stmts:
		 * 1. stmt SUBJECT(s) (ex: VARIANT: MSH6-p.Ser144Ile)
		 * 2. stmt OBJECT (ex: GO: mismatch repair)
		 * 3. a stmt VERB, ANNOT_CV_TERM (ex: stmt 1. decreases stmt 2.)
		 **/
		private Set<Statement> transformPhenotypicVariationStatement(Statement rawPhenotypicVariationStatement) {

			Set<Statement> rawStatementSubjects = getRawStatementSubjects(rawPhenotypicVariationStatement.getSubjectStatementIdsArray());
			if (rawStatementSubjects == null || rawStatementSubjects.isEmpty()) {
				throw new NextProtException("missing subject statement in phenotypic-variation statement "+rawPhenotypicVariationStatement);
			}
			Statement rawStatementObject = rawStatementsById.get(rawPhenotypicVariationStatement.getObjectStatementId());
			if (rawStatementObject == null) {
				throw new NextProtException("missing object statement in phenotypic-variation statement "+rawPhenotypicVariationStatement);
			}

			String isoformSpecificAccession = null;
			boolean isIsoSpecific = isSubjectsIsoSpecific(rawStatementSubjects);

			if (isIsoSpecific) {
				isoformSpecificAccession = getIsoAccession(rawStatementSubjects.iterator().next());
			}

			// FIXME: deduce isIsoSpecific from isoAccession existence
			return transformPhenotypicVariationStatement(rawPhenotypicVariationStatement, rawStatementSubjects, isIsoSpecific, isoformSpecificAccession);
		}

		private void trackStatementId(String statementId) {

			trackedRawStatementIds.add(statementId);
		}

		private Set<Statement> getRawStatementSubjects(String[] subjectIds) {

			Set<Statement> variants = new HashSet<>();
			for (String subjectId : subjectIds) {
				Statement subjectStatement = rawStatementsById.get(subjectId);
				if (subjectStatement == null) {
					throw new NextProtException("Subject " + subjectId + " not present in the given list");
				}
				variants.add(subjectStatement);
			}

			checkSubjects(variants);

			return variants;
		}

		// FIXME: SequenceVariantUtils.getIsoformName gives wrong results
		private String checkSubjects(Set<Statement> subjects) {

			Set<String> isoforms = subjects.stream()
					.map(s -> {
						try {
							return sequenceFeatureFactoryService.newSequenceFeature(s.getValue(ANNOTATION_NAME),
									s.getAnnotationCategory()).getIsoform().getIsoformAccession();
						} catch (Exception e) {
							throw new NextProtException(e.getMessage());
						}
					})
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


		private boolean isPhenotypicVariation(Statement statement) {

			return statement.getValue(ANNOTATION_CATEGORY).equals(PHENOTYPIC_VARIATION.getDbAnnotationTypeName());
		}

		private boolean isInteractionMapping(Statement statement) {
		    return statement.getValue(ANNOTATION_CATEGORY).equals(INTERACTION_MAPPING.getDbAnnotationTypeName());
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

		private Set<Statement> transformPhenotypicVariationStatement(Statement rawPhenotypicVariationStatement, Set<Statement> subjectStatementSet,
		                                                 boolean isIsoSpecific, String isoSpecificAccession) {
			Set<Statement> statementsToLoad = new HashSet<>();

			// 1. transform subjects: add mapping infos on each subjects
			List<Statement> transformedSubjectStatements = transformSubjects(subjectStatementSet);

			if (transformedSubjectStatements.isEmpty()) {
				report.addWarning("Empty subjects are not allowed for " + rawPhenotypicVariationStatement.getValue(ENTRY_ACCESSION) + " skipping... case for 1 variant");
			}

			TargetIsoformSet targetIsoformsSetForPhenotypicVariationStatement =
					statementIsoformPositionService.computeTargetIsoformsForProteoformAnnotation(transformedSubjectStatements, isIsoSpecific, isoSpecificAccession);

			// 2. transform object
			Statement objectStatement = transformObject(rawPhenotypicVariationStatement, isIsoSpecific, targetIsoformsSetForPhenotypicVariationStatement);

			Statement phenotypeVariationStatement = new StatementBuilder(rawPhenotypicVariationStatement)
					.addField(TARGET_ISOFORMS, targetIsoformsSetForPhenotypicVariationStatement.serializeToJsonString())
					.addSubjects(transformedSubjectStatements)
					.addObject(objectStatement)
					.removeField(STATEMENT_ID)
					.removeField(SUBJECT_STATEMENT_IDS)
					.removeField(OBJECT_STATEMENT_IDS)
					.addField(RAW_STATEMENT_ID, rawPhenotypicVariationStatement.getStatementId())
					.withAnnotationHash()
					.build();

			//add VPs
			statementsToLoad.add(phenotypeVariationStatement);
			trackStatementId(phenotypeVariationStatement.getValue(RAW_STATEMENT_ID));

			//add object statement
			if (!trackedRawStatementIds.contains(objectStatement.getValue(RAW_STATEMENT_ID))) {
				statementsToLoad.add(objectStatement);
				trackStatementId(objectStatement.getValue(RAW_STATEMENT_ID));
			}

			//add subject statements
			transformedSubjectStatements.stream()
					.filter(subjectStatement -> !trackedRawStatementIds.contains(subjectStatement.getValue(RAW_STATEMENT_ID)))
					.forEach(subjectStatement -> {
						statementsToLoad.add(subjectStatement);
						trackStatementId(subjectStatement.getValue(RAW_STATEMENT_ID));
					});

			return statementsToLoad;
		}

		private Statement transformObject(Statement originalStatement, boolean isIsoSpecific, TargetIsoformSet targetIsoformsSetForPhenotypicVariationStatement) {

			Statement originalObjectStatement = rawStatementsById.get(originalStatement.getObjectStatementId());

			TargetIsoformSet targetIsoformsForObjectStatement =
					computeTargetIsoformForObject(isIsoSpecific, originalObjectStatement, targetIsoformsSetForPhenotypicVariationStatement);

			return new StatementBuilder(originalObjectStatement)
					.addField(TARGET_ISOFORMS, targetIsoformsForObjectStatement.serializeToJsonString())
					.addField(RAW_STATEMENT_ID, originalObjectStatement.getStatementId())
					.withAnnotationHash()
					.build();
		}

		private List<Statement> transformSubjects(Set<Statement> subjectStatementSet) {

			List<Statement> transformedSubjectStatements =
					simpleStatementTransformerService.transformSubjects(subjectStatementSet);

			if (transformedSubjectStatements.isEmpty()) {
				return transformedSubjectStatements;
			}

			return transformedSubjectStatements.stream()
					.sorted((s1, s2) -> {
						int cmp = Integer.parseInt(s1.getValue(LOCATION_BEGIN)) - Integer.parseInt(s2.getValue(LOCATION_BEGIN));

						if (cmp == 0) {
							cmp = s1.getValue(VARIANT_ORIGINAL_AMINO_ACID).compareTo(s2.getValue(VARIANT_ORIGINAL_AMINO_ACID));
							if (cmp == 0) {
								return s1.getValue(VARIANT_VARIATION_AMINO_ACID).compareTo(s2.getValue(VARIANT_VARIATION_AMINO_ACID));
							}
						}
						return cmp;
					})
					.collect(Collectors.toList());
		}

		private TargetIsoformSet computeTargetIsoformForObject(boolean isIsoSpecific, Statement originalObjectStatement, TargetIsoformSet targetIsoformsSetOfPhenotypicVariation) {
			TargetIsoformSet targetIsoformsForObject;

			if (isIsoSpecific) {//If it is iso specific
				Set<TargetIsoformStatementPosition> targetIsoformsForObjectSet = new TreeSet<>();

				for (TargetIsoformStatementPosition tisp : targetIsoformsSetOfPhenotypicVariation) {
					targetIsoformsForObjectSet.add(new TargetIsoformStatementPosition(tisp.getIsoformAccession(), tisp.getSpecificity(), null));
				}
				targetIsoformsForObject = new TargetIsoformSet(targetIsoformsForObjectSet);
			} else {
				targetIsoformsForObject = statementIsoformPositionService.computeIsoformPositionsForNormalAnnotation(originalObjectStatement)
						.getTargetIsoformSet();
			}
			return targetIsoformsForObject;
		}

		// FIXME: this return bad results because SequenceVariantUtils.isIsoSpecific is wrong
		private boolean isSubjectsIsoSpecific(Set<Statement> subjects) {

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
