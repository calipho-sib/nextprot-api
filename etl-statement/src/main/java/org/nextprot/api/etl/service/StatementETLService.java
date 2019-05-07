package org.nextprot.api.etl.service;


import org.nextprot.api.etl.NextProtSource;

import java.io.IOException;

/**
 * Extract, transform, load Statements from Json data source to nxflat database
 */
public interface StatementETLService {

	String etlStatements(NextProtSource source, String release, boolean load) throws IOException;
}
