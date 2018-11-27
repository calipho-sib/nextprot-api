package org.nextprot.api.web.service.impl.writer;

import org.springframework.context.ApplicationContext;

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

    public EntryFastaStreamWriter(OutputStream os, ApplicationContext applicationContext) throws IOException {

        this(new OutputStreamWriter(os, UTF_8), applicationContext);
    }

    public EntryFastaStreamWriter(Writer writer, ApplicationContext applicationContext) {

        super(writer, "fasta/entry.fasta.vm", "overview", applicationContext);
    }

    @Override
    protected void writeEntry(String entryName) throws IOException {

        streamWithVelocityTemplate(entryName, "isoform");
    }
}
