package org.nextprot.api.core.service.export;

import org.nextprot.api.core.domain.ChromosomeReport;
import org.nextprot.api.core.service.OverviewService;
import org.nextprot.api.core.service.export.format.NextprotMediaType;
import org.nextprot.api.core.service.export.io.HPPChromosomeReportTSVWriter;
import org.nextprot.api.core.service.export.io.HPPChromosomeReportTXTWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

/**
 * Writer that can write HPP {@code ChromosomeReport}
 */
public interface HPPChromosomeReportWriter {

    void write(ChromosomeReport chromosomeReport) throws IOException;

    static Optional<HPPChromosomeReportWriter> valueOf(NextprotMediaType mediaType, OutputStream os, OverviewService overviewService) {

        if (mediaType == NextprotMediaType.TSV) {
            return Optional.of(new HPPChromosomeReportTSVWriter(os, overviewService));
        }
        else if (mediaType == NextprotMediaType.TXT) {
            return Optional.of(new HPPChromosomeReportTXTWriter(os, overviewService));
        }
        return Optional.empty();
    }
}
