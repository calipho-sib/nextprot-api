package org.nextprot.api.core.service.export.io;

import org.nextprot.api.core.domain.SlimIsoform;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Writes {@code SlimIsoform} in TSV format
 */
public class SlimIsoformTSVWriter {

    private final PrintWriter writer;

    public SlimIsoformTSVWriter(OutputStream os) {

        this.writer = new PrintWriter(os);

        writer.write(Stream.of("isoform", "md5", "sequence")
                .collect(Collectors.joining("\t")));
        writer.write("\n");
    }

    public void write(Collection<SlimIsoform> isoforms) {

        isoforms.forEach(isoform -> {
            writer.write(Stream.of(isoform.getAccession(), isoform.getMd5(), isoform.getSequence())
                    .collect(Collectors.joining("\t")));
            writer.write("\n");
        });
    }

    public void close() {

        writer.close();
    }
}
