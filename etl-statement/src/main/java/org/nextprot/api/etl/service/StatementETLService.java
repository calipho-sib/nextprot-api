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
	String extractTransformLoadStatements(StatementSource source, String release, boolean load, boolean erase) throws IOException;

	/**
	 * Extract/Transform/Load statements provided by a source
	 * @param source the statement source
	 * @param release the source release
	 * @param load load to db if true
	 * @param erase erase existing statements in the db if true
	 * @return the log message
	 * @throws IOException
	 */
	String extractTransformLoadStatementsStreaming(StatementSource source, String release, boolean load, boolean erase, boolean dropIndex) throws IOException;

	/**
	 * Drops the indexes of raw and entry mapped tables
	 * @return success/failure
	 */
	String dropIndex();

	/**
	 * Creates the indexes of raw and entry mapped tables
	 * @return success/failure
	 */
	String createIndex();
}
