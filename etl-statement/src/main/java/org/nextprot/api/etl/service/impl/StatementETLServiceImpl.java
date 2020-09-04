package org.nextprot.api.etl.service.impl;

import com.google.common.collect.Sets;
import org.apache.log4j.Logger;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.app.StatementSource;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.service.annotation.merge.AnnotationDescriptionParser;
import org.nextprot.api.core.utils.IsoformUtils;
import org.nextprot.api.etl.service.StatementETLService;
import org.nextprot.api.etl.service.StatementLoaderService;
import org.nextprot.api.etl.service.StatementSourceService;
import org.nextprot.api.etl.service.StatementTransformerService;
import org.nextprot.api.rdf.service.HttpSparqlService;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementBuilder;
import org.nextprot.commons.statements.reader.BufferedJsonStatementReader;
import org.nextprot.commons.statements.reader.JsonStatementReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.BatchUpdateException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.nextprot.api.core.utils.IsoformUtils.findEntryAccessionFromEntryOrIsoformAccession;
import static org.nextprot.commons.statements.specs.CoreStatementField.*;

/**
 * Extract all raw statements then transform them to mapped statements then load all statements to db
 *
 * Note: a better alternative is available on new repo nextprot-pipelines
 */
@Service
public class StatementETLServiceImpl implements StatementETLService {

	@Autowired
    private MasterIdentifierService masterIdentifierService;
	@Autowired
	private StatementSourceService statementSourceService;
    @Autowired
    private StatementTransformerService statementTransformerService;
    @Autowired
    private StatementLoaderService statementLoadService;
	@Autowired
	private TerminologyService terminologyService;
	@Autowired
	private HttpSparqlService httpSparqlService;

	protected static final Logger LOGGER = Logger.getLogger(StatementETLServiceImpl.class);

	@Override
    public String extractTransformLoadStatements(StatementSource source, String release, boolean load, boolean erase) throws IOException {

        ReportBuilder report = new ReportBuilder();
		float startETL = System.currentTimeMillis();
        Set<Statement> rawStatements = extractStatements(source, release, report);
        report.addInfoWithElapsedTime("Finished extraction");

        if (rawStatements.isEmpty()) {

            report.addWarning("ETL interruption: could not extract raw statements from " + source.name()
                    + " (release " + release + ")");

            return report.toString();
        }

	    Collection<Statement> mappedStatements = transformStatements(source, rawStatements, report);
        report.addInfoWithElapsedTime("Finished transformation");

        loadStatements(source, rawStatements, mappedStatements, load, report, erase);
        float etlProcessingTime = System.currentTimeMillis() - startETL;
        report.addInfoWithElapsedTime("Finished load in " + etlProcessingTime + " ms");

        return report.toString();
    }

	@Override
	public String extractTransformLoadStatementsStreaming(StatementSource source, String release, boolean load, boolean erase) throws IOException {
		ReportBuilder report = new ReportBuilder();

		// Reads the source in a streaming fashion and process transform statement by statement
		float startETL = System.currentTimeMillis();
		for( String jsonFileName : statementSourceService.getJsonFilenamesForRelease(source, release)) {
			LOGGER.info("Processing source file " + jsonFileName);
			float start = System.currentTimeMillis();
			String urlString = source.getStatementsUrl() + "/" + release + "/" + jsonFileName;
			URL  fileURL = new URL(urlString);
			BufferedJsonStatementReader bufferedJsonStatementReader = new BufferedJsonStatementReader(new InputStreamReader(fileURL.openStream()));

			while(bufferedJsonStatementReader.hasStatement()) {
				List<Statement> currentStatements = bufferedJsonStatementReader.readStatements();
				report.addInfoWithElapsedTime("Read " + currentStatements.size() + " statements");
				Set<Statement> rawStatements = new HashSet<>(currentStatements);
				if(!currentStatements.isEmpty()) {
					// transform the batch of statements
					float transformStart = System.currentTimeMillis();
					Collection<Statement> mappedStatements = transformStatements(source, rawStatements, report);
					report.addInfoWithElapsedTime("Transformed " + mappedStatements.size() + " statements");
					float transformTime = System.currentTimeMillis() - transformStart;
					LOGGER.info("Transformed " + mappedStatements.size() + " in " + transformTime + " ms");

					if(!mappedStatements.isEmpty()) {
						float loadStart = System.currentTimeMillis();
						loadStatements(source, rawStatements, mappedStatements, load, report, erase);
						report.addInfoWithElapsedTime("Load " + mappedStatements.size() + " mapped statements");
						float loadTime = System.currentTimeMillis() - loadStart;
						LOGGER.info("Loaded " + mappedStatements.size() + " in " + loadTime + " ms");
					}
				}
			}

			float processingTime = System.currentTimeMillis() - start;
			report.addInfoWithElapsedTime("Finished ETL for file " + jsonFileName + " in " + processingTime + " ms");
			LOGGER.info("Finished ETL for source file " + jsonFileName + " in " + processingTime + " ms");
		}

		float etlProcessingTime = System.currentTimeMillis() - startETL;
		report.addInfoWithElapsedTime("Finished ETL in " + etlProcessingTime + " ms");
		LOGGER.info("Finished ETL in " + etlProcessingTime + " ms");
		return report.toString();
	}

    public Set<Statement> extractStatements(StatementSource source, String release, ReportBuilder report) throws IOException {

        Set<Statement> statements = filterValidStatements(fetchAllStatements(source, release), report);
        report.addInfo("Extracting " + statements.size() + " raw statements from " + source.name() + " in " + source.getStatementsUrl());

        return statements;
    }

    private List<Statement> fetchAllStatements(StatementSource source, String release) throws IOException {

	    List<Statement> statements = new ArrayList<>();
	    for (String jsonFilename : statementSourceService.getJsonFilenamesForRelease(source, release)) {

		    JsonStatementReader reader = new JsonStatementReader(statementSourceService.getStatementsAsJsonArray(source, release, jsonFilename),
				    source.getSpecifications());
		    statements.addAll(reader.readStatements());
	    }
	    return statements;
    }

	private Set<Statement> filterValidStatements(Collection<Statement> rawStatements, ReportBuilder report) {

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

	Set<Statement> preTransformStatements(StatementSource source, Collection<Statement> rawStatements, ReportBuilder report) {

		return preProcess(source, report).process(rawStatements);
	}

	// TODO: preprocessing should be defined outside nextprot-api
	private PreTransformProcessor preProcess(StatementSource source, ReportBuilder report) {

		if (source == StatementSource.GlyConnect) {
            return new GlyConnectPreProcessor(report);
        }
        else if (source == StatementSource.BioEditor) {
            return new BioEditorPreProcessor();
        }
        else if (source == StatementSource.GnomAD) {
            return new GnomADPreProcessor();
        }
		return new StatementIdBuilder();
	}

	public Collection<Statement> transformStatements(StatementSource source, Collection<Statement> rawStatements, ReportBuilder report) {

		report.addInfoWithElapsedTime("Starting transformStatements(), raw statements count:" + rawStatements.size());
		rawStatements = preTransformStatements(source, rawStatements, report);
		report.addInfoWithElapsedTime("Finished pre transformation treatments, raw statements count:" + rawStatements.size());

		Collection<Statement> statements = statementTransformerService.transformStatements(rawStatements, report);
        report.addInfo("Transformed " + rawStatements.size() + " raw statements to " + statements.size() + " mapped statements ");

        return statements;
    }

	public void loadStatements(StatementSource source, Collection<Statement> rawStatements, Collection<Statement> mappedStatements, boolean load, ReportBuilder report, boolean erase) {

        try {
            if (load) {
                report.addInfo("Loading raw statements for source " + source + ": " + rawStatements.size());
                long start = System.currentTimeMillis();
                statementLoadService.loadRawStatementsForSource(new HashSet<>(rawStatements), source, erase);
                report.addInfo("Finish load raw statements for source " + source + " in " + (System.currentTimeMillis() - start) / 1000 + " seconds");

                report.addInfo("Loading entry statements: " + mappedStatements.size());
                start = System.currentTimeMillis();
                statementLoadService.loadStatementsMappedToEntrySpecAnnotationsForSource(mappedStatements, source, erase);
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

	public void setStatementLoadService(StatementLoaderService statementLoadService) {
		this.statementLoadService = statementLoadService;
	}

    private String extractEntryAccession(Statement statement) {

        return (statement.getValue(ENTRY_ACCESSION) != null) ?
                statement.getValue(ENTRY_ACCESSION) :
                findEntryAccessionFromEntryOrIsoformAccession(statement.getValue(NEXTPROT_ACCESSION));
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

		Set<Statement> process(Collection<Statement> statements);
	}

	private class StatementIdBuilder implements PreTransformProcessor {

		@Override
		public Set<Statement> process(Collection<Statement> statements) {

			return statements.stream()
					.map(rs -> new StatementBuilder(rs).build())
					.collect(Collectors.toSet());
		}
	}

	private class GnomADPreProcessor implements PreTransformProcessor {

		@Override
		public Set<Statement> process(Collection<Statement> statements) {

			return statements.stream()
					.filter(rs -> rs.hasField(NEXTPROT_ACCESSION.getName()))
					.map(rs -> {
						String nextprotAccession = rs.getValue(NEXTPROT_ACCESSION);
						return new StatementBuilder(rs)
								.addField(ENTRY_ACCESSION, IsoformUtils.findEntryAccessionFromEntryOrIsoformAccession(nextprotAccession))
								.addField(RESOURCE_TYPE, "database")
								.addField(REFERENCE_DATABASE, StatementSource.GnomAD.getSourceName())
								.build();
					})
					.collect(Collectors.toSet());
		}
	}
	private class GlyConnectPreProcessor implements PreTransformProcessor {

		private final ReportBuilder report;

		private GlyConnectPreProcessor(ReportBuilder report) {
			this.report = report;
		}

		@Override
		public Set<Statement> process(Collection<Statement> statements) {

			Set<Statement> filteredStatements = filterStatements(statements);

			report.addInfo("Filtering " + filteredStatements.size() +  " GlyConnect raw statements over " + statements.size());

			return setAdditionalFieldsForGlyConnectStatements(filteredStatements);
		}

		private Set<Statement> filterStatements(Collection<Statement> statements) {

			Set<EntryPosition> entryPositionsToFilterOut = fetchEntryPositionsFromSparqlQueries(
					buildSparqlQueriesToGetNGlycoEntryPositions(statements));

			return statements.stream()
					.filter(statement -> {
						EntryPosition ep = new EntryPosition(statement.getValue(NEXTPROT_ACCESSION),
								Integer.parseInt(statement.getValue(LOCATION_BEGIN)));

						return !entryPositionsToFilterOut.contains(ep);
					})
					.collect(Collectors.toSet());
		}

		private Set<Statement> setAdditionalFieldsForGlyConnectStatements(Set<Statement> statements) {

			Set<Statement> statementSet = new HashSet<>();
			Set<Statement> missingNextProtAccessionStatements = new HashSet<>();
			Set<Statement> missingCvTermAccessionStatements = new HashSet<>();

			statements.forEach(rs -> {
				String nextprotAccession = rs.getValue(NEXTPROT_ACCESSION);
				String cvTermAccession = rs.getValue(ANNOT_CV_TERM_ACCESSION);

				if (isNullOrEmptyString(nextprotAccession)) {
					missingNextProtAccessionStatements.add(rs);
				}
				else if (isNullOrEmptyString(rs.getValue(ANNOT_CV_TERM_ACCESSION))) {

					report.addWarning("skipping missing cv term accession, accession=" + nextprotAccession +
							", ref database=GlyConnect, ref accession=" + rs.getValue(REFERENCE_ACCESSION));
					missingCvTermAccessionStatements.add(rs);
				}
				else {
					CvTerm cvterm = terminologyService.findCvTermByAccession(cvTermAccession);

					if (cvterm == null) {
						throw new NextProtException("invalid cv term "+ cvTermAccession + ", accession=" +
								nextprotAccession + ", ref database=GlyConnect, ref accession=" + rs.getValue(REFERENCE_ACCESSION));
					}
					statementSet.add(new StatementBuilder(rs)
							.addField(ENTRY_ACCESSION, IsoformUtils.findEntryAccessionFromEntryOrIsoformAccession(nextprotAccession))
							.addField(RESOURCE_TYPE, "database")
							.addField(ANNOTATION_NAME, buildAnnotationNameForGlyConnect(rs))
							.addField(ANNOT_DESCRIPTION, cvterm.getDescription())
							.build());
				}
			});

			if (!missingNextProtAccessionStatements.isEmpty()) {
				report.addWarning("Undefined neXtProt accessions: skipping " + missingNextProtAccessionStatements.size() + " statements");
			}

			if (!missingCvTermAccessionStatements.isEmpty()) {
				report.addWarning("Missing cv term accessions: skipping " + missingCvTermAccessionStatements.size() + " statements");
			}

			report.addInfo("Updating " + statementSet.size() + "/" + (statements.size()) + " GlyConnect statements: set additional fields (ENTRY_ACCESSION, RESOURCE_TYPE, ANNOTATION_NAME, ANNOT_DESCRIPTION and STATEMENT_ID)");

			return statementSet;
		}

		private String buildAnnotationNameForGlyConnect(Statement statement) {

			return statement.getValue(NEXTPROT_ACCESSION) +
					"." + statement.getValue(ANNOT_CV_TERM_ACCESSION) +
					"_" + statement.getValue(LOCATION_BEGIN);
		}

		private boolean isNullOrEmptyString(String value) {

			return value == null || value.isEmpty();
		}

		private Set<String> buildSparqlQueriesToGetNGlycoEntryPositions(Collection<Statement> statements) {

			String format = "(entry:%s \"%d\"^^xsd:integer)";

			Set<String> nGlycoPTMs = new HashSet<>(Arrays.asList("PTM-0528", "PTM-0529", "PTM-0530", "PTM-0531", "PTM-0532"));

			Set<Statement> nGlycoStatements = statements.stream()
					.filter(statement -> nGlycoPTMs.contains(statement.getValue(ANNOT_CV_TERM_ACCESSION)))
					.collect(Collectors.toSet());

			List<Set<Statement>> batches = new ArrayList<>();
			int batchSize = 1000;

			Iterator<Statement> statementIterator = nGlycoStatements.iterator();
			while (statementIterator.hasNext()) {
				Set<Statement> statementSubset = new HashSet<>();
				for (int idx = 0; idx < batchSize && statementIterator.hasNext(); idx++) {
					Statement statement = statementIterator.next();
					statementSubset.add(statement);
				}
				batches.add(statementSubset);
			}

			return batches.stream()
				   .map(batch -> batch.stream()
									  .map(statement -> String.format(format,
											  statement.getValue(NEXTPROT_ACCESSION),
											  Integer.parseInt(statement.getValue(LOCATION_BEGIN))))
									  .collect(Collectors.joining("\n")))
				   .map(nGlycoEntryPositions ->
						   "select distinct ?entry ?glypos where {\n" +
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
								   "}\n" +
								   "values (?entry ?glypos) {\n" +
								   nGlycoEntryPositions +
								   "}\n" +
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
								   "\n" +
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
								   "}  order by ?entry ?glypos ")
			.collect(Collectors.toSet());
		}

		private Set<EntryPosition> fetchEntryPositionsFromSparqlQueries(Set<String> sparqlQueries) {

			Set<EntryPosition> entryPositions = new HashSet<>();
			for (String sparqlQuery : sparqlQueries) {
				HttpSparqlService.SparqlResponse response = httpSparqlService.executeSparqlQuery(sparqlQuery);

				List<String> entryList = response.mapResults("entry", HttpSparqlService.SparqlResponse.newRdfEntryConv());
				List<Integer> glyposList = response.mapResults("glypos", v -> Integer.parseInt(v));

				for (int i = 0 ; i < entryList.size(); i++) {
					entryPositions.add(new EntryPosition(entryList.get(i), glyposList.get(i)));
				}
			}
			return entryPositions;
		}

		private class EntryPosition {

			private final String entry;
			private final int position;

			private EntryPosition(String entry, int position) {

				this.entry = entry;
				this.position = position;
			}

			public String getEntry() {
				return entry;
			}

			public int getPosition() {
				return position;
			}

			@Override
			public boolean equals(Object o) {
				if (this == o) return true;
				if (o == null || getClass() != o.getClass()) return false;
				EntryPosition that = (EntryPosition) o;
				return position == that.position &&
						Objects.equals(entry, that.entry);
			}

			@Override
			public int hashCode() {
				return Objects.hash(entry, position);
			}
		}
	}

	private class BioEditorPreProcessor implements PreTransformProcessor {

		@Override
		public Set<Statement> process(Collection<Statement> statements) {

			// TODO: should moved to BioEditor code instead
			return statements.stream()
					.map(rs -> updateDescription(rs))
					.collect(Collectors.toSet());
		}
	}

	/**
	 * Update the annot description if needed then recompute the statement_id
	 */
	private Statement updateDescription(Statement statement) {

		String description = statement.getValue(ANNOT_DESCRIPTION);

		if (description != null) {

			try {
				AnnotationDescriptionParser parser = new AnnotationDescriptionParser(statement.getValue(GENE_NAME));
				String newDescription = parser.parse(description).format();

				return (description.equals(newDescription)) ? statement : new StatementBuilder(statement)
						.addField(ANNOT_DESCRIPTION, newDescription)
						.addDebugInfo("ANNOT_DESCRIPTION has changed -> STATEMENT_ID was recomputed")
						.build();
			} catch (ParseException e) {

				throw new NextProtException("cannot update description for statement " + statement, e);
			}
		}
		return statement;
	}
}
