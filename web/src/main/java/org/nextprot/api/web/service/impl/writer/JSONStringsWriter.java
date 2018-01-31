package org.nextprot.api.web.service.impl.writer;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

/**
 * Export list of strings in JSON format
 */
public class JSONStringsWriter {

    private final ObjectMapper mapper;
    private final OutputStream os;

    public JSONStringsWriter(OutputStream os) {

        mapper = new ObjectMapper();
        this.os = os;
    }

    public void write(Collection<String> strings) throws IOException {

        mapper.writeValue(os, strings);
    }
}
