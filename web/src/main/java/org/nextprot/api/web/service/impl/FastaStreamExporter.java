package org.nextprot.api.web.service.impl;

import org.apache.velocity.Template;
import org.nextprot.api.core.service.export.format.NPFileFormat;
import org.nextprot.api.core.service.fluent.FluentEntryService;
import org.springframework.web.servlet.view.velocity.VelocityConfig;

import java.io.IOException;
import java.io.Writer;

/**
 * Export entries sequence in FASTA format
 *
 * Created by fnikitin on 28/04/15.
 */
class FastaStreamExporter extends AbstractStreamExporter {

    private final VelocityConfig velocityConfig;

    public FastaStreamExporter(Writer writer, FluentEntryService fluentEntryService, VelocityConfig velocityConfig) {
        
        super(NPFileFormat.FASTA, writer, fluentEntryService);

        this.velocityConfig = velocityConfig;
    }

    @Override
    protected void exportStream(String entryName, String viewName) throws IOException {

        Template template = velocityConfig.getVelocityEngine().getTemplate("fasta/entry.fasta.vm");

        streamWithVelocityTemplate(template, entryName, "isoform", "overview");
    }
}
