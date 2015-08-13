package org.nextprot.api.web.service.impl.writer;

import com.google.common.base.Preconditions;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * A base class for writing entry list into a flushable and closeable stream
 *
 * Created by fnikitin on 11/08/15.
 */
public abstract class NPEntryWriter<S extends Flushable & Closeable> {

    protected final S stream;

    /**
     * Build writer that flush in the given stream
     * @param stream an output stream
     *
     */
    public NPEntryWriter(S stream) {

        Preconditions.checkNotNull(stream);

        this.stream = stream;
    }

    /**
     * Writes all entries given the view and closes the writer (The stream should be closed outside).

     * @param entries the entries to be flush
     * @param viewName the view name
     * @param headerParams an optionally parameters map for header
     * @throws IOException
     */
    public void write(Collection<String> entries, String viewName, Map<String, Object> headerParams) throws IOException {

        init();
        writeHeader(headerParams);

        if (entries != null) {

            for (String acc : entries) {
                writeEntry(acc, viewName);
                flush();
            }
        }

        writeFooter();
        flush();

        close();
    }

    /** Writing initiated */
    public void init() {}

    /** Write header to the output stream (to be overridden by if needed) */
    protected void writeHeader(Map<String, Object> headerParams) throws IOException {}

    /** Write a single entry to the output stream given a view */
    protected abstract void writeEntry(String entryName, String viewName) throws IOException;

    /** Write footer to the output stream (to be overridden by if needed) */
    protected void writeFooter() throws IOException {}

    /** Flushing to stream */
    protected void flush() throws IOException {
        stream.flush();
    }

    /** Closes the writer (and not the stream) */
    public void close() throws IOException { }
}
