package org.nextprot.api.etl.service.impl;

import com.google.common.collect.Sets;
import org.apache.log4j.Logger;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.app.StatementSource;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.etl.service.StatementETLService;
import org.nextprot.api.etl.service.StatementLoaderService;
import org.nextprot.api.etl.service.StatementSourceService;
import org.nextprot.api.etl.service.StatementTransformerService;
import org.nextprot.api.etl.service.preprocess.impl.StatementPreProcessServiceImpl;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.reader.BufferedJsonStatementReader;
import org.nextprot.commons.statements.reader.JsonStatementReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.BatchUpdateException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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
	private StatementPreProcessServiceImpl statementPreProcessService;

	@Value("${etl.streaming.batchSize}")
	private int batchSize;

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
		long startETL = System.currentTimeMillis();
		for( String jsonFileName : statementSourceService.getJsonFilenamesForRelease(source, release)) {
			LOGGER.info("Processing source file " + jsonFileName);
			long start = System.currentTimeMillis();
			String urlString = source.getStatementsUrl() + "/" + release + "/" + jsonFileName;
			URL  fileURL = new URL(urlString);
			BufferedJsonStatementReader bufferedJsonStatementReader = new BufferedJsonStatementReader(new InputStreamReader(fileURL.openStream()), this.batchSize);

			while(bufferedJsonStatementReader.hasStatement()) {
				List<Statement> currentStatements = bufferedJsonStatementReader.readStatements();
				report.addInfoWithElapsedTime("Read " + currentStatements.size() + " statements");
				Set<Statement> rawStatements = new HashSet<>(currentStatements);
				if(!currentStatements.isEmpty()) {
					// transform the batch of statements
					long transformStart = System.currentTimeMillis();
					Collection<Statement> mappedStatements = transformStatements(source, rawStatements, report);
					report.addInfoWithElapsedTime("Transformed " + mappedStatements.size() + " statements");
					long transformTime = (System.currentTimeMillis() - transformStart) / 1000;
					report.addInfoWithElapsedTime("Transformed " + mappedStatements.size() + " statements" + " in "+ transformTime +"ms");
					if(!mappedStatements.isEmpty()) {
						long loadStart = System.currentTimeMillis();
						loadStatements(source, rawStatements, mappedStatements, load, report, erase);
						long loadTime = (System.currentTimeMillis() - loadStart) / 1000;
						report.addInfoWithElapsedTime("Load " + mappedStatements.size() + " mapped statements in " + loadTime + " ms");
					}
				}
			}

			long processingTime = (System.currentTimeMillis() - start) / 1000;
			report.addInfoWithElapsedTime("Finished ETL for file " + jsonFileName + " in " + processingTime + " ms");
			//LOGGER.info("Finished ETL for source file " + jsonFileName + " in " + processingTime + " ms");
		}

		long etlProcessingTime = (System.currentTimeMillis() - startETL) / 1000;
		report.addInfoWithElapsedTime("Finished ETL in " + etlProcessingTime + " ms");
		//LOGGER.info("Finished ETL in " + etlProcessingTime + " ms");
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


	public Set<Statement> preTransformStatements(StatementSource source, Collection<Statement> rawStatements) {
		return this.statementPreProcessService.process(source, rawStatements);
	}

	public Collection<Statement> transformStatements(StatementSource source, Collection<Statement> rawStatements, ReportBuilder report) {

		report.addInfoWithElapsedTime("Starting transformStatements(), raw statements count:" + rawStatements.size());
		rawStatements = preTransformStatements(source, rawStatements);
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






}
