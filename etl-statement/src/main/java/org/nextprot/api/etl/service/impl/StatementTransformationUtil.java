package org.nextprot.api.etl.service.impl;

import org.apache.log4j.Logger;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.ExceptionWithReason;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryFailure;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryResult;
import org.nextprot.api.isoform.mapper.domain.FeatureQuerySuccess;
import org.nextprot.api.isoform.mapper.domain.SingleFeatureQuery;
import org.nextprot.api.isoform.mapper.domain.impl.SingleFeatureQuerySuccessImpl;
import org.nextprot.api.isoform.mapper.domain.impl.SingleFeatureQuerySuccessImpl.IsoformFeatureResult;
import org.nextprot.api.isoform.mapper.service.IsoformMappingService;
import org.nextprot.commons.constants.IsoTargetSpecificity;
import org.nextprot.commons.statements.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.nextprot.commons.statements.StatementField.*;

// TODO: This statics methods smell bad and should be refactored as a meaningful class
public class StatementTransformationUtil {

	private static Logger LOGGER = Logger.getLogger(StatementTransformationUtil.class);

	public static TargetIsoformSet computeTargetIsoformsForNormalAnnotation(Statement statement, IsoformService isoformService, IsoformMappingService isoformMappingService) {

        Optional<String> isoSpecificAccession = getOptionalIsoformAccession(statement);

        // Currently we don't create normal annotations (not associated with variant) in the bioeditor
        Set<TargetIsoformStatementPosition> targetIsoforms = new TreeSet<>();

        List<String> isoformAccessions = getIsoformAccessionsForEntryAccession(statement.getValue(StatementField.ENTRY_ACCESSION), isoformService);

        if (!isoformAccessions.isEmpty()) {
            AnnotationCategory category = AnnotationCategory.getDecamelizedAnnotationTypeName(statement.getValue(StatementField.ANNOTATION_CATEGORY));

            // POSITIONAL ANNOTATIONS
            if (category == AnnotationCategory.MODIFIED_RESIDUE || category == AnnotationCategory.GLYCOSYLATION_SITE) {

                String featureName = statement.getValue(ANNOTATION_NAME);
                FeatureQueryResult result;
                IsoTargetSpecificity isoTargetSpecificity;

                if (!isoSpecificAccession.isPresent()) {
                    result = isoformMappingService.propagateFeature(new SingleFeatureQuery(featureName, "ptm", ""));
                    isoTargetSpecificity = IsoTargetSpecificity.UNKNOWN;
                }
                else {
                    result = isoformMappingService.validateFeature(new SingleFeatureQuery(featureName, "ptm", ""));
                    isoTargetSpecificity = IsoTargetSpecificity.SPECIFIC;
                }

                if (result.isSuccess()) {
                    targetIsoforms.addAll(((SingleFeatureQuerySuccessImpl) result).getData().values().stream()
                            .filter(sr -> sr.isMapped())
                            .map(sr -> new TargetIsoformStatementPosition(sr.getIsoformAccession(), sr.getBeginIsoformPosition(), sr.getEndIsoformPosition(), isoTargetSpecificity.name(), sr.getIsoSpecificFeature()))
                            .collect(Collectors.toList()));
                }
                else {
                    ExceptionWithReason.Reason error = ((FeatureQueryFailure) result).getError();

                    String errorMessage = "ERROR: cannot compute target isoforms: isoform=" + statement.getValue(NEXTPROT_ACCESSION) + ", statement=" +
                            statement.getValue(GENE_NAME) + " | " + statement.getValue(ANNOT_SOURCE_ACCESSION) + " | " + statement.getValue(ANNOTATION_NAME) +
                            "(format='GENE | BIOEDITOR ANNOT ACCESSION | PTM'), error=" + error.getMessage();

                    LOGGER.error(errorMessage);
                    //throw new NextProtException(errorMessage);
                }
            }
            // NON-POSITIONAL ANNOTATIONS
            else {
                for (String isoAccession : isoformAccessions) {

                    if (!isoSpecificAccession.isPresent()) { //If not present add for them all
                        targetIsoforms.add(new TargetIsoformStatementPosition(isoAccession, IsoTargetSpecificity.UNKNOWN.name(), null));
                    } else {
                        if (isoAccession.equals(isoSpecificAccession.get())) {
                            targetIsoforms.add(new TargetIsoformStatementPosition(isoAccession, IsoTargetSpecificity.SPECIFIC.name(), null));
                        }
                    }
                }
            }
        }

        return new TargetIsoformSet(targetIsoforms);
	}

	private static Optional<String> getOptionalIsoformAccession(Statement statement) {

        String accession = statement.getValue(NEXTPROT_ACCESSION);

        if (accession != null && accession.contains("-")) { //It is iso specific for example NX_P19544-4 means only specifc to iso 4
            return Optional.of(accession);
        }

        return Optional.empty();
    }

	private static List<String> getIsoformAccessionsForEntryAccession(String entryAccession, IsoformService isoformService) {
		List<Isoform> isoforms = isoformService.findIsoformsByEntryName(entryAccession);
		NPreconditions.checkNotEmpty(isoforms, "Isoforms should not be null for " + entryAccession);

		return isoforms.stream()
                .map(Isoform::getIsoformAccession)
                .collect(Collectors.toList());
	}

	public static TargetIsoformSet computeTargetIsoformsForProteoformAnnotation(List<Statement> subjectsForThisProteoform, boolean isIsoSpecific, String isoSpecificAccession, List<String> isoformAccessions) {

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

		return new TargetIsoformSet(result);

	}

	// TODO: handle ptm type features propagation
	static List<Statement> getPropagatedStatementVariantsForEntry(IsoformMappingService isoformMappingService, Set<Statement> multipleSubjects, String nextprotAccession) {

		List<Statement> result = new ArrayList<>();

		for (Statement subject : multipleSubjects) {

			FeatureQueryResult featureQueryResult;
			featureQueryResult = isoformMappingService.propagateFeature(new SingleFeatureQuery(subject.getValue(ANNOTATION_NAME), "variant", nextprotAccession));
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
	static IsoformPositions calcIsoformPositions(Statement variationStatement, FeatureQuerySuccess result) {

        IsoformPositions isoformPositions = new IsoformPositions();
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

    private static Statement mapVariationStatementToEntry(Statement variationStatement, FeatureQuerySuccess result) {

        IsoformPositions isoformPositions = calcIsoformPositions(variationStatement, result);

        return StatementBuilder.createNew()
                .addMap(variationStatement).addField(StatementField.RAW_STATEMENT_ID, variationStatement.getStatementId()) // Keep statement
                .addField(StatementField.LOCATION_BEGIN, String.valueOf(isoformPositions.getBeginPositionOfCanonicalOrIsoSpec()))
                .addField(StatementField.LOCATION_END, String.valueOf(isoformPositions.getEndPositionOfCanonicalOrIsoSpec()))
                .addField(StatementField.LOCATION_BEGIN_MASTER, String.valueOf(isoformPositions.getMasterBeginPosition()))
                .addField(StatementField.LOCATION_END_MASTER, String.valueOf(isoformPositions.getMasterEndPosition()))
                .addField(StatementField.ISOFORM_CANONICAL, isoformPositions.getCanonicalIsoform())
                .addField(StatementField.TARGET_ISOFORMS, isoformPositions.getTargetIsoformSet().serializeToJsonString())
                .buildWithAnnotationHash();
    }

    private static class IsoformPositions {

        private Integer beginPositionOfCanonicalOrIsoSpec = null;
        private Integer endPositionOfCanonicalOrIsoSpec = null;
        private Integer masterBeginPosition = null;
        private Integer masterEndPosition = null;
        private String canonicalIsoform = null;
        private TargetIsoformSet targetIsoformSet;

        public Integer getBeginPositionOfCanonicalOrIsoSpec() {
            return beginPositionOfCanonicalOrIsoSpec;
        }

        public void setBeginPositionOfCanonicalOrIsoSpec(Integer beginPositionOfCanonicalOrIsoSpec) {
            this.beginPositionOfCanonicalOrIsoSpec = beginPositionOfCanonicalOrIsoSpec;
        }

        public Integer getEndPositionOfCanonicalOrIsoSpec() {
            return endPositionOfCanonicalOrIsoSpec;
        }

        public void setEndPositionOfCanonicalOrIsoSpec(Integer endPositionOfCanonicalOrIsoSpec) {
            this.endPositionOfCanonicalOrIsoSpec = endPositionOfCanonicalOrIsoSpec;
        }

        public Integer getMasterBeginPosition() {
            return masterBeginPosition;
        }

        public void setMasterBeginPosition(Integer masterBeginPosition) {
            this.masterBeginPosition = masterBeginPosition;
        }

        public Integer getMasterEndPosition() {
            return masterEndPosition;
        }

        public void setMasterEndPosition(Integer masterEndPosition) {
            this.masterEndPosition = masterEndPosition;
        }

        public String getCanonicalIsoform() {
            return canonicalIsoform;
        }

        public void setCanonicalIsoform(String canonicalIsoform) {
            this.canonicalIsoform = canonicalIsoform;
        }

        public TargetIsoformSet getTargetIsoformSet() {
            return targetIsoformSet;
        }

        public void setTargetIsoformSet(TargetIsoformSet targetIsoformSet) {
            this.targetIsoformSet = targetIsoformSet;
        }
    }

}
