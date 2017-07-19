package org.nextprot.api.web.service.impl.writer;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.web.NXVelocityContext;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Streams entries sequence in PEFF (extended FASTA) format
 *
 * Created by fnikitin on 28/04/15.
 */
public class EntryPeffStreamWriter extends EntryVelocityBasedStreamWriter {

    public EntryPeffStreamWriter(OutputStream os) throws IOException {

        this(new OutputStreamWriter(os, UTF_8));
    }

    public EntryPeffStreamWriter(Writer writer) {

        super(writer, "peff/entry.peff.vm", "entry");
    }

    @Override
    protected void writeEntry(String entryName) throws IOException {

        streamWithVelocityTemplate(entryName, "isoform");
    }

    @Override
    protected NXVelocityContext newNXVelocityContext(Entry entry) {

        NXVelocityContext velocityContext = super.newNXVelocityContext(entry);

        velocityContext.add("peffByIsoform", entryReportService.reportIsoformPeffHeaders(entry.getUniqueName()));

        return velocityContext;
    }
}
