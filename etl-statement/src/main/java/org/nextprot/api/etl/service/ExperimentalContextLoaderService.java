package org.nextprot.api.etl.service;

import org.nextprot.api.core.app.StatementSource;

/**
 * Service which loads experimental contexts derived from a given source, reading from a file in the given release folder
 */
public interface ExperimentalContextLoaderService {

    String loadExperimentalContexts(StatementSource source, String release, boolean load);
}
