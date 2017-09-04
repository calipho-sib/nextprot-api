package org.nextprot.api.web.service.impl.writer;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.release.ReleaseInfo;
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
    protected void writeHeader(int entryNum, ReleaseInfo releaseInfo, String description) throws IOException {

        StringBuilder sb = new StringBuilder();

        sb
                .append("# PEFF 1.0\n")
                .append("# DbName=neXtProt\n")
                .append("# DbSource=https://www.nextprot.org\n")
                .append("# DbVersion=").append(releaseInfo.getDatabaseRelease()).append("\n")
                .append("# DbDescription=").append(description).append("\n")
                .append("# Prefix=nxp\n")
                .append("# NumberOfEntries=").append(entryNum).append("\n")
                .append("# SequenceType=AA\n\n")
        ;

        getStream().write(sb.toString());
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
