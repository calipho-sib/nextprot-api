package org.nextprot.api.web.service.impl.writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.web.service.ExportService;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import static org.nextprot.api.commons.utils.StringUtils.CR_LF;

    /**
 * Streams entries in TXT format
 *
 * Created by fnikitin on 28/04/15.
 * Modified by dteixeir on 13/07 to include header
 */
public class NPEntryTXTStreamWriter extends NPEntryVelocityBasedStreamWriter {

	private static final Log LOGGER = LogFactory.getLog(NPEntryTXTStreamWriter.class);

    public NPEntryTXTStreamWriter(Writer writer) {

        super(writer, "txt/entry.txt.vm", "accession");
    }

    @Override
    protected void writeHeader(Map<String, Object> params) throws IOException {

        if (params != null && params.containsKey(ExportService.ENTRIES_COUNT_PARAM))
            getStream().write("#nb entries=" + params.get(ExportService.ENTRIES_COUNT_PARAM) + CR_LF);
    	else
            LOGGER.warn("Entries count parameter not found, header discarded...");
    }

    @Override
    protected void writeEntry(String entryName) throws IOException {

        streamWithVelocityTemplate(entryName);
    }
}
