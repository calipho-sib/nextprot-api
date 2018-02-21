package org.nextprot.api.core.export;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.export.format.NextprotMediaType;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Writes entry parts information in specific format (i.e. xls, tsv, ...)
 */
public abstract class EntryPartWriter {

    private final OutputStream outputStream;

    EntryPartWriter(OutputStream os) {

        this.outputStream = os;
    }

    public void write(Entry entry) throws IOException {

        writeHeader();
        writeRows(entry);
        flush();
    }

    OutputStream getOutputStream() {
        return outputStream;
    }

    protected abstract void writeHeader() throws IOException;
    protected abstract void writeRows(Entry entry) throws IOException;
    protected void flush() throws IOException {
        outputStream.flush();
    }

    public static EntryPartWriter valueOf(NextprotMediaType format, EntryPartExporter exporter, OutputStream os) {

        switch (format) {

            case XLS:
                return new EntryPartWriterXLS(exporter, os);
            case TSV:
                return new EntryPartWriterTSV(exporter, os);
            default:
                throw new NextProtException("No writer implementation for " + format);
        }
    }
}
