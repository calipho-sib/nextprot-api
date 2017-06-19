package org.nextprot.api.core.service.export.io;

import org.nextprot.api.core.domain.ChromosomeReport;
import org.nextprot.api.core.domain.EntryReport;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Writes a {@code ChromosomeReport} in TSV format
 *
 * Created by fnikitin on 19.04.17.
 */
public class ChromosomeReportTSVWriter extends BaseChromosomeReportWriter {

    private final PrintWriter writer;

    public ChromosomeReportTSVWriter(OutputStream os) {
        super(os);
        this.writer = new PrintWriter(os);
    }

    @Override
    protected void writeChromosomeReport(ChromosomeReport report) throws IOException {

        writer.write(extractHeaders().stream().collect(Collectors.joining("\t")));
        writer.write("\n");

        for (EntryReport er : report.getEntryReports()) {

            writeEntryReport(extractValues(er));
        }
    }

    @Override
    public void close() throws IOException {
        writer.close();
        super.close();
    }

    private void writeEntryReport(List<String> values) throws IOException {

        writer.write(values.stream().collect(Collectors.joining("\t")));
        writer.write("\n");
    }

    private static List<String> extractHeaders() {

        return Arrays.asList(
                "Gene name", "neXtProt AC", "Chromosomal location", "Start position", "Stop position",
                "Coding strand", "Protein existence", "Proteomics", "Antibody", "3D", "Disease", "Isoforms", "Variants",
                "PTMs", "Description"
        );
    }
}
