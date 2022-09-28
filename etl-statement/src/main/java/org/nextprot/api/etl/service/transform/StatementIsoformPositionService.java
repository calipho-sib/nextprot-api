package org.nextprot.api.etl.service.transform;

import org.nextprot.api.etl.domain.IsoformPositions;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.TargetIsoformSet;

import java.util.List;

/**
 * Compute location of statement on isoforms
 */
public interface StatementIsoformPositionService {

	IsoformPositions computeIsoformPositionsForNormalAnnotation(Statement statement);

	TargetIsoformSet computeTargetIsoformsForProteoformAnnotation(List<Statement> transformedSubjectStatements,
	                                                              boolean isIsoSpecific, String isoSpecificAccession);

	IsoformPositions computeIsoformPositionsForVariantOrMutagenesis(Statement subject);
}
