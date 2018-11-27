package org.nextprot.api.web.service.impl.writer;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.service.export.format.NextprotMediaType;
import org.springframework.context.ApplicationContext;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A base class for writing entry list into a flushable and closeable stream
 *
 * @param <S> decorated stream
 *
 * Created by fnikitin on 11/08/15.
 */
public abstract class EntryStreamWriter<S extends Flushable & Closeable> implements AutoCloseable {

    private static String ENTRY_COUNT = "entryCount";
    private static String ISOFORM_COUNT = "isoformCount";
    private static String RELEASE_INFO = "releaseInfo";
    private static String RELEASE_DATA_SOURCES = "releaseDataSources";
    private static String DESCRIPTION = "description";

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

        write(entries, new HashMap<>());
    }

    /**
     * Writes each entry and flush to the stream then at the end closes the writer (The stream should be closed
     * outside this class).
     *
     * @param entries the entries to be flush
     * @param infos informations about entries
     */
    public void write(Collection<String> entries, Map<String, Object> infos) throws IOException {

        if (entries == null) { entries = new ArrayList<>(); }

        infos.put(ENTRY_COUNT, entries.size());

        writeHeader(infos);

        for (String acc : entries) {
            writeEntry(acc);
            flush();
        }

        writeFooter();
        flush();
    }

    /** Write header to the output stream (to be overridden by if needed) */
    protected void writeHeader(Map<String, Object> infos) throws IOException {}

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
    public static EntryStreamWriter newAutoCloseableWriter(NextprotMediaType format, String view, OutputStream os, ApplicationContext applicationContext) throws IOException {

        Preconditions.checkNotNull(format);

        switch (format) {

            case XML:
                return new EntryXMLStreamWriter(os, view, applicationContext);
            case TXT:
                return new EntryTXTStreamWriter(os, applicationContext);
            case XLS:
                return EntryXLSWriter.newNPEntryXLSWriter(os, view, applicationContext);
            case JSON:
                return new EntryJSONStreamWriter(os, view, applicationContext);
            case FASTA:
                return new EntryFastaStreamWriter(os, applicationContext);
            case PEFF:
                return new EntryPEFFStreamWriter(os, applicationContext);
            case TURTLE:
                return new EntryTTLStreamWriter(os, view, applicationContext);
            default:
                throw new NextProtException("No NPEntryStreamWriter implementation for " + format);
        }
    }

    public static String getEntryCountKey() {
        return ENTRY_COUNT;
    }

    public static String getIsoformCountKey() {
        return ISOFORM_COUNT;
    }

    public static String getReleaseInfoKey() {
        return RELEASE_INFO;
    }

    public static String getReleaseDataSourcesKey() {
        return RELEASE_DATA_SOURCES;
    }

    public static String getDescriptionKey() {
        return DESCRIPTION;
    }
}
