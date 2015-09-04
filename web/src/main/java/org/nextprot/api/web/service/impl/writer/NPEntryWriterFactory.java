package org.nextprot.api.web.service.impl.writer;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.service.export.format.FileFormat;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * A factory to build instances of NPEntryWriter
 *
 * Created by fnikitin on 11/08/15.
 */
public class NPEntryWriterFactory {

    private static final String UTF_8 = "UTF-8";

    /**
     * Create new instance of streaming NPEntryWriter
     *
     * @param format the output file format
     * @param os the output stream
     * @return a NPEntryWriter instance
     * @throws UnsupportedEncodingException
     */
    public static NPEntryStreamWriter newNPEntryStreamWriter(FileFormat format, String view, OutputStream os) throws IOException {

        Preconditions.checkNotNull(format);
        Preconditions.checkNotNull(os);

        switch (format) {

            case XML:
                return new NPEntryXMLStreamWriter(new OutputStreamWriter(os, UTF_8), view);
            case TXT:
                return new NPEntryTXTStreamWriter(new OutputStreamWriter(os, UTF_8));
            case XLS:
                return NPEntryXLSWriter.newNPEntryXLSWriter(os, view);
            case JSON:
                return new NPEntryJSONStreamWriter(os, view);
            case FASTA:
                return new NPEntryFastaStreamWriter(new OutputStreamWriter(os, UTF_8));
            case PEFF:
                return new NPEntryPeffStreamWriter(new OutputStreamWriter(os, UTF_8));
            case TURTLE:
                return new NPEntryTTLStreamWriter(new OutputStreamWriter(os, UTF_8), view);
            default:
                throw new NextProtException("No NPEntryStreamWriter implementation for "+format);
        }
    }
}
