package org.nextprot.api.core.utils.annot.export;

import org.nextprot.api.core.domain.Entry;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Writes entry parts information in specific format (i.e. xls, tsv, ...)
 * @param <T> OutputStream output type
 */
public abstract class EntryPartWriter<T extends OutputStream> {

    private final T outputStream;

    EntryPartWriter() {

        this.outputStream = newOutputStream();
    }

    public void write(Entry entry) throws IOException {

        writeHeader(outputStream);
        writeRows(entry, outputStream);
    }

    T getOutputStream() {
        return outputStream;
    }

    protected abstract T newOutputStream();
    protected abstract void writeHeader(T outputStream) throws IOException;
    protected abstract void writeRows(Entry entry, T outputStream) throws IOException;
}
