package org.nextprot.api.etl.service;


import org.nextprot.api.etl.StatementSource;

import java.io.IOException;

/**
 * Extract, transform, load Statements from Json data source to nxflat database
 */
public interface StatementETLService {

	String extractTransformLoadStatements(StatementSource source, String release, boolean load) throws IOException;
}
