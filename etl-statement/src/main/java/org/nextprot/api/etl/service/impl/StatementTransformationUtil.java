package org.nextprot.api.etl.service.impl;

import org.apache.log4j.Logger;
import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryFailure;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryResult;
import org.nextprot.api.isoform.mapper.domain.FeatureQuerySuccess;
import org.nextprot.api.isoform.mapper.domain.SingleFeatureQuery;
import org.nextprot.api.isoform.mapper.domain.impl.SingleFeatureQuerySuccessImpl.IsoformFeatureResult;
import org.nextprot.api.isoform.mapper.service.IsoformMappingService;
import org.nextprot.commons.constants.IsoTargetSpecificity;
import org.nextprot.commons.statements.*;

import java.util.*;
import java.util.stream.Collectors;


public class StatementTransformationUtil {

	private static Logger LOGGER = Logger.getLogger(StatementTransformationUtil.class);

	/**
	 *
	 * @param entryAccession
	 * @param isoformService
	 * @param isoSpecific
	 * @return
	 */
	public static TargetIsoformSet computeTargetIsoformsForNormalAnnotation(String entryAccession, IsoformService isoformService, Optional<String> isoSpecific) {

		List<String> isoformAccessions = getIsoformAccessionsForEntryAccession(entryAccession, isoformService);

		// Currently we don't create normal annotations (not associated with vvariant) in the bioeditor
		Set<TargetIsoformStatementPosition> targetIsoforms = new TreeSet<>();
		for (String isoAccession : isoformAccessions) {

			if(!isoSpecific.isPresent()) { //If not present add for them all
				targetIsoforms.add(new TargetIsoformStatementPosition(isoAccession, IsoTargetSpecificity.UNKNOWN.name(), null));
			}else {
				String specificIsoformAccession = isoSpecific.get();
				if(isoAccession.equals(specificIsoformAccession)){
					targetIsoforms.add(new TargetIsoformStatementPosition(isoAccession, IsoTargetSpecificity.SPECIFIC.name(), null));
				}

			}
		}
		return new TargetIsoformSet(targetIsoforms);

	}

	private static List<String> getIsoformAccessionsForEntryAccession(String entryAccession, IsoformService isoformService) {
		List<Isoform> isoforms = isoformService.findIsoformsByEntryName(entryAccession);
		NPreconditions.checkNotEmpty(isoforms, "Isoforms should not be null for " + entryAccession);
		return isoforms.stream().map(Isoform::getIsoformAccession).collect(Collectors.toList());
	}

	public static TargetIsoformSet computeTargetIsoformsForProteoformAnnotation(Statement proteoformStatement, IsoformMappingService isoformMappingService,
			List<Statement> subjectsForThisProteoform, boolean isIsoSpecific, String isoSpecificAccession, List<String> isoformAccessions) {

		List<String> isoformsToBeConsidered = new ArrayList<>();

		if (isIsoSpecific) {
			isoformsToBeConsidered.add(isoSpecificAccession);
		} else {
			isoformsToBeConsidered.addAll(isoformAccessions);
		}


		Set<TargetIsoformStatementPosition> result = new TreeSet<>();

		for (String isoformAccession : isoformsToBeConsidered) {

			String name = null;
			boolean allOk = true;

			for (Statement s : subjectsForThisProteoform) {
				TargetIsoformSet targetIsoforms = TargetIsoformSet.deSerializeFromJsonString(s.getValue(StatementField.TARGET_ISOFORMS));
				List<TargetIsoformStatementPosition> targetIsoformsFiltered = targetIsoforms.stream().filter(ti -> ti.getIsoformAccession().equals(isoformAccession)).collect(Collectors.toList());

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

		// targetIsoformsForObject =
		// TargetIsoformUtils.getTargetIsoformForObjectSerialized(subject,
		// isoformNames);

		return new TargetIsoformSet(result);

	}

	static List<Statement> getPropagatedStatementsForEntry(IsoformMappingService isoformMappingService, Set<Statement> multipleSubjects, String nextprotAccession) {

		List<Statement> result = new ArrayList<>();

		for (Statement subject : multipleSubjects) {

			FeatureQueryResult featureQueryResult;
			featureQueryResult = isoformMappingService.propagateFeature(new SingleFeatureQuery(subject.getValue(StatementField.ANNOTATION_NAME), "variant", nextprotAccession));
			if (featureQueryResult.isSuccess()) {
				result.add(mapVariationStatementToEntry(subject, (FeatureQuerySuccess) featureQueryResult));
			} else {
				FeatureQueryFailure failure = (FeatureQueryFailure) featureQueryResult;
				String message = "Failure for " + subject.getStatementId() + " " + failure.getError().getMessage();
				LOGGER.error(message);
			}
		}

		if (result.size() == multipleSubjects.size()) {
			return result;
		} else {
			return new ArrayList<>(); // return an empty list
		}

	}

	/**
	 * @param variationStatement
	 *            Can be a variant or mutagenesis
	 * @param result
	 * @return
	 */
	static Statement mapVariationStatementToEntry(Statement variationStatement, FeatureQuerySuccess result) {

		String beginPositionOfCanonicalOrIsoSpec = null;
		String endPositionOfCanonicalOrIsoSpec = null;

		String masterBeginPosition = null;
		String masterEndPosition = null;

		String isoCanonical = null;

		TargetIsoformSet targetIsoforms = new TargetIsoformSet();

		for (IsoformFeatureResult isoformFeatureResult : result.getData().values()) {
			if (isoformFeatureResult.isMapped()) {

				targetIsoforms.add(new TargetIsoformStatementPosition(isoformFeatureResult.getIsoformAccession(), isoformFeatureResult.getBeginIsoformPosition(),
						isoformFeatureResult.getEndIsoformPosition(), IsoTargetSpecificity.UNKNOWN.name(), // Target
																												// by
																												// default
																												// to
																												// all
																												// variations
																												// (the
																												// subject
																												// is
																												// always
																												// propagated)
						isoformFeatureResult.getIsoSpecificFeature()));

				// Will be set in case that we don't want to propagate to
				// canonical
				if (beginPositionOfCanonicalOrIsoSpec == null) {
					beginPositionOfCanonicalOrIsoSpec = String.valueOf(isoformFeatureResult.getBeginIsoformPosition());
				}
				if (endPositionOfCanonicalOrIsoSpec == null) {
					endPositionOfCanonicalOrIsoSpec = String.valueOf(isoformFeatureResult.getEndIsoformPosition());
				}

				// If possible use canonical
				if (isoformFeatureResult.isCanonical()) {
					if (isoCanonical != null) {
						throw new NextProtException("Canonical position set already");
					}
					isoCanonical = isoformFeatureResult.getIsoformAccession();
					beginPositionOfCanonicalOrIsoSpec = String.valueOf(isoformFeatureResult.getBeginIsoformPosition());
					endPositionOfCanonicalOrIsoSpec = String.valueOf(isoformFeatureResult.getEndIsoformPosition());
				}

				if (masterBeginPosition == null) {
					masterBeginPosition = String.valueOf(isoformFeatureResult.getBeginMasterPosition());
				}

				if (masterEndPosition == null) {
					masterEndPosition = String.valueOf(isoformFeatureResult.getEndMasterPosition());
				}

				if (masterBeginPosition != null) {
					if (!masterBeginPosition.equals(String.valueOf(isoformFeatureResult.getBeginMasterPosition()))) {
						throw new NextProtException("Begin master position " + masterBeginPosition + " does not match " + String.valueOf(isoformFeatureResult.getBeginMasterPosition()
								+ " for different isoforms (" + result.getData().values().size() + ") for statement " + variationStatement.getStatementId()));
					}
				}

				if (masterEndPosition != null) {
					if (!masterEndPosition.equals(String.valueOf(isoformFeatureResult.getEndMasterPosition()))) {
						throw new NextProtException("End master position does not match for different isoforms" + variationStatement.getStatementId());
					}
				}

			}

		}

		Statement rs = StatementBuilder.createNew().addMap(variationStatement).addField(StatementField.RAW_STATEMENT_ID, variationStatement.getStatementId()) // Keep
																																								// statement
				.addField(StatementField.LOCATION_BEGIN, beginPositionOfCanonicalOrIsoSpec).addField(StatementField.LOCATION_END, endPositionOfCanonicalOrIsoSpec)
				.addField(StatementField.LOCATION_BEGIN_MASTER, masterBeginPosition).addField(StatementField.LOCATION_END_MASTER, masterEndPosition)
				.addField(StatementField.ISOFORM_CANONICAL, isoCanonical).addField(StatementField.TARGET_ISOFORMS, targetIsoforms.serializeToJsonString())
				.buildWithAnnotationHash();

		return rs;
	}

}
