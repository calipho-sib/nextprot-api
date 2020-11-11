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
		
        // before doing anything, delete old statements for source if requested to do so
        if (erase) {
            report.addInfoWithElapsedTime("Starting deletion of statements of " + source.getSourceName());
	        statementLoadService.deleteRawStatements(source);
	        statementLoadService.deleteEntryMappedStatements(source);
            report.addInfoWithElapsedTime("Completed deletion of statements of " + source.getSourceName());
        }
		
        Set<Statement> rawStatements = extractStatements(source, release, report);
        report.addInfoWithElapsedTime("Finished extraction");

        if (rawStatements.isEmpty()) {

            report.addWarning("ETL interruption: could not extract raw statements from " + source.name()
                    + " (release " + release + ")");

            return report.toString();
        }

	    Collection<Statement> mappedStatements = transformStatements(source, rawStatements, report);
        if(mappedStatements == null) {
        	LOGGER.debug("Empyt mapped statements");
        	return report.toString();
		}
        report.addInfoWithElapsedTime("Finished transformation");

        loadStatements(source, rawStatements, mappedStatements, load, report);
        float etlProcessingTime = System.currentTimeMillis() - startETL;
        report.addInfoWithElapsedTime("Finished load in " + etlProcessingTime + " ms");

        return report.toString();
    }


	@Override
	public String extractTransformLoadStatementsStreaming(StatementSource source, String release, boolean load, boolean erase, boolean dropIndex) throws IOException {
		ReportBuilder report = new ReportBuilder();
		long startETL = System.currentTimeMillis();

		// Should drop the indexes
		List<String> indexDefinitions = null;
		if(dropIndex) {
			LOGGER.info("Dropping the indexes of the raw and entry mapped tables");
			indexDefinitions = statementLoadService.dropIndexes();
			LOGGER.info("Dropped indexes:  " + indexDefinitions.size());
		}

        // before doing anything, delete old statements for source if requested to do so
        if (erase) {
            report.addInfoWithElapsedTime("Starting deletion of statements of " + source.getSourceName());
	        statementLoadService.deleteRawStatements(source);
	        statementLoadService.deleteEntryMappedStatements(source);
            report.addInfoWithElapsedTime("Completed deletion of statements of " + source.getSourceName());
        }		
		
		// Reads the source in a streaming fashion and process transform statement by statement
        int stmtProcessedSoFar = 0;
		for( String jsonFileName : statementSourceService.getJsonFilenamesForRelease(source, release)) {
			LOGGER.info("Processing source file " + jsonFileName);
			long start = System.currentTimeMillis();
			String urlString = source.getStatementsUrl() + "/" + release + "/" + jsonFileName;
			URL  fileURL = new URL(urlString);
			BufferedJsonStatementReader bufferedJsonStatementReader = new BufferedJsonStatementReader(new InputStreamReader(fileURL.openStream()), this.batchSize);

			while(bufferedJsonStatementReader.hasStatement()) {
				List<Statement> currentStatements = bufferedJsonStatementReader.readStatements();
				report.addInfoWithElapsedTime("step: extract, read_statements: " + currentStatements.size() + ", time:  -");
				Set<Statement> rawStatements = new HashSet<>(currentStatements);
				report.addInfoWithElapsedTime("step: extract, raw_statements: " + rawStatements.size() + ", time:  -");
				if (!currentStatements.isEmpty()) {
					// transform the batch of statements
					long transformStart = System.currentTimeMillis();
					Collection<Statement> mappedStatements = transformStatements(source, rawStatements, report);
					long transformTime = (System.currentTimeMillis() - transformStart) / 1000;
					if(mappedStatements == null) {
						report.addInfoWithElapsedTime("'step' : transform, statements: 0, time:" + transformTime);
						// Still need to store the raw statements
						long loadStart = System.currentTimeMillis();
						loadStatements(source, currentStatements, null, load, report);
						long loadTime = (System.currentTimeMillis() - loadStart) / 1000;
						report.addInfoWithElapsedTime("'step' : load , mappedStatements: rowStatements: " + currentStatements.size() + ", time: " + loadTime);
					} else {
						report.addInfoWithElapsedTime("'step' : transform, statements: " + mappedStatements.size() + ", time:" + transformTime);
						if(!mappedStatements.isEmpty()) {
							long loadStart = System.currentTimeMillis();
							loadStatements(source, currentStatements, mappedStatements, load, report);
							long loadTime = (System.currentTimeMillis() - loadStart) / 1000;
							report.addInfoWithElapsedTime("'step' : load , mappedStatements: " + mappedStatements.size() + ", rowStatements: " + currentStatements.size() + ", time: " + loadTime);
						}					
					}
				}
				stmtProcessedSoFar += currentStatements.size();
				report.addInfoWithElapsedTime("'step' : status, statements processed so far: " + stmtProcessedSoFar);
			}
			bufferedJsonStatementReader.close();
			long processingTime = (System.currentTimeMillis() - start) / 1000;
			report.addInfoWithElapsedTime("{ 'File' : " + jsonFileName + " , 'time' : " + processingTime);
		}

		long etlProcessingTime = (System.currentTimeMillis() - startETL) / 1000;
		report.addInfoWithElapsedTime("{ 'Step' : Done, 'Time' : " + etlProcessingTime);

		// Recreates the dropped indexes from the index definitions
		if(dropIndex) {
			LOGGER.info("Re-create indexes : " + indexDefinitions.size());
			long startIndexCreate = System.currentTimeMillis();
			statementLoadService.createIndexes(indexDefinitions);
			long indexCreationTime = (System.currentTimeMillis() - startIndexCreate) / 1000;
			report.addInfoWithElapsedTime("{ 'Step' : Reindexed, 'Time' : " + indexCreationTime);
		}
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

		    JsonStatementReader reader = new JsonStatementReader(
		    		statementSourceService.getStatementsAsJsonArray(source, release, jsonFilename),
				    source.getSpecifications());
		    statements.addAll(reader.readStatements());
		    reader.close();
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
		Collection<Statement> preTransformStatements = preTransformStatements(source, rawStatements);
		if(preTransformStatements.isEmpty()) {
			LOGGER.debug("Raw statements are empty");
			report.addInfoWithElapsedTime("Pre-transformation returned empty");
			return null;
		}

		report.addInfoWithElapsedTime("Finished pre transformation, raw statements count:" + preTransformStatements.size());
		Collection<Statement> statements = statementTransformerService.transformStatements(preTransformStatements, report);
        report.addInfo("Transformed " + preTransformStatements.size() + " raw statements to " + statements.size() + " mapped statements ");

        return statements;
    }

	public void loadStatements(StatementSource source, Collection<Statement> rawStatements, Collection<Statement> mappedStatements, boolean load, ReportBuilder report) {

        try {
            if (load) {
            	if(rawStatements != null && rawStatements.size() > 0) {
					report.addInfo("Loading raw statements for source " + source + ": " + rawStatements.size());
					long start = System.currentTimeMillis();
					statementLoadService.loadRawStatementsForSource(new HashSet<>(rawStatements), source);
					report.addInfo("Finish load raw statements for source " + source + " in " + (System.currentTimeMillis() - start) / 1000 + " seconds");
				}

				if(mappedStatements != null && mappedStatements.size() > 0) {
					report.addInfo("Loading entry statements: " + mappedStatements.size());
					long start = System.currentTimeMillis();
					statementLoadService.loadEntryMappedStatementsForSource(mappedStatements, source);
					report.addInfo("Finish load mapped statements for source " + source + " in " + (System.currentTimeMillis() - start) / 1000 + " seconds");
				}
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

		Collection<Statement> process(Collection<Statement> statements);
	}






}
