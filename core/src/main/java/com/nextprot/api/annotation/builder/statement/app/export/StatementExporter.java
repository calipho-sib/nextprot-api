package com.nextprot.api.annotation.builder.statement.app.export;

import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import com.nextprot.api.annotation.builder.statement.dao.StatementDao;
import org.apache.log4j.Logger;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementField;
import org.nextprot.commons.statements.constants.AnnotationType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class StatementExporter {

    protected static final Logger LOGGER = Logger.getLogger(StatementExporter.class);

    private final StatementDao statementDao;
    private final MasterIdentifierService masterIdentifierService;

    private final StringBuilder sb = new StringBuilder();

    private final Config config;
    private final Set<String> fetchedAccessions;

    public StatementExporter(StatementDao statementDao, MasterIdentifierService masterIdentifierService) {

        this(statementDao, masterIdentifierService, new Config());
    }

    public StatementExporter(StatementDao statementDao, MasterIdentifierService masterIdentifierService,
                             Config config) {

        this.statementDao = statementDao;
        this.masterIdentifierService = masterIdentifierService;

        this.config = config;
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

        if (accessions.isEmpty())
            System.err.println("ERROR: could not find "+geneName);

        accessions.forEach(this::fetchStatementsFromEntryAccession);
    }

    private void fetchStatementsFromEntryAccession(String entryAccession) {

        if (fetchedAccessions.contains(entryAccession)) return;

        List<Statement> statements = statementDao.findNormalStatements(AnnotationType.ENTRY, entryAccession).stream()
                .filter(statement -> config.categories.contains(AnnotationCategory.getDecamelizedAnnotationTypeName(statement.getValue(StatementField.ANNOTATION_CATEGORY))))
                .collect(Collectors.toList());

        if (!statements.isEmpty()) {
            appendTsvString(statements);
        }

        fetchedAccessions.add(entryAccession);
    }

    public String exportAsTsvString() {

        return config.fields.stream().map(Enum::name).collect(Collectors.joining("\t")) + "\n"
                + sb.toString();
    }

    void exportAsTsvFile(String filename) throws FileNotFoundException {

        Path path = Paths.get(filename);

        filename = path.getParent().toString() + File.separator + Files.getNameWithoutExtension(filename)+".tsv";

        PrintWriter pw = new PrintWriter(filename);
        pw.append(exportAsTsvString());
        pw.close();

        System.out.println("statements exported in "+filename);
    }

    public Set<String> getFetchedAccessions() {
        return fetchedAccessions;
    }

    private void appendTsvString(List<Statement> statements) {

        for (Statement statement : statements) {

            sb.append(config.fields.stream().map(statement::getValue).collect(Collectors.joining("\t"))).append("\n");
        }
    }

    public static class Config {

        private final Set<AnnotationCategory> categories;
        private final List<StatementField> fields = Arrays.asList(StatementField.GENE_NAME,
                StatementField.NEXTPROT_ACCESSION,
                StatementField.ANNOTATION_CATEGORY,
                StatementField.ANNOTATION_NAME,
                StatementField.VARIANT_ORIGINAL_AMINO_ACID,
                StatementField.VARIANT_VARIATION_AMINO_ACID,
                StatementField.LOCATION_BEGIN_MASTER,
                StatementField.LOCATION_END_MASTER,
                StatementField.LOCATION_BEGIN,
                StatementField.LOCATION_END
        );

        public Config() {

            this(EnumSet.of(AnnotationCategory.VARIANT, AnnotationCategory.MUTAGENESIS));
        }

        public Config(String... categories) {

            this.categories = new HashSet<>(categories.length);

            for (String category : categories) {

                this.categories.add(AnnotationCategory.getByDbAnnotationTypeName(category));
            }
        }

        public Config(Set<AnnotationCategory> categories) {

            this.categories = categories;
        }

        public Set<AnnotationCategory> getCategories() {
            return categories;
        }

        public List<StatementField> getFields() {
            return fields;
        }

        @Override
        public String toString() {
            return "Config{" +
                    "categories=" + categories +
                    ", fields=" + fields +
                    '}';
        }
    }
}
