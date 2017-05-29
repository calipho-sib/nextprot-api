package org.nextprot.api.core.service.export;

import org.nextprot.api.core.domain.ChromosomeReport;

import java.io.IOException;

/**
 * Writer that can write HPP {@code ChromosomeReport}
 */
public interface HPPChromosomeReportWriter {

    void write(ChromosomeReport chromosomeReport) throws IOException;
}
