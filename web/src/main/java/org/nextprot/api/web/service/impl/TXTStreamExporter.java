	package org.nextprot.api.web.service.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.nextprot.api.web.service.ExportService;

/**
 * Export entries in TXT format
 *
 * Created by fnikitin on 28/04/15.
 * Modified by dteixeir on 13/07 to include header
 */
public class TXTStreamExporter extends NPStreamExporter {

	private static final Log LOGGER = LogFactory.getLog(TXTStreamExporter.class);

    private final Template template;

    public TXTStreamExporter() {

        template = velocityConfig.getVelocityEngine().getTemplate("txt/entry.txt.vm");
    }

    @Override
    protected void writeHeader(Writer writer, Map<String, Object> params) throws IOException {
    	if((params != null) & params.containsKey(ExportService.ENTRIES_COUNT_PARAM)){
        	writer.write("#nb entries=" + params.get(ExportService.ENTRIES_COUNT_PARAM) + "\n");
    	}else LOGGER.warn("Entries count parameter not found, header discarded...");
    	
    }

    @Override
    protected void exportStream(String entryName, Writer writer, String viewName) throws IOException {

        streamWithVelocityTemplate(template, entryName, writer, "accession");
    }
}
