package org.nextprot.api.core.service.export.io;

import org.nextprot.api.core.domain.EntityName;
import org.nextprot.api.core.domain.ChromosomeReport;
import org.nextprot.api.core.domain.EntryReport;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.domain.ProteinExistence;
import org.nextprot.api.core.service.OverviewService;
import org.nextprot.api.core.service.export.HPPChromosomeReportWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Writes a {@code ChromosomeReport} in TSV format
 *
 * Created by fnikitin on 19.04.17.
 */
public class HPPChromosomeReportTSVWriter implements HPPChromosomeReportWriter {

    private PrintWriter writer;
    private final OverviewService overviewService;

    public HPPChromosomeReportTSVWriter(OutputStream os, OverviewService overviewService) {

        this.overviewService = overviewService;
        this.writer = new PrintWriter(os);
    }

    @Override
    public void write(ChromosomeReport report) throws IOException {

        writer.write(extractHeaders().stream().collect(Collectors.joining("\t")));
        writer.write("\n");

        Map<String, EntryReport> groupedByAccession = report.getEntryReports().stream()
                .collect(Collectors.toMap(
                        EntryReport::getAccession,
                        er -> er,
                        (er1, er2) -> er1) // 1. keep one entry report for each entry accession
                );

        List<String> sortedAccessions = new ArrayList<>(groupedByAccession.keySet()).stream()
                .sorted()                  // 2. order by entry accession
                .collect(Collectors.toList());

        for (String accession : sortedAccessions) {

            EntryReport er = groupedByAccession.get(accession);
            writer.write(extractValues(er,  overviewService.findOverviewByEntry(er.getAccession()))
                    .stream().collect(Collectors.joining("\t")));
            writer.write("\n");
        }

        writer.close();
    }

    private List<String> extractValues(EntryReport entryReport, Overview overview) {

        return Arrays.asList(
                entryReport.getAccession(),
                getMainEntityNames(overview.getGeneNames()),
                ProteinExistence.valueOfKey(entryReport.getProteinExistence()).getDescription(),
                (entryReport.isProteomics()) ? "yes" : "no",
                (entryReport.isAntibody()) ? "yes" : "no"
        );
    }

    private static String getMainEntityNames(List<EntityName> entityNameList) {

        if (entityNameList != null && !entityNameList.isEmpty()) {
            return entityNameList.stream()
                    .filter(EntityName::isMain)
                    .map(EntityName::getName)
                    .collect(Collectors.joining(";"));
        }
        return "-";
    }

    private static List<String> extractHeaders() {

        return Arrays.asList(
                "neXtProt AC", "Gene name(s)", "Protein existence", "Proteomics", "Antibody"
        );
    }
}
