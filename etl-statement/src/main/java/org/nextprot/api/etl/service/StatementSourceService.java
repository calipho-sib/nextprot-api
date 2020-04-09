package org.nextprot.api.etl.service;

import java.io.IOException;
import java.util.Set;

import org.nextprot.api.core.app.StatementSource;

/**
 * Provide statements from Json files for a specific source and release
 */
public interface StatementSourceService {

	Set<String> getJsonFilenamesForRelease(StatementSource source, String release) throws IOException;

	String getStatementsAsJsonArray(StatementSource source, String release, String jsonFileName) throws IOException;
}
