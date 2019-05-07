package org.nextprot.api.etl.service;

import org.nextprot.api.etl.NextProtSource;

import java.io.IOException;
import java.util.Set;

public interface StatementSourceService {

	Set<String> getJsonFilenamesForRelease(NextProtSource source, String release) throws IOException;

	String getStatementJsonContent(NextProtSource source, String release, String jsonFileName) throws IOException;
}
