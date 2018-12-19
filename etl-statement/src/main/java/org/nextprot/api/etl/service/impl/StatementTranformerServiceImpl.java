package org.nextprot.api.etl.service.impl;

import com.google.common.base.Preconditions;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import org.apache.log4j.Logger;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.service.annotation.merge.AnnotationDescriptionParser;
import org.nextprot.api.etl.service.StatementTransformerService;
import org.nextprot.api.etl.service.impl.StatementETLServiceImpl.ReportBuilder;
import org.nextprot.api.isoform.mapper.service.IsoformMappingService;
import org.nextprot.api.isoform.mapper.service.SequenceFeatureFactoryService;
import org.nextprot.api.isoform.mapper.utils.SequenceVariantUtils;
import org.nextprot.api.rdf.service.SparqlService;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementBuilder;
import org.nextprot.commons.statements.StatementField;
import org.nextprot.commons.statements.TargetIsoformSet;
import org.nextprot.commons.statements.TargetIsoformStatementPosition;
import org.nextprot.commons.statements.constants.NextProtSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StatementTranformerServiceImpl implements StatementTransformerService {

    private static Logger LOGGER = Logger.getLogger(StatementTranformerServiceImpl.class);

	private static final String ENTRY_SUFFIX_URI = "http://nextprot.org/rdf/entry/";

	@Autowired
    private IsoformService isoformService;

    @Autowired
    private IsoformMappingService isoformMappingService;

    @Autowired
    private SequenceFeatureFactoryService sequenceFeatureFactoryService;

    @Autowired
    private TerminologyService terminologyService;

	@Autowired
	private SparqlService sparqlService;

    @Override
    public Set<Statement> transformStatements(NextProtSource source, Set<Statement> rawStatements, ReportBuilder report) {

        return new StatementTransformer(preProcess(source, report).process(rawStatements), report).transform();
    }

	// TODO: preprocessing should be defined outside nextprot-api
    private PreProcessor preProcess(NextProtSource source, ReportBuilder report) {

		if (source == NextProtSource.GlyConnect) {
			return new GlyConnectPreProcessor(report);
		}
		else if (source == NextProtSource.BioEditor) {
			return new BioEditorPreProcessor(report);
		}
		return statements -> statements;
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
        private final Set<String> trackedStatementIds;

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
            trackedStatementIds = new HashSet<>();
        }

        private Set<Statement> transform() {

            Set<Statement> mappedStatements = new HashSet<>();
            trackedStatementIds.clear();

            for (Statement statement : rawStatements) {

                if (isTripletStatement(statement)) {

                    mappedStatements.addAll(transformTripletStatement(statement));
                }
                else if (!trackedStatementIds.contains(statement.getValue(StatementField.STATEMENT_ID))) {

                    transformSimpleStatement(statement).ifPresent(s -> mappedStatements.add(s));
                    trackedStatementIds.add(statement.getValue(StatementField.STATEMENT_ID));
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
            String firstSubjectEntryAccession = subjectStatement.getValue(StatementField.ENTRY_ACCESSION);
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
                    .map(statement -> statement.getValue(StatementField.STATEMENT_ID))
                    .collect(Collectors.toList()));
            trackedStatementIds.add(originalStatement.getValue(StatementField.STATEMENT_ID));
        }

        private Optional<Statement> transformSimpleStatement(Statement simpleStatement) {

            String category = simpleStatement.getValue(StatementField.ANNOTATION_CATEGORY);

            if (category.equals(AnnotationCategory.PHENOTYPIC_VARIATION.getDbAnnotationTypeName())) {
                throw new NextProtException("Not expecting phenotypic variation at this stage.");
            }

            StatementTransformationUtil.IsoformPositions isoformPositions =
                    StatementTransformationUtil.computeTargetIsoformsForNormalAnnotation(simpleStatement, isoformService, isoformMappingService);

            if (!isoformPositions.hasTargetIsoforms()) {

                LOGGER.warn("Skipping statement "+simpleStatement.getValue(StatementField.ANNOTATION_NAME) + " (source="+simpleStatement.getValue(StatementField.ASSIGNED_BY)+")");
                return Optional.empty();
            }

            StatementBuilder builder = StatementBuilder.createNew()
                    .addMap(simpleStatement)
                    .addField(StatementField.RAW_STATEMENT_ID, simpleStatement.getStatementId());

            if (isoformPositions.hasExactPositions()) {
                builder.addField(StatementField.LOCATION_BEGIN, String.valueOf(isoformPositions.getBeginPositionOfCanonicalOrIsoSpec()))
                        .addField(StatementField.LOCATION_END, String.valueOf(isoformPositions.getEndPositionOfCanonicalOrIsoSpec()))
                        .addField(StatementField.LOCATION_BEGIN_MASTER, String.valueOf(isoformPositions.getMasterBeginPosition()))
                        .addField(StatementField.LOCATION_END_MASTER, String.valueOf(isoformPositions.getMasterEndPosition()));
            }

            return Optional.of(builder
                    .addField(StatementField.ISOFORM_CANONICAL, isoformPositions.getCanonicalIsoform())
                    .addField(StatementField.TARGET_ISOFORMS, isoformPositions.getTargetIsoformSet().serializeToJsonString())
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

        private String getIsoAccession(Statement statement) {

            String featureName = statement.getValue(StatementField.ANNOTATION_NAME);
            String featureType = statement.getValue(StatementField.ANNOTATION_CATEGORY);

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

                String entryAccession = subjectStatements.get(0).getValue(StatementField.ENTRY_ACCESSION);

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

                    trackedStatementIds.add(objectStatement.getValue(StatementField.STATEMENT_ID));
                    objectIsoStatement = StatementBuilder.createNew().addMap(objectStatement)
                            .addField(StatementField.TARGET_ISOFORMS, targetIsoformsForObject)
                            .buildWithAnnotationHash();

                    phenotypeIsoStatement = StatementBuilder.createNew().addMap(originalStatement)
                            .addField(StatementField.TARGET_ISOFORMS, targetIsoformsForPhenotype)
                            .addSubjects(subjectStatements).addObject(objectIsoStatement)
                            .removeField(StatementField.STATEMENT_ID)
                            .removeField(StatementField.SUBJECT_STATEMENT_IDS)
                            .removeField(StatementField.OBJECT_STATEMENT_IDS)
                            .addField(StatementField.RAW_STATEMENT_ID, originalStatement.getStatementId())
                            .buildWithAnnotationHash();
                } else {

                    phenotypeIsoStatement = StatementBuilder.createNew().addMap(originalStatement)
                            .addField(StatementField.TARGET_ISOFORMS, targetIsoformsForPhenotype) // in case of entry
                            .addSubjects(subjectStatements)
                            .removeField(StatementField.STATEMENT_ID)
                            .removeField(StatementField.SUBJECT_STATEMENT_IDS)
                            .removeField(StatementField.OBJECT_STATEMENT_IDS)
                            .addField(StatementField.RAW_STATEMENT_ID, originalStatement.getStatementId())
                            .buildWithAnnotationHash();
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

	public interface PreProcessor {

		Set<Statement> process(Set<Statement> statements);
	}

	private class GlyConnectPreProcessor implements PreProcessor {

		private final ReportBuilder report;

		private GlyConnectPreProcessor(ReportBuilder report) {
			this.report = report;
		}

		@Override
		public Set<Statement> process(Set<Statement> statements) {

			Set<Statement> filteredStatements = filterStatements(statements);

			return setAdditionalFieldsForGlyConnectStatementsHACK(filteredStatements);
		}

		private Set<Statement> setAdditionalFieldsForGlyConnectStatementsHACK(Set<Statement> statements) {

			Set<Statement> statementSet = new HashSet<>();
			Set<Statement> invalidStatements = new HashSet<>();

			statements.forEach(rs -> {
				if (rs.getValue(StatementField.NEXTPROT_ACCESSION) != null) {
					CvTerm cvterm = terminologyService.findCvTermByAccession(rs.getValue(StatementField.ANNOT_CV_TERM_ACCESSION));

					if (cvterm == null) {
						throw new NextProtException("invalid cv term "+ rs.getValue(StatementField.ANNOT_CV_TERM_ACCESSION) + ", accession=" +
								rs.getValue(StatementField.NEXTPROT_ACCESSION) + ", ref database=GlyConnect, ref accession=" + rs.getValue(StatementField.REFERENCE_ACCESSION));
					}

					statementSet.add(new StatementBuilder()
							.addMap(rs)
							.addField(StatementField.ENTRY_ACCESSION, rs.getValue(StatementField.NEXTPROT_ACCESSION))
							.addField(StatementField.RESOURCE_TYPE, "database")
							.addField(StatementField.ANNOTATION_NAME, buildAnnotationNameForGlyConnect(rs))
							.addField(StatementField.ANNOT_DESCRIPTION, cvterm.getDescription())
							.build());
				} else {
					invalidStatements.add(rs);
				}
			});

			if (!invalidStatements.isEmpty()) {
				report.addWarning("Undefined neXtProt accessions: skipping " + invalidStatements.size() + " statements");
			}

			report.addInfo("Updating " + statementSet.size() + "/" + (statements.size()) + " GlyConnect statements: set additional fields (ENTRY_ACCESSION, RESOURCE_TYPE, ANNOTATION_NAME, ANNOT_DESCRIPTION and STATEMENT_ID)");

			return statementSet;
		}

		private String buildSparql(Set<Statement> statements) {

			String format = "(entry:%s \"%d\"^^xsd:integer)";

			String selectedPTM0528EntryPositions = statements.stream()
					.filter(statement -> statement.getValue(StatementField.ANNOT_CV_TERM_ACCESSION).equals("PTM-0528"))
					.map(statement -> String.format(format,
							statement.getValue(StatementField.NEXTPROT_ACCESSION),
							Integer.parseInt(statement.getValue(StatementField.LOCATION_BEGIN))))
					.collect(Collectors.joining("\n"));

			return "select distinct ?entry ?glypos where {\n" +
					"values (?entry ?glypos) {\n" +
					selectedPTM0528EntryPositions +
					" }\n" +
					"\n" +
					"?entry :isoform ?iso.\n" +
					"?iso :swissprotDisplayed true .\n" +
					"\n" +
					"{\n" +
					"values ?forbidtopodom {\n" +
					"cv:CVTO_0001\n" +
					"cv:CVTO_0004\n" +
					"cv:CVTO_0013\n" +
					"cv:CVTO_0015\n" +
					"cv:CVTO_0022\n" +
					"} # topo check\n" +
					"  ?iso :topology ?topodom .\n" +
					"  ?topodom :term ?forbidtopodom; :start ?topodomstart; :end ?topodomend .\n" +
					"  filter((?glypos >= ?topodomstart) && (?glypos <= ?topodomend))\n" +
					"}\n" +
					"  union\n" +
					"{\n" +
					"values ?forbiddom {\n" +
					"cv:DO-00843\n" +
					"cv:DO-00082\n" +
					"cv:DO-00096\n" +
					"cv:DO-00098\n" +
					"cv:DO-00099\n" +
					"cv:DO-00100\n" +
					"cv:DO-00127\n" +
					"cv:DO-00135\n" +
					"cv:DO-00212\n" +
					"cv:DO-00218\n" +
					"cv:DO-00224\n" +
					"cv:DO-00234\n" +
					"cv:DO-00847\n" +
					"cv:DO-00280\n" +
					"cv:DO-00282\n" +
					"cv:DO-00302\n" +
					"cv:DO-00310\n" +
					"cv:DO-00341\n" +
					"cv:DO-00343\n" +
					"cv:DO-00349\n" +
					"cv:DO-00350\n" +
					"cv:DO-00354\n" +
					"cv:DO-00376\n" +
					"cv:DO-00378\n" +
					"cv:DO-00404\n" +
					"cv:DO-00416\n" +
					"cv:DO-00418\n" +
					"cv:DO-00421\n" +
					"cv:DO-00415\n" +
					"cv:DO-00430\n" +
					"cv:DO-00462\n" +
					"cv:DO-00466\n" +
					"cv:DO-00467\n" +
					"cv:DO-00469\n" +
					"cv:DO-00477\n" +
					"cv:DO-00869\n" +
					"cv:DO-00555\n" +
					"cv:DO-00592\n" +
					"cv:DO-00602\n" +
					"cv:DO-00604\n" +
					"cv:DO-00779\n" +
					"cv:DO-00918\n" +
					"cv:DO-00943\n" +
					"cv:DO-00632\n" +
					"cv:DO-00636\n" +
					"cv:DO-00671\n" +
					"cv:DO-00691\n" +
					"cv:DO-00695\n" +
					"cv:DO-00700\n" +
					"cv:DO-00832\n" +
					"cv:DO-00741\n" +
					"cv:DO-00078\n" +
					"cv:DO-00057\n" +
					"cv:DO-00104\n" +
					"cv:DO-00144\n" +
					"cv:DO-00244\n" +
					"cv:DO-00273\n" +
					"cv:DO-00284\n" +
					"cv:DO-00387\n" +
					"cv:DO-00451\n" +
					"cv:DO-00561\n" +
					"cv:DO-00650\n" +
					"cv:DO-00658\n" +
					"cv:DO-00692\n" +
					"cv:DO-00697\n" +
					"cv:DO-00707\n" +
					"}  # domain check\n" +
					"  ?iso :domain ?dom .\n" +
					"  ?dom :term ?forbiddom; :start ?domstart; :end ?domend .\n" +
					"  filter((?glypos >= ?domstart) && (?glypos <= ?domend))\n" +
					"  }\n" +
					"  union\n" +
					" {\n" +
					"  ?iso :signalPeptide ?sigpep .\n" +
					"  ?sigpep :start ?sigtart; :end ?sigend .\n" +
					"  filter((?glypos >= ?sigtart) && (?glypos <= ?sigend))\n" +
					" }\n" +
					"  union\n" +
					" {\n" +
					"  ?iso :mitochondrialTransitPeptide ?trpep .\n" +
					"  ?trpep :start ?trtart; :end ?trend .\n" +
					"  filter((?glypos >= ?trtart) && (?glypos <= ?trend))\n" +
					" }\n" +
					"}  order by ?entry ?glypos ";
		}

		private class EntryPosition {

			private final String entry;
			private final int position;

			public EntryPosition(String entry, int position) {

				this.entry = entry;
				this.position = position;
			}

			public String getEntry() {
				return entry;
			}

			public int getPosition() {
				return position;
			}
		}

		private List<EntryPosition> execSparql(String sparql) {

			QueryExecution queryExecution = sparqlService.queryExecution(sparql);

			List<EntryPosition> results = new ArrayList<>();

			ResultSet rs = queryExecution.execSelect();

			/**
			 * This give an empty graph....
			 * Model m = rs.getResourceModel();
			 * Graph g = m.getGraph();
			 * System.err.println("The graph is" + g);
			 */

			Var x = Var.alloc("entry");
			while (rs.hasNext()) {
				Binding b = rs.nextBinding();
				Node entryNode = b.get(x);
				if (entryNode == null) {
					queryExecution.close();
					throw new NextProtException("Bind your protein result to a variable called ?entry. Example: \"?entry :classifiedWith cv:KW-0813.\"");
				} else if (entryNode.toString().indexOf(ENTRY_SUFFIX_URI) == -1) {
					queryExecution.close();
					throw new NextProtException("Any entry found in the output, however was found: " + entryNode.toString());
				}

				String entry = entryNode.toString().replace(ENTRY_SUFFIX_URI, "").trim();
				results.add(new EntryPosition(entry, 0));
			}
			queryExecution.close();

			return results;
		}

		private Set<Statement> filterStatements(Set<Statement> statements) {

			List<EntryPosition> results = execSparql(buildSparql(statements));

			return statements.stream()
					.filter(statement -> {
						EntryPosition ep = new EntryPosition(statement.getValue(StatementField.NEXTPROT_ACCESSION), Integer.parseInt(statement.getValue(StatementField.LOCATION_BEGIN)));

						return !results.contains(ep);
					})
					.collect(Collectors.toSet());
		}
	}

	private class BioEditorPreProcessor implements PreProcessor {

		private final ReportBuilder report;

		private BioEditorPreProcessor(ReportBuilder report) {
			this.report = report;
		}

		@Override
		public Set<Statement> process(Set<Statement> statements) {

			Set<Statement> updated = updateAnnotDescriptionFieldForBioEditorStatementsHACK(statements);

			return updated;
		}

		private Set<Statement> updateAnnotDescriptionFieldForBioEditorStatementsHACK(Set<Statement> statements) {

			Set<Statement> statementSet = new HashSet<>();

			statements.forEach(rs -> {
				if (rs.getValue(StatementField.ANNOT_DESCRIPTION) != null) {

					String annotDescription = rs.getValue(StatementField.ANNOT_DESCRIPTION);

					AnnotationDescriptionParser parser = new AnnotationDescriptionParser(rs.getValue(StatementField.GENE_NAME));

					try {
						statementSet.add(new StatementBuilder()
								.addMap(rs)
								.addField(StatementField.ANNOT_DESCRIPTION, parser.parse(annotDescription).format())
								.build());
					} catch (ParseException e) {

						throw new NextProtException("cannot update description for statement "+rs, e);
					}
				} else {

					statementSet.add(rs);
				}
			});

			report.addInfo("Updating " + statementSet.size() + "/" + (statements.size()) + " BioEditor statements: reformat field ANNOT_DESCRIPTION");

			return statementSet;
		}
	}
}
