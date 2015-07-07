package org.nextprot.api.web.service.impl;

import java.io.IOException;
import java.io.Writer;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.fluent.EntryConfig;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Export entries in JSON format
 *
 * Created by fnikitin on 28/04/15.
 */
public class JSONStreamExporter extends NPStreamExporter {

    private final JsonFactory factory;

    public JSONStreamExporter() {
        
        ObjectMapper mapper = new ObjectMapper();
        factory = mapper.getFactory();
    }

    @Override
    protected void exportStream(String entryName, Writer writer, String viewName) throws IOException {

        JsonGenerator generator = factory.createGenerator(writer);

        Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryName).with(viewName));
        generator.writeObject(entry);
    }
}
