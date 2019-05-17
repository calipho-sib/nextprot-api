package org.nextprot.api.etl.service;

import org.nextprot.api.etl.StatementSourceEnum;

import java.io.IOException;
import java.util.Set;

/**
 * Provide statements from Json files for a specific source and release
 */
public interface StatementSourceService {

	Set<String> getJsonFilenamesForRelease(StatementSourceEnum source, String release) throws IOException;

	String getStatementsAsJsonArray(StatementSourceEnum source, String release, String jsonFileName) throws IOException;
}
