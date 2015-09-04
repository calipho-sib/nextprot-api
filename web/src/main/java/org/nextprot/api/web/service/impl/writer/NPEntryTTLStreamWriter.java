package org.nextprot.api.web.service.impl.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.nextprot.api.web.NXVelocityContext;

/**
 * Created by dteixeira
 */
public class NPEntryTTLStreamWriter extends NPEntryVelocityBasedStreamWriter {

    private static final Log LOGGER = LogFactory.getLog(NPEntryTTLStreamWriter.class);

    public NPEntryTTLStreamWriter(Writer writer, String viewName) {
        super(writer, "turtle/entry.ttl.vm", viewName);
    }
    
    public NPEntryTTLStreamWriter(OutputStream os, String viewName) {
        this(new OutputStreamWriter(os), viewName);
    }


    @Override
    protected void writeEntry(String entryName) throws IOException {
        streamWithVelocityTemplate(entryName);
    }

    @Override
    protected void writeHeader(Map<String, Object> params) throws IOException {
        Template headerTemplate = velocityConfig.getVelocityEngine().getTemplate("turtle/prefix.ttl.vm");
        headerTemplate.merge(new NXVelocityContext(params), getStream());
    }

}
