package org.nextprot.api.web.service.impl.writer;

import org.nextprot.api.core.domain.Entry;

import java.io.IOException;
import java.io.Writer;

/**
 * Streams entries sequence in PEFF (extended FASTA) format
 *
 * Created by fnikitin on 28/04/15.
 */
public class NPEntryPeffStreamWriter extends NPEntryVelocityBasedStreamWriter {

    public NPEntryPeffStreamWriter(Writer writer) {

        super(writer, "peff/entry.peff.vm", "entry");
    }

    @Override
    protected void writeEntry(String entryName) throws IOException {

        streamWithVelocityTemplate(entryName, "isoform");
    }

    @Override
    protected void handleEntry(Entry entry) {

        //IsoformPTMPsiPeffFormatter.addPsiModIdsToMap(entry, terminologyMapper);
    }
}
