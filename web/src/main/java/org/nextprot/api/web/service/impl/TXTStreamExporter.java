package org.nextprot.api.web.service.impl;

import org.apache.velocity.Template;

import java.io.IOException;
import java.io.Writer;

/**
 * Export entries in TXT format
 *
 * Created by fnikitin on 28/04/15.
 */
public class TXTStreamExporter extends NPStreamExporter {

    private final Template template;

    TXTStreamExporter() {

        template = velocityConfig.getVelocityEngine().getTemplate("txt/entry.txt.vm");
    }

    @Override
    protected void exportStream(String entryName, Writer writer, String viewName) throws IOException {

        streamWithVelocityTemplate(template, entryName, writer, "accession");
    }
}
