package org.nextprot.api.etl.service;


import org.nextprot.api.etl.StatementSourceEnum;

import java.io.IOException;

/**
 * Extract, transform, load Statements from Json data source to nxflat database
 */
public interface StatementETLService {

	/**
	 * Extract/Transform/Load statements provided by a source
	 * @param source the statement source
	 * @param release the source release
	 * @param load load to db if true
	 * @return the log message
	 * @throws IOException
	 */
	String extractTransformLoadStatements(StatementSourceEnum source, String release, boolean load) throws IOException;
}
