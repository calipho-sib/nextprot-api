package org.nextprot.api.etl.service.impl;

import org.apache.log4j.Logger;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.ExceptionWithReason;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.etl.domain.IsoformPositions;
import org.nextprot.api.etl.service.StatementIsoformPositionService;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryFailure;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryResult;
import org.nextprot.api.isoform.mapper.domain.FeatureQuerySuccess;
import org.nextprot.api.isoform.mapper.domain.SingleFeatureQuery;
import org.nextprot.api.isoform.mapper.domain.impl.SingleFeatureQuerySuccessImpl;
import org.nextprot.api.isoform.mapper.service.IsoformMappingService;
import org.nextprot.commons.constants.IsoTargetSpecificity;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.TargetIsoformSet;
import org.nextprot.commons.statements.TargetIsoformStatementPosition;
import org.nextprot.commons.statements.specs.CoreStatementField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.nextprot.commons.statements.specs.CoreStatementField.*;

@Service
public class StatementIsoformPositionServiceImpl implements StatementIsoformPositionService {

	private static Logger LOGGER = Logger.getLogger(StatementIsoformPositionServiceImpl.class);

	@Autowired
	private IsoformService isoformService;

	@Autowired
	private IsoformMappingService isoformMappingService;

	@Override
	public IsoformPositions computeIsoformPositionsForNormalAnnotation(Statement statement) {

		Optional<String> isoSpecificAccession = statement.getOptionalIsoformAccession();

		// Currently we don't create normal annotations (not associated with variant) in the bioeditor
		List<String> isoformAccessions = isoformService.findIsoformsByEntryName(statement.getEntryAccession()).stream()
				.map(Isoform::getIsoformAccession)
				.collect(Collectors.toList());

		IsoformPositions isoformPositions = new IsoformPositions();

		if (!isoformAccessions.isEmpty()) {
			AnnotationCategory category = AnnotationCategory.getDecamelizedAnnotationTypeName(statement.getAnnotationCategory());

			// POSITIONAL ANNOTATIONS
			if (category.isChildOf(AnnotationCategory.POSITIONAL_ANNOTATION) && category != AnnotationCategory.PTM_INFO) {

				isoformPositions = buildTargetIsoformStatementPositions(category, statement, isoSpecificAccession.isPresent());
			}

			// NON-POSITIONAL ANNOTATIONS: yes indeed, it is weird :(
			else {

				Set<TargetIsoformStatementPosition> targetIsoforms = new TreeSet<>();

				for (String isoAccession : isoformAccessions) {

					if (!isoSpecificAccession.isPresent()) { //If not present add for them all

						targetIsoforms.add(new TargetIsoformStatementPosition(isoAccession, IsoTargetSpecificity.UNKNOWN.name(), null));
					}
					else if (isoAccession.equals(isoSpecificAccession.get())) {

						targetIsoforms.add(new TargetIsoformStatementPosition(isoAccession, IsoTargetSpecificity.SPECIFIC.name(), null));
					}
				}

				isoformPositions.setTargetIsoformSet(new TargetIsoformSet(targetIsoforms));
			}
		}

		return isoformPositions;
	}

	@Override
	public TargetIsoformSet computeTargetIsoformsForProteoformAnnotation(List<Statement> transformedSubjectStatements,
	                                                                     boolean isIsoSpecific, String isoSpecificAccession) {

		String entryAccession = transformedSubjectStatements.get(0).getEntryAccession();

		List<String> isoformsToBeConsidered = (isIsoSpecific) ?
				Collections.singletonList(isoSpecificAccession) :
				isoformService.findIsoformsByEntryName(entryAccession).stream()
						.map(Isoform::getIsoformAccession)
						.collect(Collectors.toList());

		Set<TargetIsoformStatementPosition> result = new TreeSet<>();

		for (String isoformAccession : isoformsToBeConsidered) {

			String name = null;
			boolean allOk = true;

			for (Statement s : transformedSubjectStatements) {
				TargetIsoformSet targetIsoforms = TargetIsoformSet.deSerializeFromJsonString(s.getValue(CoreStatementField.TARGET_ISOFORMS));

				List<TargetIsoformStatementPosition> targetIsoformsFiltered = targetIsoforms.stream()
						.filter(ti -> ti.getIsoformAccession().equals(isoformAccession))
						.collect(Collectors.toList());

				if (targetIsoformsFiltered.isEmpty()) {
					LOGGER.debug("(skip) Could not map to isoform " + isoformAccession);
					allOk = false;
					break;
				} else if (targetIsoformsFiltered.size() > 1) {
					throw new NextProtException("Something got wrong. Found more than one target isoform for same accession" + isoformAccession);
				}

				TargetIsoformStatementPosition tisp = targetIsoformsFiltered.iterator().next();

				if (name == null) {
					name = tisp.getName();
				} else {
					name += (" + " + tisp.getName());
				}

			}

			if (name != null && allOk) {

				if (isIsoSpecific) {
					result.add(new TargetIsoformStatementPosition(isoformAccession, IsoTargetSpecificity.SPECIFIC.name(), name));
				} else {
					result.add(new TargetIsoformStatementPosition(isoformAccession, IsoTargetSpecificity.UNKNOWN.name(), name));
				}
			}
		}

		return new TargetIsoformSet(result);

	}

	@Override
	public IsoformPositions computeIsoformPositionsForVariantOrMutagenesis(Statement subject) {

		if (subject.getValue(ANNOTATION_CATEGORY).equals("variant") || subject.getValue(ANNOTATION_CATEGORY).equals("mutagenesis")) {

			FeatureQueryResult featureQueryResult = isoformMappingService.propagateFeature(
					SingleFeatureQuery.variant(subject.getValue(ANNOTATION_NAME), subject.getValue(ENTRY_ACCESSION)));

			if (featureQueryResult.isSuccess()) {
				return calcIsoformPositions(subject, (FeatureQuerySuccess) featureQueryResult);
			} else {
				String message = "Failure for " + subject.getStatementId() + " " +
						((FeatureQueryFailure) featureQueryResult).getError().getMessage();
				LOGGER.error(message);
			}
		}
		else {
			LOGGER.error("skip subject "+subject.getStatementId()+": not a variant nor a mutagenesis, category="+subject.getValue(ANNOTATION_CATEGORY));
		}
		return null;
	}

	private IsoformPositions buildTargetIsoformStatementPositions(AnnotationCategory category, Statement statement,
	                                                              boolean isoSpecific) {
		String featureType = deduceFeatureType(category);

		if (featureType == null) {
			throw new NextProtException("Cannot build target isoform for statement "+ statement);
		}

		IsoformPositions isoformPositions = new IsoformPositions();

		String featureName = statement.getValue(ANNOTATION_NAME);
		SingleFeatureQuery query = new SingleFeatureQuery(featureName, featureType, statement.getEntryAccession());
		FeatureQueryResult result;
		IsoTargetSpecificity isoTargetSpecificity;

		if (!isoSpecific) {
			result = isoformMappingService.propagateFeature(query);
			isoTargetSpecificity = IsoTargetSpecificity.UNKNOWN;
		}
		else {
			result = isoformMappingService.validateFeature(query);
			isoTargetSpecificity = IsoTargetSpecificity.SPECIFIC;
		}

		if (result.isSuccess()) {

			isoformPositions = calcIsoformPositions(statement, (FeatureQuerySuccess) result, isoTargetSpecificity);
		}
		else {
			ExceptionWithReason.Reason error = ((FeatureQueryFailure) result).getError();

			String errorMessage = "ERROR: cannot compute target isoforms: isoform=" + statement.getValue(NEXTPROT_ACCESSION) + ", statement=" +
					statement.getValue(GENE_NAME) + " | " + statement.getValue(ANNOT_SOURCE_ACCESSION) + " | " + statement.getValue(ANNOTATION_NAME) +
					"(format='GENE | BIOEDITOR ANNOT ACCESSION | PTM'), error=" + error.getMessage();

			LOGGER.error(errorMessage);
			//throw new NextProtException(errorMessage);
		}

		return isoformPositions;
	}

	private IsoformPositions calcIsoformPositions(Statement variationStatement, FeatureQuerySuccess result) {

		return calcIsoformPositions(variationStatement, result, IsoTargetSpecificity.UNKNOWN);
	}

	/**
	 * @param variationStatement
	 *            Can be a variant or mutagenesis
	 * @param result
	 * @return
	 */
	private IsoformPositions calcIsoformPositions(Statement variationStatement, FeatureQuerySuccess result, IsoTargetSpecificity isoTargetSpecificity) {

		IsoformPositions isoformPositions = new IsoformPositions();
		TargetIsoformSet targetIsoforms = new TargetIsoformSet();

		for (SingleFeatureQuerySuccessImpl.IsoformFeatureResult isoformFeatureResult : result.getData().values()) {
			if (isoformFeatureResult.isMapped()) {

				targetIsoforms.add(new TargetIsoformStatementPosition(isoformFeatureResult.getIsoformAccession(), isoformFeatureResult.getBeginIsoformPosition(),
						isoformFeatureResult.getEndIsoformPosition(), isoTargetSpecificity.name(),
						isoformFeatureResult.getIsoSpecificFeature()));

				// Will be set in case that we don't want to propagate to
				// canonical
				if (isoformPositions.getBeginPositionOfCanonicalOrIsoSpec() == null) {
					isoformPositions.setBeginPositionOfCanonicalOrIsoSpec(isoformFeatureResult.getBeginIsoformPosition());
				}
				if (isoformPositions.getEndPositionOfCanonicalOrIsoSpec() == null) {
					isoformPositions.setEndPositionOfCanonicalOrIsoSpec(isoformFeatureResult.getEndIsoformPosition());
				}

				// If possible use canonical
				if (isoformFeatureResult.isCanonical()) {
					if (isoformPositions.getCanonicalIsoform() != null) {
						throw new NextProtException("Canonical position set already");
					}
					isoformPositions.setCanonicalIsoform(isoformFeatureResult.getIsoformAccession());
					isoformPositions.setBeginPositionOfCanonicalOrIsoSpec(isoformFeatureResult.getBeginIsoformPosition());
					isoformPositions.setEndPositionOfCanonicalOrIsoSpec(isoformFeatureResult.getEndIsoformPosition());
				}

				if (isoformPositions.getMasterBeginPosition() == null) {
					isoformPositions.setMasterBeginPosition(isoformFeatureResult.getBeginMasterPosition());
				}

				if (isoformPositions.getMasterEndPosition() == null) {
					isoformPositions.setMasterEndPosition(isoformFeatureResult.getEndMasterPosition());
				}

				if (isoformPositions.getMasterBeginPosition() != null) {
					if (!isoformPositions.getMasterBeginPosition().equals(isoformFeatureResult.getBeginMasterPosition())) {
						throw new NextProtException("Begin master position " + isoformPositions.getMasterBeginPosition()
								+ " does not match " + isoformFeatureResult.getBeginMasterPosition()
								+ " for different isoforms (" + result.getData().values().size() + ") for statement " + variationStatement.getStatementId());
					}
				}

				if (isoformPositions.getMasterEndPosition() != null) {
					if (!isoformPositions.getMasterEndPosition().equals(isoformFeatureResult.getEndMasterPosition())) {
						throw new NextProtException("End master position does not match for different isoforms" + variationStatement.getStatementId());
					}
				}
			}
		}

		isoformPositions.setTargetIsoformSet(targetIsoforms);

		return isoformPositions;
	}

	private static String deduceFeatureType(AnnotationCategory category) {

		if (category == AnnotationCategory.VARIANT || category  == AnnotationCategory.MUTAGENESIS) {
			return "variant";
		}
		else if (category == AnnotationCategory.MODIFIED_RESIDUE || category == AnnotationCategory.GLYCOSYLATION_SITE) {
			return "ptm";
		}
		return null;
	}
}
