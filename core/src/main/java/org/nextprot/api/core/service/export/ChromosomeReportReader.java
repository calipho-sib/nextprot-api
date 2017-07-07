package org.nextprot.api.core.service.export;

import org.nextprot.api.core.domain.ChromosomeReport;

import java.io.Reader;
import java.text.ParseException;

/**
 * Reader parse String into {@code ChromosomeReport}
 *
 * Created by fnikitin on 19.04.17.
 */
public interface ChromosomeReportReader {

    ChromosomeReport read(Reader reader) throws ParseException;
}
