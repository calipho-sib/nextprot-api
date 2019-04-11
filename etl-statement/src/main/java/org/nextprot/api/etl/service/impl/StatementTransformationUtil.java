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
import org.nextprot.api.isoform.mapper.domain.impl.SingleFeatureQuerySuccessImpl.IsoformFeatureResult;
import org.nextprot.api.isoform.mapper.service.IsoformMappingService;
import org.nextprot.commons.constants.IsoTargetSpecificity;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementBuilder;
import org.nextprot.commons.statements.TargetIsoformSet;
import org.nextprot.commons.statements.TargetIsoformStatementPosition;
import org.nextprot.commons.statements.specs.CoreStatementField;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.nextprot.commons.statements.specs.CoreStatementField.*;

// TODO: This statics methods smell bad and should be refactored as a meaningful class
public class StatementTransformationUtil {

	private static Logger LOGGER = Logger.getLogger(StatementTransformationUtil.class);

	public static IsoformPositions computeTargetIsoformsForNormalAnnotation(Statement statement, IsoformService isoformService, IsoformMappingService isoformMappingService) {

        Optional<String> isoSpecificAccession = getOptionalIsoformAccession(statement);

        // Currently we don't create normal annotations (not associated with variant) in the bioeditor
        List<String> isoformAccessions = getIsoformAccessionsForEntryAccession(statement.getValue(ENTRY_ACCESSION), isoformService);
        IsoformPositions isoformPositions = new IsoformPositions();

        if (!isoformAccessions.isEmpty()) {
            AnnotationCategory category = AnnotationCategory.getDecamelizedAnnotationTypeName(statement.getValue(ANNOTATION_CATEGORY));

            // POSITIONAL ANNOTATIONS
            if (category.isChildOf(AnnotationCategory.POSITIONAL_ANNOTATION) && category != AnnotationCategory.PTM_INFO) {

                isoformPositions = buildTargetIsoformStatementPositions(category, statement, isoformMappingService, isoSpecificAccession);
            }

            // NON-POSITIONAL ANNOTATIONS
            else {

                Set<TargetIsoformStatementPosition> targetIsoforms = new TreeSet<>();

                for (String isoAccession : isoformAccessions) {

                    if (!isoSpecificAccession.isPresent()) { //If not present add for them all
                        targetIsoforms.add(new TargetIsoformStatementPosition(isoAccession, IsoTargetSpecificity.UNKNOWN.name(), null));
                    } else {
                        if (isoAccession.equals(isoSpecificAccession.get())) {
                            targetIsoforms.add(new TargetIsoformStatementPosition(isoAccession, IsoTargetSpecificity.SPECIFIC.name(), null));
                        }
                    }
                }

                isoformPositions.setTargetIsoformSet(new TargetIsoformSet(targetIsoforms));
            }
        }

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

    private static IsoformPositions buildTargetIsoformStatementPositions(AnnotationCategory category, Statement statement, IsoformMappingService isoformMappingService,
                                                                                            Optional<String> isoSpecificAccession) {
	    String featureType = deduceFeatureType(category);

	    if (featureType == null) {
            throw new NextProtException("Cannot build target isoform for statement "+ statement);
        }

        IsoformPositions isoformPositions = new IsoformPositions();

	    String featureName = statement.getValue(ANNOTATION_NAME);
        FeatureQueryResult result;
        IsoTargetSpecificity isoTargetSpecificity;

        if (!isoSpecificAccession.isPresent()) {
            result = isoformMappingService.propagateFeature(new SingleFeatureQuery(featureName, featureType, ""));
            isoTargetSpecificity = IsoTargetSpecificity.UNKNOWN;
        }
        else {
            result = isoformMappingService.validateFeature(new SingleFeatureQuery(featureName, featureType, ""));
            isoTargetSpecificity = IsoTargetSpecificity.SPECIFIC;
        }

        if (result.isSuccess()) {

            isoformPositions = calcIsoformPositions(statement, (FeatureQuerySuccess) result, isoTargetSpecificity);

            /*targetIsoforms.addAll(((SingleFeatureQuerySuccessImpl) result).getData().values().stream()
                    .filter(sr -> sr.isMapped())
                    .map(sr -> new TargetIsoformStatementPosition(sr.getIsoformAccession(), sr.getBeginIsoformPosition(), sr.getEndIsoformPosition(), isoTargetSpecificity.name(), sr.getIsoSpecificFeature()))
                    .collect(Collectors.toList()));*/
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

			subjectsForThisProteoform.sort((s1, s2) -> {
				int cmp = Integer.parseInt(s1.getValue(LOCATION_BEGIN)) - Integer.parseInt(s2.getValue(LOCATION_BEGIN));

				if (cmp == 0) {
					cmp = s1.getValue(VARIANT_ORIGINAL_AMINO_ACID).compareTo(s2.getValue(VARIANT_ORIGINAL_AMINO_ACID));
					if (cmp == 0) {
						return s1.getValue(VARIANT_VARIATION_AMINO_ACID).compareTo(s2.getValue(VARIANT_VARIATION_AMINO_ACID));
					}
				}
				return cmp;
			});

			for (Statement s : subjectsForThisProteoform) {
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

	// TODO: handle ptm type features propagation
	static List<Statement> transformVariantStatementsComputeMappings(IsoformMappingService isoformMappingService, Set<Statement> multipleSubjects, String nextprotAccession) {

		List<Statement> result = new ArrayList<>();

		for (Statement subject : multipleSubjects) {

			if (subject.getValue(ANNOTATION_CATEGORY).equals("variant")) {

				FeatureQueryResult featureQueryResult;
				featureQueryResult = isoformMappingService.propagateFeature(new SingleFeatureQuery(subject.getValue(ANNOTATION_NAME), "variant", nextprotAccession));
				if (featureQueryResult.isSuccess()) {
					result.add(buildStatementWithMappings(subject, (FeatureQuerySuccess) featureQueryResult));
				} else {
					FeatureQueryFailure failure = (FeatureQueryFailure) featureQueryResult;
					String message = "Failure for " + subject.getStatementId() + " " + failure.getError().getMessage();
					LOGGER.error(message);
				}
			}
			else {
				LOGGER.error("skip subject "+subject.getStatementId()+": not a variant, category="+subject.getValue(ANNOTATION_CATEGORY));
			}
		}

		if (result.size() == multipleSubjects.size()) {
			return result;
		} else {
			return new ArrayList<>(); // return an empty list
		}

	}

    static IsoformPositions calcIsoformPositions(Statement variationStatement, FeatureQuerySuccess result) {

	    return calcIsoformPositions(variationStatement, result, IsoTargetSpecificity.UNKNOWN);
    }

	/**
	 * @param variationStatement
	 *            Can be a variant or mutagenesis
	 * @param result
	 * @return
	 */
	static IsoformPositions calcIsoformPositions(Statement variationStatement, FeatureQuerySuccess result, IsoTargetSpecificity isoTargetSpecificity) {

        IsoformPositions isoformPositions = new IsoformPositions();
		TargetIsoformSet targetIsoforms = new TargetIsoformSet();

		for (IsoformFeatureResult isoformFeatureResult : result.getData().values()) {
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

    private static Statement buildStatementWithMappings(Statement variationStatement, FeatureQuerySuccess result) {

        IsoformPositions isoformPositions = calcIsoformPositions(variationStatement, result);

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

    public static class IsoformPositions {

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

        public boolean hasTargetIsoforms() {
            return targetIsoformSet != null && !targetIsoformSet.isEmpty();
        }

        public boolean hasExactPositions() {
            return beginPositionOfCanonicalOrIsoSpec != null && endPositionOfCanonicalOrIsoSpec != null
                    && masterBeginPosition != null && masterEndPosition != null;
        }

        public TargetIsoformSet getTargetIsoformSet() {
            return targetIsoformSet;
        }

        public void setTargetIsoformSet(TargetIsoformSet targetIsoformSet) {
            this.targetIsoformSet = targetIsoformSet;
        }
    }

}
