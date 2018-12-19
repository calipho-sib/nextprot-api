package org.nextprot.api.etl.service.impl;

import com.google.common.collect.Sets;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import org.apache.log4j.Logger;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.service.annotation.merge.AnnotationDescriptionParser;
import org.nextprot.api.etl.service.StatementETLService;
import org.nextprot.api.etl.service.StatementExtractorService;
import org.nextprot.api.etl.service.StatementLoaderService;
import org.nextprot.api.etl.service.StatementTransformerService;
import org.nextprot.api.rdf.service.SparqlService;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementBuilder;
import org.nextprot.commons.statements.StatementField;
import org.nextprot.commons.statements.constants.NextProtSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.nextprot.api.core.utils.IsoformUtils.findEntryAccessionFromEntryOrIsoformAccession;

@Service
public class StatementETLServiceImpl implements StatementETLService {

	private static final String ENTRY_SUFFIX_URI = "http://nextprot.org/rdf/entry/";

	@Autowired
    private MasterIdentifierService masterIdentifierService;
    @Autowired
    private StatementExtractorService statementExtractorService;
    @Autowired
    private StatementTransformerService statementTransformerService;
    @Autowired
    private StatementLoaderService statementLoadService;
	@Autowired
	private TerminologyService terminologyService;
	@Autowired
	private SparqlService sparqlService;

    @Override
    public String etlStatements(NextProtSource source, String release, boolean load) throws IOException {

        ReportBuilder report = new ReportBuilder();

        Set<Statement> rawStatements = extractStatements(source, release, report);
        report.addInfoWithElapsedTime("Finished extraction");

        //rawStatements = rawStatements.stream()
        //        .filter(rs -> rs.getValue(StatementField.ENTRY_ACCESSION).equals("NX_Q8NEV4"))
        //        .collect(Collectors.toSet());

        if (rawStatements.isEmpty()) {

            report.addWarning("ETL interruption: could not extract raw statements from " + source.name()
                    + " (release " + release + ")");

            return report.toString();
        }

	    rawStatements = preTransformStatements(source, rawStatements, report);
	    report.addInfoWithElapsedTime("Finished pre transformation treatments");

        Set<Statement> mappedStatements = transformStatements(source, rawStatements, report);
        report.addInfoWithElapsedTime("Finished transformation");

        loadStatements(source, rawStatements, mappedStatements, load, report);
        report.addInfoWithElapsedTime("Finished load");

        return report.toString();

    }

    Set<Statement> extractStatements(NextProtSource source, String release, ReportBuilder report) throws IOException {

        Set<Statement> statements = filterValidStatements(statementExtractorService.getStatementsForSource(source, release), report);
        report.addInfo("Extracting " + statements.size() + " raw statements from " + source.name() + " in " + source.getStatementsUrl());

        return statements;
    }

	Set<Statement> preTransformStatements(NextProtSource source, Set<Statement> rawStatements, ReportBuilder report) {

		return preProcess(source, report).process(rawStatements);
	}

	// TODO: preprocessing should be defined outside nextprot-api
	private PreTransformProcessor preProcess(NextProtSource source, ReportBuilder report) {

		if (source == NextProtSource.GlyConnect) {
			return new GlyConnectPreProcessor(report);
		}
		else if (source == NextProtSource.BioEditor) {
			return new BioEditorPreProcessor(report);
		}
		return stmts -> stmts;
	}

    Set<Statement> transformStatements(NextProtSource source, Set<Statement> rawStatements, ReportBuilder report) {

        Set<Statement> statements = statementTransformerService.transformStatements(source, rawStatements, report);
        report.addInfo("Transformed " + rawStatements.size() + " raw statements to " + statements.size() + " mapped statements ");

        return statements;
    }

    void loadStatements(NextProtSource source, Set<Statement> rawStatements, Set<Statement> mappedStatements, boolean load, ReportBuilder report) {

        try {

            if (load) {

                report.addInfo("Loading raw statements for source " + source + ": " + rawStatements.size());
                long start = System.currentTimeMillis();
                statementLoadService.loadRawStatementsForSource(new HashSet<>(rawStatements), source);
                report.addInfo("Finish load raw statements for source " + source + " in " + (System.currentTimeMillis() - start) / 1000 + " seconds");

                report.addInfo("Loading entry statements: " + mappedStatements.size());
                start = System.currentTimeMillis();
                statementLoadService.loadStatementsMappedToEntrySpecAnnotationsForSource(mappedStatements, source);
                report.addInfo("Finish load mapped statements for source " + source + " in " + (System.currentTimeMillis() - start) / 1000 + " seconds");

            } else {
                report.addInfo("skipping load of " + rawStatements.size() + " raw statements and " + mappedStatements.size() + " mapped statements for source " + source);
            }
        } catch (BatchUpdateException e) {
            throw new NextProtException("Failed to load in source " + source + ":" + e + ", cause=" + e.getNextException());
        } catch (SQLException e) {
            throw new NextProtException("Failed to load in source " + source + ":" + e);
        }
    }

    private Set<Statement> filterValidStatements(Set<Statement> rawStatements, ReportBuilder report) {

        Set<String> allValidEntryAccessions = masterIdentifierService.findUniqueNames();
        Set<String> statementEntryAccessions = rawStatements.stream()
                .map(statement -> extractEntryAccession(statement))
                .collect(Collectors.toSet());

        Sets.SetView<String> invalidStatementEntryAccessions = Sets.difference(statementEntryAccessions, allValidEntryAccessions);

        if (!invalidStatementEntryAccessions.isEmpty()) {

            report.addWarning("Error: skipping statements with invalid entry accessions " + invalidStatementEntryAccessions);
        }

        return rawStatements.stream()
                .filter(statement -> allValidEntryAccessions.contains(extractEntryAccession(statement)))
                .collect(Collectors.toSet());
    }

    private String extractEntryAccession(Statement statement) {

        return (statement.getValue(StatementField.ENTRY_ACCESSION) != null) ?
                statement.getValue(StatementField.ENTRY_ACCESSION) :
                findEntryAccessionFromEntryOrIsoformAccession(statement.getValue(StatementField.NEXTPROT_ACCESSION));
    }

    @Override
    public void setStatementExtractorService(StatementExtractorService statementExtractorService) {
        this.statementExtractorService = statementExtractorService;
    }

    @Override
    public void setStatementTransformerService(StatementTransformerService statementTransformerService) {
        this.statementTransformerService = statementTransformerService;
    }

    @Override
    public void setStatementLoadService(StatementLoaderService statementLoadService) {
        this.statementLoadService = statementLoadService;
    }

    /**
     * Adds synchronisation to StringBuilder
     */
    public static class ReportBuilder {

        private static final Logger LOGGER = Logger.getLogger(StatementETLServiceImpl.class);
        long start;
        private StringBuilder builder; //Needs to use buffer to guarantee synchronisation

        public ReportBuilder() {

            start = System.currentTimeMillis();
            builder = new StringBuilder();

        }

        public synchronized void addInfo(String message) {
            if (builder != null) {
                LOGGER.info(message);
                builder.append(message + "\n");
            }
        }

        public synchronized void addInfoWithElapsedTime(String message) {
            String messageWithElapsedTime = message + " " + ((System.currentTimeMillis() - start) / 1000) + " seconds from the start of ETL process.";
            addInfo(messageWithElapsedTime);
        }

        @Override
        public synchronized String toString() {
            return builder.toString();
        }

        public synchronized void addWarning(String string) {
            addInfo("WARNING - " + string);
        }
    }

	public interface PreTransformProcessor {

		Set<Statement> process(Set<Statement> statements);
	}

	private class GlyConnectPreProcessor implements PreTransformProcessor {

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

		private String buildAnnotationNameForGlyConnect(Statement statement) {

			return statement.getValue(StatementField.NEXTPROT_ACCESSION) +
					"." + statement.getValue(StatementField.ANNOT_CV_TERM_ACCESSION) +
					"_" + statement.getValue(StatementField.LOCATION_BEGIN);
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

	private class BioEditorPreProcessor implements PreTransformProcessor {

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
