package org.nextprot.api.web.service.impl.writer;

import java.io.IOException;
import java.io.Writer;

/**
 * Export entries sequence in FASTA format
 *
 * Created by fnikitin on 28/04/15.
 */
public class NPEntryFastaWriter extends NPEntryVelocityBasedWriter {

    public NPEntryFastaWriter(Writer writer) {

        super(writer, "fasta/entry.fasta.vm");
    }

    @Override
    protected void writeEntry(String entryName, String viewName) throws IOException {

        streamWithVelocityTemplate(entryName, "isoform", "overview");
    }
}
