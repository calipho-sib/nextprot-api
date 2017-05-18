package org.nextprot.api.core.service.export.writer;

import org.nextprot.api.core.domain.ChromosomeReport;
import org.nextprot.api.core.service.export.ChromosomeReportWriter;

import java.io.IOException;
import java.io.OutputStream;

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
}
