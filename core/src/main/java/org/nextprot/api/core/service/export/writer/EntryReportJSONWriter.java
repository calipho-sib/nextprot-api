package org.nextprot.api.core.service.export.writer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.POJONode;
import org.nextprot.api.core.domain.EntryReport;
import org.nextprot.api.core.service.export.EntryReportWriter;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Writes {@code EntryReport}s in JSON format
 *
 * Created by fnikitin on 19.04.17.
 */
public class EntryReportJSONWriter extends EntryReportWriter {

    private final ObjectMapper mapper;
    private final ArrayNode jsonArray;

    public EntryReportJSONWriter(OutputStream os) throws IOException {

        super(os);
        mapper = new ObjectMapper();
        jsonArray = mapper.createArrayNode();
    }

    @Override
    protected void writeEntryReport(EntryReport entryReport) throws IOException {

        jsonArray.add(new POJONode(entryReport));
    }

    @Override
    public void flush() throws IOException {

        mapper.writerWithDefaultPrettyPrinter().writeValue(os, jsonArray);
    }

    @Override
    public void close() throws IOException {

        super.close();
    }
}
