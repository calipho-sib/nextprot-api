package org.nextprot.api.core.service.export;

import org.nextprot.api.core.domain.EntryReport;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

/**
 * Base class Writer that can write {@code EntryReport}s
 *
 * Created by fnikitin on 19.04.17.
 */
public abstract class EntryReportWriter {

    protected final OutputStream os;

    public EntryReportWriter(OutputStream os) {
        this.os = os;
    }

    protected void writeHeader() throws IOException { }

    protected void flush() throws IOException { }

    protected void close() throws IOException {
        os.close();
    }

    public void write(Collection<EntryReport> entryReports) throws IOException {

        writeHeader();

        if (entryReports != null) {

            for (EntryReport entryReport : entryReports) {
                writeEntryReport(entryReport);
            }
        }

        flush();
        close();
    }

    protected abstract void writeEntryReport(EntryReport entryReport) throws IOException;
}
