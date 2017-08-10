package org.nextprot.api.web.service.impl.writer;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.release.ReleaseInfo;
import org.nextprot.api.core.service.export.format.NextprotMediaType;

import java.io.*;
import java.util.Collection;

/**
 * A base class for writing entry list into a flushable and closeable stream
 *
 * @param <S> decorated stream
 *
 * Created by fnikitin on 11/08/15.
 */
public abstract class EntryStreamWriter<S extends Flushable & Closeable> implements AutoCloseable {

    static final String UTF_8 = "UTF-8";

    private final S stream;

    /**
     * Build writer that flush in the given stream
     * @param stream an output stream (this stream will be closed by this writer)
     */
    EntryStreamWriter(S stream) {

        Preconditions.checkNotNull(stream);

        this.stream = stream;
    }

    /**
     * @return the output stream (should be closed outside this class).
     */
    final S getStream() {

        return stream;
    }

    /**
     * Writes all entries and closes the writer (The stream should be closed outside this class).
     *
     * @param entries the entries to be flush
     */
    public void write(Collection<String> entries) throws IOException {

        write(entries, null);
    }

    /**
     * Writes each entry and flush to the stream then at the end closes the writer (The stream should be closed
     * outside this class).
     *
     * @param entries the entries to be flush
     * @param releaseInfo information about current neXtProt release
     */
    public void write(Collection<String> entries, ReleaseInfo releaseInfo) throws IOException {

        writeHeader((entries != null) ? entries.size():0, releaseInfo);

        if (entries != null) {

            for (String acc : entries) {
                writeEntry(acc);
                flush();
            }
        }

        writeFooter();
        flush();
    }

    /** Write header to the output stream (to be overridden by if needed) */
    protected void writeHeader(int entryNum, ReleaseInfo releaseInfo) throws IOException {}

    /** Write a single entry to the output stream */
    protected abstract void writeEntry(String entryName) throws IOException;

    /** Write footer to the output stream (to be overridden by if needed) */
    protected void writeFooter() throws IOException {}

    /** Flushing to stream */
    protected void flush() throws IOException {
        stream.flush();
    }

    /** Closing the decorated stream */
    @Override
    public void close() throws IOException {
        stream.close();
    }

    /**
     * Create new instance of streaming NPEntryWriter
     *
     * @param format the output file format
     * @param view the view for velocity
     * @param os the output stream
     * @return a NPEntryWriter instance
     * @throws UnsupportedEncodingException
     */
    public static EntryStreamWriter newAutoCloseableWriter(NextprotMediaType format, String view, OutputStream os) throws IOException {

        Preconditions.checkNotNull(format);

        switch (format) {

            case XML:
                return new EntryXMLStreamWriter(os, view);
            case TXT:
                return new EntryTXTStreamWriter(os);
            case XLS:
                return EntryXLSWriter.newNPEntryXLSWriter(os, view);
            case JSON:
                return new EntryJSONStreamWriter(os, view);
            case FASTA:
                return new EntryFastaStreamWriter(os);
            case PEFF:
                return new EntryPeffStreamWriter(os);
            case TURTLE:
                return new EntryTTLStreamWriter(os, view);
            default:
                throw new NextProtException("No NPEntryStreamWriter implementation for " + format);
        }
    }
}
