package org.nextprot.api.web.service.impl.writer;

import org.nextprot.api.core.domain.Entry;

import java.io.IOException;
import java.io.Writer;

/**
 * Export entries sequence in PEFF (extended FASTA) format
 *
 * Created by fnikitin on 28/04/15.
 */
public class NPEntryPeffWriter extends NPEntryVelocityBasedWriter {

    public NPEntryPeffWriter(Writer writer) {

        super(writer, "peff/entry.peff.vm");
    }

    @Override
    protected void writeEntry(String entryName, String viewName) throws IOException {

        streamWithVelocityTemplate(entryName, "isoform", "entry");
    }

    @Override
    protected void handleEntry(Entry entry) {

        //IsoformPTMPsiPeffFormatter.addPsiModIdsToMap(entry, terminologyMapper);
    }
}
