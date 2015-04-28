package org.nextprot.api.web.service.impl;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.export.format.NPFileFormat;
import org.nextprot.api.core.service.fluent.FluentEntryService;

import java.io.IOException;
import java.io.Writer;

/**
 * Export entries in JSON format
 *
 * Created by fnikitin on 28/04/15.
 */
class JSONStreamExporter extends AbstractStreamExporter {

    private final JsonGenerator generator;

    public JSONStreamExporter(Writer writer, FluentEntryService fluentEntryService) throws IOException {
        
        super(NPFileFormat.JSON, writer, fluentEntryService);

        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getFactory();
        generator = factory.createGenerator(writer);
    }

    @Override
    protected void exportStream(String entryName, String viewName) throws IOException {

        Entry entry = fluentEntryService.newFluentEntry(entryName).buildWithView(viewName);
        generator.writeObject(entry);
    }
}
