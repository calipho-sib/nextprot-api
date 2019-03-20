package org.nextprot.api.core.export;

import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.dao.StatementDao;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementField;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class StatementExporter {

    private static final Logger LOGGER = Logger.getLogger(StatementExporter.class);

    private final StatementDao statementDao;
    private final MasterIdentifierService masterIdentifierService;

    private final Config config;

    public StatementExporter(StatementDao statementDao, MasterIdentifierService masterIdentifierService) {

        this(statementDao, masterIdentifierService, new Config());
    }

    public StatementExporter(StatementDao statementDao, MasterIdentifierService masterIdentifierService,
                             Config config) {

        this.statementDao = statementDao;
        this.masterIdentifierService = masterIdentifierService;

        this.config = config;
    }

    public String exportGeneStatementsAsTsvString(String geneName) {

        Preconditions.checkNotNull(geneName, "gene name should not be null");
        Preconditions.checkArgument(!geneName.isEmpty(), "gene name should be defined");

        StringBuilder sb = new StringBuilder();

        // header row
        sb.append(config.fields.stream().map(Enum::name).collect(Collectors.joining("\t"))).append("\n");

        Set<String> accessions = masterIdentifierService.findEntryAccessionByGeneName(geneName, false);

        if (accessions.isEmpty()) LOGGER.warn("could not find "+geneName);
        accessions.forEach(accession -> fetchAndAppendStatementsFromEntryAccession(accession, sb));

        return sb.toString();
    }

    public Map<String, String> exportAllGeneStatementsAsTsvString() {

        List<String> geneNames = statementDao.findAllDistinctValuesforField(StatementField.GENE_NAME);

        Map<String, String> map = new HashMap<>(geneNames.size());

        for (String geneName : geneNames) {

            // append statement rows
            map.put(geneName, exportGeneStatementsAsTsvString(geneName));
        }

        return map;
    }

    private void fetchAndAppendStatementsFromEntryAccession(String entryAccession, StringBuilder sb) {

        List<Statement> statements = statementDao.findNormalStatements(entryAccession).stream()
                .filter(statement -> config.categories.contains(AnnotationCategory.getDecamelizedAnnotationTypeName(statement.getValue(StatementField.ANNOTATION_CATEGORY))))
                .collect(Collectors.toList());

        if (!statements.isEmpty()) {
            // statement rows
            for (Statement statement : statements) {
                sb.append(config.fields.stream().map(statement::getValue).collect(Collectors.joining("\t"))).append("\n");
            }
        }
    }

    public void exportAsTsvFile(String directory, String genename, String content) throws FileNotFoundException {

        String filename = directory + File.separator + genename+"_statements.tsv";

        PrintWriter pw = new PrintWriter(filename);
        pw.append(content);
        pw.close();
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
