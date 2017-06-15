package org.nextprot.api.core.service.export.io;

import org.nextprot.api.core.domain.ChromosomeReport;
import org.nextprot.api.core.domain.EntryReport;
import org.nextprot.api.core.service.export.ChromosomeReportWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import static org.nextprot.api.core.domain.EntryReport.getValidGeneNameValue;

/**
 * Base class Writer that can write {@code ChromosomeReport}
 *
 * Created by fnikitin on 19.04.17.
 */
abstract class BaseChromosomeReportWriter implements ChromosomeReportWriter {

    protected final OutputStream os;

    public BaseChromosomeReportWriter(OutputStream os) {
        this.os = os;
    }

    protected void writeHeader(ChromosomeReport report) throws IOException {}

    protected void writeFooter() throws IOException {}

    protected void close() throws IOException {
        os.close();
    }

    public void write(ChromosomeReport chromosomeReport) throws IOException {

        writeHeader(chromosomeReport);
        writeChromosomeReport(chromosomeReport);
        writeFooter();

        close();
    }

    protected abstract void writeChromosomeReport(ChromosomeReport report) throws IOException;

    protected List<String> extractValues(EntryReport entryReport) {

        return Arrays.asList(
                getValidGeneNameValue(entryReport.getGeneName()),
                entryReport.getAccession(),
                entryReport.getChromosomalLocation(),
                entryReport.getGeneStartPosition(),
                entryReport.getGeneEndPosition(),
                entryReport.getCodingStrand(),
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
    }
}
