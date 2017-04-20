package org.nextprot.api.core.service.export;

import org.nextprot.api.core.domain.ChromosomeReport;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Base class Writer that can write {@code ChromosomeReport}
 *
 * Created by fnikitin on 19.04.17.
 */
public abstract class ChromosomeReportWriter {

    protected final OutputStream os;

    public ChromosomeReportWriter(OutputStream os) {
        this.os = os;
    }

    protected void writeHeader(ChromosomeReport.Summary summary) throws IOException {}

    protected void writeFooter() throws IOException {}

    protected void close() throws IOException {
        os.close();
    }

    public void write(ChromosomeReport chromosomeReport) throws IOException {

        writeHeader(chromosomeReport.getSummary());
        writeChromosomeReport(chromosomeReport);
        writeFooter();

        close();
    }

    protected abstract void writeChromosomeReport(ChromosomeReport report) throws IOException;
}
