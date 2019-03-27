package org.nextprot.api.core.export;

import org.apache.log4j.Logger;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.dao.StatementDao;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.commons.statements.NXFlatTableStatementField;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AnnotationExporter {

    private static final Logger LOGGER = Logger.getLogger(AnnotationExporter.class);

    private final EntryBuilderService entryBuilderService;
    private final StatementDao statementDao;
    private final MasterIdentifierService masterIdentifierService;

    private final Config config;
    private final Map<AnnotationCategory, NpBedMergingStats> statisticsMap = new EnumMap<>(AnnotationCategory.class);

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

        List<String> geneNames = statementDao.findAllDistinctValuesforField(NXFlatTableStatementField.GENE_NAME);

        return exportAnnotationStatsAsTsvString(geneNames);
    }

    public String exportAnnotationStatsAsTsvString(List<String> geneNames) {

        StringBuilder sb = new StringBuilder();

        // header row
        sb.append(config.fields.stream().collect(Collectors.joining("\t"))).append("\n");

        calcAnnotationStatsFromGeneNames(geneNames);

        // rows
        for (Map.Entry<AnnotationCategory, NpBedMergingStats> entry : statisticsMap.entrySet()) {

            NpBedMergingStats stats = entry.getValue();

            sb
                    .append(entry.getKey())
                    .append("\t")
                    .append(stats.countAnnots(NpBedMergingStats.AnnotType.MERGED))
                    .append("\t")
                    .append(stats.countAnnots(NpBedMergingStats.AnnotType.UNMERGED_BED))
                    .append("\t")
                    .append(stats.countAnnots(NpBedMergingStats.AnnotType.UNMERGED_NP))
                    .append("\t")
                    .append((stats.countAnnots(NpBedMergingStats.AnnotType.MERGED)>0) ? toAnnotationString(stats.getAnnots(NpBedMergingStats.AnnotType.MERGED).get(0)) : "")
                    .append("\t")
                    .append((stats.countAnnots(NpBedMergingStats.AnnotType.UNMERGED_BED)>0) ? stats.getAnnots(NpBedMergingStats.AnnotType.UNMERGED_BED).get(0).getUniqueName() : "")
                    .append("\t")
                    .append((stats.countAnnots(NpBedMergingStats.AnnotType.UNMERGED_NP)>0) ? stats.getAnnots(NpBedMergingStats.AnnotType.UNMERGED_NP).get(0).getUniqueName() : "")
                    .append("\n");
        }

        return sb.toString();
    }

    private String toAnnotationString(Annotation annotation) {

        return annotation.getUniqueName()+" | "+annotation.getAnnotationHash();
    }

    private void calcAnnotationStatsFromGeneNames(List<String> geneNames) {

        List<String> accessions = new ArrayList<>();

        for (String geneName : geneNames) {

            Set<String> set = masterIdentifierService.findEntryAccessionByGeneName(geneName, false);

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
                        stats.addAnnot(NpBedMergingStats.AnnotType.MERGED, annotation);
                    }
                    // not merged
                    else {
                        stats.addAnnot(NpBedMergingStats.AnnotType.UNMERGED_BED, annotation);
                    }
                }
                // np1
                else {
                    stats.addAnnot(NpBedMergingStats.AnnotType.UNMERGED_NP, annotation);
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
        private final List<String> fields = Arrays.asList("category", "merged#", "unmerged_bed#", "unmerged_np1#", "merged_ex", "unmerged_bed_ex", "unmerged_np1_ex");

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

        public enum AnnotType {
            MERGED, UNMERGED_BED, UNMERGED_NP
        }

        private Map<AnnotType, List<Annotation>> annots = new EnumMap<>(AnnotType.class);

        NpBedMergingStats() {

            for (AnnotType type : AnnotType.values()) {

                annots.put(type, new ArrayList<>());
            }
        }

        public void addAnnot(AnnotType type, Annotation annotation) {

            annots.get(type).add(annotation);
        }

        public int countAnnots(AnnotType type) {

            return annots.get(type).size();
        }

        public List<Annotation> getAnnots(AnnotType type) {
            return annots.get(type);
        }
    }
}
