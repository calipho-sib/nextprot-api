package org.nextprot.api.etl.service.impl;

import org.apache.log4j.Logger;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.etl.domain.IsoformPositions;
import org.nextprot.api.etl.service.SimpleStatementTransformerService;
import org.nextprot.api.etl.service.StatementIsoformPositionService;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.nextprot.api.commons.constants.AnnotationCategory.PHENOTYPIC_VARIATION;
import static org.nextprot.commons.statements.specs.CoreStatementField.*;

public class SimpleStatementTransformerServiceImpl implements SimpleStatementTransformerService {

	private static Logger LOGGER = Logger.getLogger(SimpleStatementTransformerServiceImpl.class);

	@Autowired
	private StatementIsoformPositionService statementIsoformPositionService;

	@Override
	public Optional<Statement> transformStatement(Statement simpleStatement) {

		String category = simpleStatement.getValue(ANNOTATION_CATEGORY);

		if (category.equals(PHENOTYPIC_VARIATION.getDbAnnotationTypeName())) {
			throw new NextProtException("Not expecting phenotypic variation at this stage.");
		}

		IsoformPositions isoformPositions =
				statementIsoformPositionService.computeIsoformPositionsForNormalAnnotation(simpleStatement);

		if (!isoformPositions.hasTargetIsoforms()) {

			LOGGER.warn("Skipping statement " + simpleStatement.getValue(ANNOTATION_NAME) + " (source=" + simpleStatement.getValue(ASSIGNED_BY) + ")");
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

	/**
	 * Compute the locations on all isoforms of subjects statements
	 */
	// TODO: handle ptm type features propagation
	@Override
	public List<Statement> transformVariantAndMutagenesisSet(Collection<Statement> subjects) {

		List<Statement> result = new ArrayList<>();

		for (Statement subject : subjects) {

			Statement transformedStatement = buildStatementWithLocations(subject);

			if (transformedStatement != null) {

				result.add(transformedStatement);
			}
		}

		if (result.size() == subjects.size()) {
			return result;
		} else {
			return new ArrayList<>(); // return an empty list
		}
	}

	private Statement buildStatementWithLocations(Statement variationStatement) {

		IsoformPositions isoformPositions = statementIsoformPositionService.computeIsoformPositionsForNormalAnnotation(variationStatement);

		return new StatementBuilder(variationStatement)
				.addField(RAW_STATEMENT_ID, variationStatement.getStatementId()) // Keep statement
				.addField(LOCATION_BEGIN, String.valueOf(isoformPositions.getBeginPositionOfCanonicalOrIsoSpec()))
				.addField(LOCATION_END, String.valueOf(isoformPositions.getEndPositionOfCanonicalOrIsoSpec()))
				.addField(LOCATION_BEGIN_MASTER, String.valueOf(isoformPositions.getMasterBeginPosition()))
				.addField(LOCATION_END_MASTER, String.valueOf(isoformPositions.getMasterEndPosition()))
				.addField(ISOFORM_CANONICAL, isoformPositions.getCanonicalIsoform())
				.addField(TARGET_ISOFORMS, isoformPositions.getTargetIsoformSet().serializeToJsonString())
				.withAnnotationHash()
				.build();
	}
}
