package org.nextprot.api.etl.service.impl;

import com.google.common.collect.Sets;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.etl.NextProtSource;
import org.nextprot.api.etl.service.StatementETLService;
import org.nextprot.api.etl.service.StatementLoaderService;
import org.nextprot.api.etl.service.StatementSourceService;
import org.nextprot.api.etl.service.StatementTransformerService;
import org.nextprot.api.rdf.service.HttpSparqlService;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.reader.JsonStreamingReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.sql.BatchUpdateException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.nextprot.api.core.utils.IsoformUtils.findEntryAccessionFromEntryOrIsoformAccession;
import static org.nextprot.commons.statements.specs.CoreStatementField.ENTRY_ACCESSION;
import static org.nextprot.commons.statements.specs.CoreStatementField.NEXTPROT_ACCESSION;

/**
 * ETL Pipeline with Batch Processing
 */
@Service
public class MultipleBatchesStatementETLService implements StatementETLService {

	@Autowired
	private MasterIdentifierService masterIdentifierService;

	@Autowired
	private TerminologyService terminologyService;

	@Autowired
	private HttpSparqlService httpSparqlService;

	@Autowired
	private StatementSourceService statementSourceService;

	@Autowired
	private StatementTransformerService statementTransformerService;

	@Autowired
	private StatementLoaderService statementLoadService;

	@Override
	public String etlStatements(NextProtSource source, String release, boolean load) throws IOException {

		SingleBatchStatementETLService.ReportBuilder report = new SingleBatchStatementETLService.ReportBuilder();

		// traverse all json files
		for (String jsonFilename : statementSourceService.getJsonFilenamesForRelease(source, release)) {

			String jsonContent = statementSourceService.getStatementJsonContent(source, release, jsonFilename);

			JsonStreamingReader reader = new JsonStreamingReader(new StringReader(jsonContent), source.getSpecifications());

			while (reader.hasNextStatement()) {

				Set<Statement> rawStatements = filterValidStatements(reader.readStatements(10), report);

				Collection<Statement> mappedStatements = buildMappedStatements(rawStatements, report);

				if (load) {
					loadStatements(source, rawStatements, mappedStatements, report);
				}
			}
		}

		return report.toString();
	}

	private Collection<Statement> buildMappedStatements(Collection<Statement> rawStatements, SingleBatchStatementETLService.ReportBuilder report) {

		//rawStatements = preTransformStatements(source, rawStatements, report);
		//report.addInfoWithElapsedTime("Finished pre transformation treatments");

		Collection<Statement> statements = statementTransformerService.transformStatements(rawStatements, report);
		report.addInfo("Transformed " + rawStatements.size() + " raw statements to " + statements.size() + " mapped statements ");

		return statements;
	}

	private void loadStatements(NextProtSource source, Collection<Statement> rawStatements, Collection<Statement> mappedStatements, SingleBatchStatementETLService.ReportBuilder report) {

		try {
			report.addInfo("Loading raw statements for source " + source + ": " + rawStatements.size());
			long start = System.currentTimeMillis();
			statementLoadService.loadRawStatementsForSource(new HashSet<>(rawStatements), source);
			report.addInfo("Finish load raw statements for source " + source + " in " + (System.currentTimeMillis() - start) / 1000 + " seconds");

			report.addInfo("Loading entry statements: " + mappedStatements.size());
			start = System.currentTimeMillis();
			statementLoadService.loadStatementsMappedToEntrySpecAnnotationsForSource(mappedStatements, source);
			report.addInfo("Finish load mapped statements for source " + source + " in " + (System.currentTimeMillis() - start) / 1000 + " seconds");

		} catch (BatchUpdateException e) {
			throw new NextProtException("Failed to load in source " + source + ":" + e + ", cause=" + e.getNextException());
		} catch (SQLException e) {
			throw new NextProtException("Failed to load in source " + source + ":" + e);
		}
	}

	private Set<Statement> filterValidStatements(Collection<Statement> rawStatements, SingleBatchStatementETLService.ReportBuilder report) {

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

		return (statement.getValue(ENTRY_ACCESSION) != null) ?
				statement.getValue(ENTRY_ACCESSION) :
				findEntryAccessionFromEntryOrIsoformAccession(statement.getValue(NEXTPROT_ACCESSION));
	}
}
