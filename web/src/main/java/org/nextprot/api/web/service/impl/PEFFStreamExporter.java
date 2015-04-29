package org.nextprot.api.web.service.impl;

import org.apache.velocity.Template;

import java.io.IOException;
import java.io.Writer;

/**
 * Export entries sequence in PEFF (extended FASTA) format
 *
 * Created by fnikitin on 28/04/15.
 */
public class PeffStreamExporter extends NPStreamExporter {

    private final Template template;

    PeffStreamExporter() {

        template = velocityConfig.getVelocityEngine().getTemplate("peff/entry.peff.vm");
    }

    @Override
    protected void exportStream(String entryName, Writer writer, String viewName) throws IOException {

        streamWithVelocityTemplate(template, entryName, writer, "isoform", "overview");
    }
}
