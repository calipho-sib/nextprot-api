package org.nextprot.api.web.service.impl.writer;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.fluent.EntryConfig;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Export entries in JSON format
 *
 * Created by fnikitin on 28/04/15.
 */
public class NPEntryJSONWriter extends NPEntryOutputStreamWriter {

    private final JsonFactory factory;

    public NPEntryJSONWriter(OutputStream os) {

        super(os);

        ObjectMapper mapper = new ObjectMapper();
        factory = mapper.getFactory();
    }

    @Override
    protected void writeEntry(String entryName, String viewName) throws IOException {

        JsonGenerator generator = factory.createGenerator(stream);

        Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryName).with(viewName));
        generator.writeObject(entry);
    }
}
