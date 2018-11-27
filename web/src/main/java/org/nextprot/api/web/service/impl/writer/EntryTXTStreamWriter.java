package org.nextprot.api.web.service.impl.writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import static org.nextprot.api.commons.utils.StringUtils.CR_LF;

/**
 * Streams entries in TXT format
 *
 * Created by fnikitin on 28/04/15.
 * Modified by dteixeir on 13/07 to include header
 */
public class EntryTXTStreamWriter extends EntryVelocityBasedStreamWriter {

	private static final Log LOGGER = LogFactory.getLog(EntryTXTStreamWriter.class);

    public EntryTXTStreamWriter(OutputStream os, ApplicationContext applicationContext) throws IOException {

        this(new OutputStreamWriter(os, UTF_8), applicationContext);
    }

    public EntryTXTStreamWriter(Writer writer, ApplicationContext applicationContext) {

        super(writer, "txt/entry.txt.vm", "accession", applicationContext);
    }

    @Override
    protected void writeHeader(Map<String, Object> infos) throws IOException {

        int entryNum = (int) infos.get(EntryStreamWriter.getEntryCountKey());

        if (entryNum > 0)
            getStream().write("#nb entries=" + entryNum + CR_LF);
    	else
            LOGGER.warn("Entries count parameter not found, header discarded...");
    }

    @Override
    protected void writeEntry(String entryName) throws IOException {

        streamWithVelocityTemplate(entryName);
    }
}
