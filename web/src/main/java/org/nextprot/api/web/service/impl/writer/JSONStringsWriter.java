package org.nextprot.api.web.service.impl.writer;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

/**
 * Export list of strings in JSON format
 */
public class JSONStringsWriter {

    private final JsonGenerator generator;

    public JSONStringsWriter(OutputStream os) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getFactory();
        generator = factory.createGenerator(os);
    }

    public void write(String string) throws IOException {

        generator.writeObject(string);
    }

    public void write(Collection<String> strings) throws IOException {

        generator.writeObject(strings);
    }

    public void close() throws IOException {

        generator.close();
    }
}
