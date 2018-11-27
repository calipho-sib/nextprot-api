package org.nextprot.api.web.service.impl.writer;

import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.EntryReportStatsService;
import org.springframework.web.servlet.view.velocity.VelocityConfig;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Streams entries sequence in FASTA format
 *
 * Created by fnikitin on 28/04/15.
 */
public class EntryFastaStreamWriter extends EntryVelocityBasedStreamWriter {

    public EntryFastaStreamWriter(OutputStream os, EntryBuilderService entryBuilderService,
                                  EntryReportStatsService entryReportStatsService,
                                  VelocityConfig velocityConfig) throws IOException {

        this(new OutputStreamWriter(os, UTF_8), entryBuilderService, entryReportStatsService, velocityConfig);
    }

    public EntryFastaStreamWriter(Writer writer, EntryBuilderService entryBuilderService,
                                  EntryReportStatsService entryReportStatsService,
                                  VelocityConfig velocityConfig) {

        super(writer, "fasta/entry.fasta.vm", "overview", entryBuilderService, entryReportStatsService, velocityConfig);
    }

    @Override
    protected void writeEntry(String entryName) throws IOException {

        streamWithVelocityTemplate(entryName, "isoform");
    }
}
