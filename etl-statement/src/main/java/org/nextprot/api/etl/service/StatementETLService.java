package org.nextprot.api.etl.service;


import java.io.IOException;

import org.nextprot.api.core.app.StatementSource;

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
	String extractTransformLoadStatements(StatementSource source, String release, boolean load) throws IOException;

	/**
	 * Extract/Transform/Load statements provided by a source
	 * @param source the statement source
	 * @param release the source release
	 * @param load load to db if true
	 * @return the log message
	 * @throws IOException
	 */
	String extractTransformLoadStatementsStreaming(StatementSource source, String release, boolean load) throws IOException;
}
