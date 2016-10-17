package org.nextprot.api.web.service.impl.writer;

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

    public EntryFastaStreamWriter(OutputStream os) throws IOException {

        this(new OutputStreamWriter(os, UTF_8));
    }

    public EntryFastaStreamWriter(Writer writer) {

        super(writer, "fasta/entry.fasta.vm", "overview");
    }

    @Override
    protected void writeEntry(String entryName) throws IOException {

        streamWithVelocityTemplate(entryName, "isoform");
    }
}
