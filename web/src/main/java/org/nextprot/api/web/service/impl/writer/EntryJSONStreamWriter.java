package org.nextprot.api.web.service.impl.writer;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.fluent.EntryConfig;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Export entries in JSON format
 *
 * Created by fnikitin on 28/04/15.
 */
public class EntryJSONStreamWriter extends EntryOutputStreamWriter {

    private final JsonGenerator generator;
    private final String viewName;

    public EntryJSONStreamWriter(OutputStream os, String viewName) throws IOException {

        super(os);

        Preconditions.checkNotNull(viewName);

        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getFactory();
        generator = factory.createGenerator(os);
        generator.writeStartArray();

        this.viewName = viewName;
    }

    @Override
    protected void writeEntry(String entryName) throws IOException {

        Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryName).with(viewName));
        generator.writeObject(entry);
    }

    @Override
    public void close() throws IOException {

        generator.writeEndArray();
        generator.close();
    }
}
