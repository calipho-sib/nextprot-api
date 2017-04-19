package org.nextprot.api.core.service.export.writer;

import org.nextprot.api.core.domain.EntryReport;
import org.nextprot.api.core.service.export.EntryReportWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Writes {@code EntryReport}s in TSV format
 *
 * Created by fnikitin on 19.04.17.
 */
public class EntryReportTSVWriter extends EntryReportWriter {

    private final PrintWriter writer;

    public EntryReportTSVWriter(OutputStream os) {
        super(os);
        this.writer = new PrintWriter(os);
    }

    @Override
    protected void writeHeader() throws IOException {

        List<String> headers = Arrays.asList(
                "Gene name", "neXtProt AC", "Chromosomal position", "Start position", "Stop position",
                "Protein existence", "Proteomics", "Antibody", "3D", "Disease", "Isoforms", "Variants",
                "PTMs", "Description"
        );

        writer.write(headers.stream().collect(Collectors.joining("\t")));
        writer.write("\n");
    }

    @Override
    protected void writeEntryReport(EntryReport entryReport) throws IOException {

        List<String> row = Arrays.asList(
                entryReport.getGeneName(),
                entryReport.getAccession(),
                entryReport.getChromosomalLocation(),
                String.valueOf(entryReport.getGeneStartPosition()),
                String.valueOf(entryReport.getGeneEndPosition()),
                entryReport.getProteinExistence(),
                (entryReport.isProteomics()) ? "yes" : "no",
                (entryReport.isAntibody()) ? "yes" : "no",
                (entryReport.is3D()) ? "yes" : "no",
                (entryReport.isDisease()) ? "yes" : "no",
                String.valueOf(entryReport.countIsoforms()),
                String.valueOf(entryReport.countVariants()),
                String.valueOf(entryReport.countPTMs()),
                String.valueOf(entryReport.getDescription())
        );

        writer.write(row.stream().collect(Collectors.joining("\t")));
        writer.write("\n");
    }

    @Override
    public void close() throws IOException {
        writer.close();
        super.close();
    }
}
