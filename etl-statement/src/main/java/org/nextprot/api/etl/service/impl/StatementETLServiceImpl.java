package org.nextprot.api.etl.service.impl;

import com.google.common.collect.Sets;
import org.apache.log4j.Logger;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.etl.service.StatementETLService;
import org.nextprot.api.etl.service.StatementExtractorService;
import org.nextprot.api.etl.service.StatementLoaderService;
import org.nextprot.api.etl.service.StatementTransformerService;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementField;
import org.nextprot.commons.statements.constants.NextProtSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.nextprot.api.core.utils.IsoformUtils.findEntryAccessionFromEntryOrIsoformAccession;

@Service
public class StatementETLServiceImpl implements StatementETLService {

    @Autowired private MasterIdentifierService masterIdentifierService;
	@Autowired private StatementExtractorService statementExtractorService;
	@Autowired private StatementTransformerService statementTransformerService;
	@Autowired private StatementLoaderService statementLoadService;

	@Override
	public String etlStatements(NextProtSource source, String release, boolean load) {

		ReportBuilder report = new ReportBuilder();
		
		Set<Statement> rawStatements = extractStatements(source, release, report);
		report.addInfoWithElapsedTime("Finished extraction");

        if (rawStatements.isEmpty()) {

            report.addWarning("ETL interruption: could not extract raw statements from " + source.name()
                    + " (release " + release + ")");

            return report.toString();
        }

        Set<Statement> mappedStatements = transformStatements(source, rawStatements, report);
		report.addInfoWithElapsedTime("Finished transformation");

		loadStatements(source, rawStatements, mappedStatements, load, report);
		report.addInfoWithElapsedTime("Finished load");

		return report.toString();
		
	}

    Set<Statement> extractStatements(NextProtSource source, String release, ReportBuilder report) {

        Set<Statement> statements = filterValidStatements(statementExtractorService.getStatementsForSource(source, release), report);
        report.addInfo("Extracting " + statements.size() + " raw statements from " + source.name() + " in " + source.getStatementsUrl());

        return statements;
    }

    Set<Statement> transformStatements(NextProtSource source, Set<Statement> rawStatements, ReportBuilder report) {

        Set<Statement> statements =  statementTransformerService.transformStatements(source, rawStatements, report);
        report.addInfo("Transformed " + rawStatements.size() + " raw statements to " + statements.size() + " mapped statements ");

        return statements;
    }

    void loadStatements(NextProtSource source, Set<Statement> rawStatements, Set<Statement> mappedStatements, boolean load, ReportBuilder report) {

        try {

            if(load){

                report.addInfo("Loading raw statements for source " + source + ": " + rawStatements.size());
                long start = System.currentTimeMillis();
                statementLoadService.loadRawStatementsForSource(new HashSet<>(rawStatements), source);
                report.addInfo("Finish load raw statements for source "+ source +" in " + (System.currentTimeMillis() - start)/1000 + " seconds");

                report.addInfo("Loading entry statements: " + mappedStatements.size());
                start = System.currentTimeMillis();
                statementLoadService.loadStatementsMappedToEntrySpecAnnotationsForSource(mappedStatements, source);
                report.addInfo("Finish load mapped statements for source "+ source + " in " + (System.currentTimeMillis() - start)/1000 + " seconds");

            }else {
                report.addInfo("skipping load of " + rawStatements.size() + " raw statements and " + mappedStatements.size() + " mapped statements for source "+ source);
            }


        }catch (SQLException e){
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

            report.addWarning("Error: skipping statements with invalid entry accessions "+invalidStatementEntryAccessions);
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

        public ReportBuilder (){

            start = System.currentTimeMillis();
            builder = new StringBuilder();

        }

        public synchronized void addInfo(String message){
            if(builder != null){
                LOGGER.info(message);
                builder.append(message + "\n");
            }
        }

        public synchronized void addInfoWithElapsedTime(String message){
            String messageWithElapsedTime = message + " " + ((System.currentTimeMillis() - start)/1000) + " seconds from the start of ETL process.";
            addInfo(messageWithElapsedTime);
        }

        @Override
        public synchronized String toString(){
            return builder.toString();
        }

        public synchronized void addWarning(String string) {
            addInfo("WARNING - " + string);
        }
    }
}
