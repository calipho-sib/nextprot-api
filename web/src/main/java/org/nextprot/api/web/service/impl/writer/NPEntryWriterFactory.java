package org.nextprot.api.web.service.impl.writer;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.service.export.format.NPFileFormat;

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
    public static NPEntryWriter newNPEntryStreamWriter(NPFileFormat format, OutputStream os) throws IOException {

        Preconditions.checkNotNull(format);
        Preconditions.checkNotNull(os);

        switch (format) {

            case XML:
                return new NPEntryXMLWriter(new OutputStreamWriter(os, UTF_8));
            case TXT:
                return new NPEntryTXTWriter(new OutputStreamWriter(os, UTF_8));
            case XLS:
                return new NPEntryXLSWriter(os);
            case JSON:
                return new NPEntryJSONWriter(os);
            case FASTA:
                return new NPEntryFastaWriter(new OutputStreamWriter(os, UTF_8));
            case PEFF:
                return new NPEntryPeffWriter(new OutputStreamWriter(os, UTF_8));
            default:
                throw new NextProtException("No NPStreamExporter implementation for "+format);
        }
    }
}
