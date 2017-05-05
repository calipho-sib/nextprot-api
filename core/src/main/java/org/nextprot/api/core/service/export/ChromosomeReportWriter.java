package org.nextprot.api.core.service.export;

import org.nextprot.api.core.domain.ChromosomeReport;
import org.nextprot.api.core.service.export.format.NextprotMediaType;
import org.nextprot.api.core.service.export.writer.ChromosomeReportTSVWriter;
import org.nextprot.api.core.service.export.writer.ChromosomeReportTXTWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

/**
 * Writer that can write {@code ChromosomeReport}
 *
 * Created by fnikitin on 19.04.17.
 */
public interface ChromosomeReportWriter {

    void write(ChromosomeReport chromosomeReport) throws IOException;

    static Optional<ChromosomeReportWriter> valueOf(NextprotMediaType mediaType, OutputStream os) {

        if (mediaType == NextprotMediaType.TSV) {
            return Optional.of(new ChromosomeReportTSVWriter(os));
        }
		else if (mediaType == NextprotMediaType.TXT) {
            return Optional.of(new ChromosomeReportTXTWriter(os));
        }
        return Optional.empty();
    }
}
