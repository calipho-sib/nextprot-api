package org.nextprot.api.core.service.export;

import org.nextprot.api.core.domain.ChromosomeReport;

import java.io.IOException;

/**
 * Writer that can write {@code ChromosomeReport}
 *
 * Created by fnikitin on 19.04.17.
 */
public interface ChromosomeReportWriter {

    void write(ChromosomeReport chromosomeReport) throws IOException;
}
