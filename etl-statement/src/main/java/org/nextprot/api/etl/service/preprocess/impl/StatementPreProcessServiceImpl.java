package org.nextprot.api.etl.service.preprocess.impl;

import org.apache.log4j.Logger;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.app.StatementSource;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.MainNames;
import org.nextprot.api.core.service.MainNamesService;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.service.annotation.merge.AnnotationDescriptionParser;
import org.nextprot.api.core.service.impl.MasterIdentifierServiceImpl;
import org.nextprot.api.core.utils.IsoformUtils;
import org.nextprot.api.etl.service.impl.StatementETLServiceImpl;
import org.nextprot.api.etl.service.preprocess.StatementPreProcessService;
import org.nextprot.api.rdf.service.HttpSparqlService;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementBuilder;
import org.nextprot.commons.statements.specs.CustomStatementField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static org.nextprot.commons.statements.specs.CoreStatementField.*;

@Service
public class StatementPreProcessServiceImpl implements StatementPreProcessService {

    protected final Logger LOGGER = Logger.getLogger(StatementPreProcessServiceImpl.class);

    @Autowired
    TerminologyService terminologyService;

    @Autowired
    HttpSparqlService httpSparqlService;

    @Autowired
    MasterIdentifierService masterIdentifierService;

    @Autowired
    MainNamesService mainNamesService;

    @Override
    public Set<Statement> process(StatementSource source, Collection<Statement> statements) {
        if (source == StatementSource.GlyConnect) {
            StatementETLServiceImpl.ReportBuilder report = new StatementETLServiceImpl.ReportBuilder();
            return new GlyConnectPreProcessor(report)
                    .process(statements);
        } else if (source == StatementSource.BioEditor) {
            return new BioEditorPreProcessor()
                    .process(statements);
        } else if (source == StatementSource.Bgee) {
            return new BgeePreProcessor()
                    .process(statements);
        } else if (source == StatementSource.IntAct) {
            StatementETLServiceImpl.ReportBuilder report = new StatementETLServiceImpl.ReportBuilder();
            return new IntActPreProcessor(report).process(statements);
        }
        return new StatementIdBuilder()
                .process(statements);
    }

    private class StatementIdBuilder implements StatementETLServiceImpl.PreTransformProcessor {

        @Override
        public Set<Statement> process(Collection<Statement> statements) {

            return statements.stream()
                    .map(rs -> new StatementBuilder(rs).build())
                    .collect(Collectors.toSet());
        }
    }

    private class GlyConnectPreProcessor implements StatementETLServiceImpl.PreTransformProcessor {

        private final StatementETLServiceImpl.ReportBuilder report;

        private GlyConnectPreProcessor(StatementETLServiceImpl.ReportBuilder report) {
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

    private class BioEditorPreProcessor implements StatementETLServiceImpl.PreTransformProcessor {

        @Override
        public Set<Statement> process(Collection<Statement> statements) {

            // TODO: should moved to BioEditor code instead
            return statements.stream()
                    .map(rs -> updateDescription(rs))
                    .collect(Collectors.toSet());
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

    private class BgeePreProcessor implements StatementETLServiceImpl.PreTransformProcessor {

        private final String RNA_SEQ_EVIDENCE = "ECO:0000295";
        private final String POSITIVE_EXPRESSION = "positive";
        private final String NEGATIVE_EXPRESSION = "negative";

        @Override
        public Set<Statement> process(Collection<Statement> statements) {
            // Read the ENSEMBL_ID from extra fields and converts it to the entry ID
            Set<Statement> preProcessedStatements = statements.stream()
                    .map((statement) -> {
                        // Maps the gene with the corresponding
                        String ensemblID = statement.getValue(new CustomStatementField("ENSEMBL_ID"));
                        MasterIdentifierServiceImpl.MapStatus geneMapStatus = masterIdentifierService.getMapStatusForENSG(ensemblID);
                        LOGGER.info("Gene to entry map status " + geneMapStatus.getStatus());
                        List<String> entries = geneMapStatus.getEntries();
                        if(geneMapStatus.getStatus().equals(MasterIdentifierServiceImpl.MapStatus.Status.MAPS_NO_ENTRY)) {
                            LOGGER.info("No entry found for ensembl ID " + ensemblID + " ignoring the statement");
                            return null;
                        } else if(geneMapStatus.getStatus().equals(MasterIdentifierServiceImpl.MapStatus.Status.MAPS_MULTIPLE_ENTRIES)) {
                            LOGGER.info("Multiple entries found for ensembl ID " + ensemblID + " ignoring the statement");
                            return null;
                        } else if(geneMapStatus.getStatus().equals(MasterIdentifierServiceImpl.MapStatus.Status.MAPS_MULTIGENE_ENTRY)) {
                            LOGGER.info("Multigene entry "+ entries.get(0).toString() +" found for ensembl ID " + ensemblID + " ignoring the statement");
                            return null;
                        } else {
                            String entryAccession = entries.get(0).toString();
                            LOGGER.info("Unique entry found for ensembl ID " + ensemblID + " entry " + entryAccession);
                            statement.put(ENTRY_ACCESSION, entryAccession);

                            // Should transform the expression level
                            // "detected", "not detected" instead of "positive", "negative when EVIDENCE_CODE == "ECO:0000295" ( RNA-sequencing evidence)
                            String ecoCode = statement.getValue(EVIDENCE_CODE);
                            if(ecoCode != null) {
                                if(RNA_SEQ_EVIDENCE.equals(ecoCode)) {
                                    CustomStatementField expressionLevelField = new CustomStatementField("EXPRESSION_LEVEL");
                                    String expressionLevel = statement.getValue(expressionLevelField);
                                    if(POSITIVE_EXPRESSION.equals(expressionLevel)) {
                                        statement.put(expressionLevelField, "detected");
                                    } else if(NEGATIVE_EXPRESSION.equals(expressionLevel)) {
                                        statement.put(expressionLevelField, "not detected");
                                    }
                                }
                            } else {
                                LOGGER.info("ECO Code is null");
                            }

                            return statement;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            return preProcessedStatements;
        }
    }

    private class IntActPreProcessor implements StatementETLServiceImpl.PreTransformProcessor {

        private final StatementETLServiceImpl.ReportBuilder report;

        private IntActPreProcessor(StatementETLServiceImpl.ReportBuilder report) {
            this.report = report;
        }

        @Override
        public Set<Statement> process(Collection<Statement> statements) {
            Set<Statement> filteredStatements = filterStatements(statements);
            report.addInfo("Filtering " + filteredStatements.size() +  " IntAct raw statements over " + statements.size());
            return setAdditionalFieldsForIntActStatements(filteredStatements);
        }

        private Set<Statement> filterStatements(Collection<Statement> statements) {
            // Filter out statements with unknown NEXTPROT_ACCESSION
            return statements.stream()
                             .filter(statement -> {
                                 String nxAcc = statement.getValue(NEXTPROT_ACCESSION);
                                 Optional<MainNames> isoformOrEntryMainName = mainNamesService.findIsoformOrEntryMainName(nxAcc);
                                 return isoformOrEntryMainName.isPresent() ;
                             })
                             .collect(Collectors.toSet());
        }

        private Set<Statement> setAdditionalFieldsForIntActStatements(Set<Statement> statements) {

            Set<Statement> statementSet = new HashSet<>();

            statements.forEach(st -> {
                        StatementBuilder builder = new StatementBuilder(st);
                        if (st.getValue(new CustomStatementField("XENO")).equals("YES")) {
                            builder.addField(BIOLOGICAL_OBJECT_DATABASE, "UniProtKB");
                        }
                        statementSet.add(builder.build());
                    }
            );

            report.addInfo("Updating " + statementSet.size() + "/" + (statements.size()) +
                    " IntAct statements: set additional fields (RESOURCE_TYPE, STATEMENT_ID, and BIOLOGICAL_OBJECT_DATABASE)");

            return statementSet;
        }
    }
}
