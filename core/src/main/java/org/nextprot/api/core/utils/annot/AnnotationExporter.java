package org.nextprot.api.core.utils.annot;

import com.nextprot.api.annotation.builder.statement.dao.StatementDao;
import org.apache.log4j.Logger;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.commons.statements.StatementField;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class AnnotationExporter {

    private static final Logger LOGGER = Logger.getLogger(AnnotationExporter.class);

    private final EntryBuilderService entryBuilderService;
    private final StatementDao statementDao;
    private final MasterIdentifierService masterIdentifierService;

    private final Config config;
    private final Map<AnnotationCategory, NpBedMergingStats> statisticsMap = new HashMap<>();

    public AnnotationExporter(EntryBuilderService entryBuilderService, StatementDao statementDao, MasterIdentifierService masterIdentifierService) {

        this(entryBuilderService, statementDao, masterIdentifierService, new Config());
    }

    public AnnotationExporter(EntryBuilderService entryBuilderService, StatementDao statementDao, MasterIdentifierService masterIdentifierService,
                              Config config) {

        this.entryBuilderService = entryBuilderService;
        this.statementDao = statementDao;
        this.masterIdentifierService = masterIdentifierService;

        this.config = config;
    }

    public String exportAllGeneStatementsAsTsvString() {

        List<String> geneNames = statementDao.findAllDistinctValuesforField(StatementField.GENE_NAME);

        return exportAnnotationStatsAsTsvString(geneNames);
    }

    public String exportAnnotationStatsAsTsvString(List<String> geneNames) {

        StringBuilder sb = new StringBuilder();

        // header row
        sb.append(config.fields.stream().collect(Collectors.joining("\t"))).append("\n");

        calcAnnotationStatsFromGeneNames(geneNames);

        // rows
        for (AnnotationCategory category : statisticsMap.keySet()) {

            NpBedMergingStats stats = statisticsMap.get(category);

            sb
                    .append(category)
                    .append("\t")
                    .append(stats.countMergedAnnots())
                    .append("\t")
                    .append(stats.countUnmergedAnnots())
                    .append("\t")
                    .append((stats.countMergedAnnots()>0) ? toAnnotationString(stats.getMerged().get(0)) : "")
                    .append("\t")
                    .append((stats.countUnmergedAnnots()>0) ? stats.getUnmerged().get(0) : "")
                    .append("\n");
        }

        return sb.toString();
    }

    private String toAnnotationString(Annotation annotation) {

        return annotation.getUniqueName()+"."+annotation.getAnnotationHash();
    }

    private void calcAnnotationStatsFromGeneNames(List<String> geneNames) {

        List<String> accessions = new ArrayList<>();

        for (String geneName : geneNames) {

            Set<String> set = masterIdentifierService.findEntryAccessionByGeneName(geneName);

            if (set.isEmpty()) LOGGER.warn("could not find " + geneName);
            accessions.add(set.iterator().next());
        }

        calcAnnotationStatsFromEntryAccessions(accessions);
    }

    private void calcAnnotationStatsFromEntryAccessions(List<String> entryAccessions) {

        statisticsMap.clear();

        for (String entryAccession : entryAccessions) {

            Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryAccession).withAnnotations());

            // statement rows
            for (Annotation annotation : entry.getAnnotations()) {

                if (!statisticsMap.containsKey(annotation.getAPICategory())) {
                    statisticsMap.put(annotation.getAPICategory(), new NpBedMergingStats());
                }

                NpBedMergingStats stats = statisticsMap.get(annotation.getAPICategory());

                //sb.append(config.fields.stream().map(statement::getValue).collect(Collectors.joining("\t"))).append("\n");

                String annotationHash = annotation.getAnnotationHash();
                String uniqueName = annotation.getUniqueName();

                // bed integrated with np1
                if (annotationHash != null) {

                    // merged
                    if (!uniqueName.equals(annotationHash)) {
                        stats.addMergedAnnot(annotation);
                    }
                    // not merged
                    else {
                        stats.addUnmergedAnnot(annotation);
                    }
                }
                // np1
                else {
                    stats.addUnmergedAnnot(annotation);
                }
            }
        }
    }

    public Map<AnnotationCategory, NpBedMergingStats> getStatisticsMap() {
        return statisticsMap;
    }

    public void exportAsTsvFile(String directory, String fileName, String content) throws FileNotFoundException {

        String filename = directory + File.separator + fileName+".tsv";

        PrintWriter pw = new PrintWriter(filename);
        pw.append(content);
        pw.close();
    }

    public static class Config {

        private final Set<AnnotationCategory> categories;
        private final List<String> fields = Arrays.asList("category", "merged#", "unmerged#", "merged example", "unmerged example");

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

        public List<String> getFields() {
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

    public static class NpBedMergingStats {

        private List<Annotation> merged = new ArrayList<>();
        private List<Annotation> unmerged = new ArrayList<>();

        public void addMergedAnnot(Annotation annotation) {
            merged.add(annotation);
        }

        public void addUnmergedAnnot(Annotation annotation) {
            unmerged.add(annotation);
        }

        public int countMergedAnnots() {
            return merged.size();
        }

        public int countUnmergedAnnots() {
            return unmerged.size();
        }

        public List<Annotation> getMerged() {
            return merged;
        }

        public List<Annotation> getUnmerged() {
            return unmerged;
        }

        @Override
        public String toString() {
            return "NpBedMergingStats{" +
                    "countMergedAnnots=" + countMergedAnnots() +
                    ((countMergedAnnots()>0) ? " ("+merged +")" : "") +
                    ", countUnmergedAnnots=" + countUnmergedAnnots() +
                    ((countUnmergedAnnots()>0) ? " ("+unmerged +")" : "") +
                    '}';
        }
    }
}
