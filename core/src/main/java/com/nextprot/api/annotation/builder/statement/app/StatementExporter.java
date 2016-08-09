package com.nextprot.api.annotation.builder.statement.app;

import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import com.nextprot.api.annotation.builder.statement.dao.StatementDao;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementField;
import org.nextprot.commons.statements.constants.AnnotationType;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class StatementExporter {

    private final StatementDao statementDao;
    private final MasterIdentifierService masterIdentifierService;

    private final StringBuilder sb = new StringBuilder();
    private final Set<AnnotationCategory> categories;
    private final List<StatementField> fields;
    private final String header;
    private final Set<String> fetchedAccessions;

    public StatementExporter(StatementDao statementDao, MasterIdentifierService masterIdentifierService) {

        this(statementDao, masterIdentifierService, EnumSet.of(AnnotationCategory.VARIANT, AnnotationCategory.MUTAGENESIS),
             Arrays.asList(StatementField.GENE_NAME,
                StatementField.NEXTPROT_ACCESSION,
                StatementField.ANNOTATION_CATEGORY,
                StatementField.ANNOTATION_NAME,
                StatementField.VARIANT_ORIGINAL_AMINO_ACID,
                StatementField.VARIANT_VARIATION_AMINO_ACID,
                StatementField.LOCATION_BEGIN_MASTER,
                StatementField.LOCATION_END_MASTER,
                StatementField.LOCATION_BEGIN,
                StatementField.LOCATION_END
             )
        );
    }

    public StatementExporter(StatementDao statementDao, MasterIdentifierService masterIdentifierService,
                             Set<AnnotationCategory> categories, List<StatementField> fields) {

        this.statementDao = statementDao;
        this.masterIdentifierService = masterIdentifierService;

        this.categories = categories;
        this.fields = fields;
        this.header = fields.stream().map(Enum::name).collect(Collectors.joining("\t"));
        this.fetchedAccessions = new HashSet<>();
    }

    public void fetchAllGeneStatements() {

        statementDao.findUniqueNames().forEach(this::fetchStatementsFromEntryAccession);
    }

    public void fetchGeneSetStatements(Set<String> geneNames) {

        geneNames.forEach(this::fetchGeneStatements);
    }

    public void fetchGeneStatements(String geneName) {

        Preconditions.checkNotNull(geneName, "gene name should not be null");
        Preconditions.checkArgument(!geneName.isEmpty(), "gene name should be defined");

        Set<String> accessions = masterIdentifierService.findEntryAccessionByGeneName(geneName);

        accessions.forEach(this::fetchStatementsFromEntryAccession);
    }

    private void fetchStatementsFromEntryAccession(String entryAccession) {

        if (fetchedAccessions.contains(entryAccession)) return;

        List<Statement> statements = statementDao.findNormalStatements(AnnotationType.ENTRY, entryAccession).stream()
                .filter(statement -> categories.contains(AnnotationCategory.getDecamelizedAnnotationTypeName(statement.getValue(StatementField.ANNOTATION_CATEGORY))))
                .collect(Collectors.toList());

        if (!statements.isEmpty()) {
            appendTsvString(statements);
        }

        fetchedAccessions.add(entryAccession);
    }

    public String exportAsTsvString() {

        return header+"\n"+sb.toString();
    }

    void exportAsTsvFile(String filename) throws FileNotFoundException {

        PrintWriter pw = new PrintWriter(Files.getNameWithoutExtension(filename)+".tsv");
        pw.append(exportAsTsvString());
        pw.close();
    }

    public Set<String> getFetchedAccessions() {
        return fetchedAccessions;
    }

    private void appendTsvString(List<Statement> statements) {

        for (Statement statement : statements) {

            sb.append(fields.stream().map(statement::getValue).collect(Collectors.joining("\t"))).append("\n");
        }
    }
}
