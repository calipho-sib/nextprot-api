package org.nextprot.api.core.utils.annot.export;

import org.nextprot.api.core.domain.Entry;

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

    public OutputStream getOutputStream() {
        return outputStream;
    }

    protected abstract void writeHeader() throws IOException;
    protected abstract void writeRows(Entry entry) throws IOException;
    protected void flush() throws IOException {
        outputStream.flush();
    }
}
