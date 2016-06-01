package org.nextprot.api.commons.spring.jdbc;

import javax.sql.DataSource;

public interface DataSourceServiceLocator {

	/**
	 * Gets data source for conventional data
	 * @return
	 */
	public DataSource getDataSource();

	/**
	 * Gets the data source related to user data (different database)
	 * @return
	 */
	public DataSource getUserDataSource();


	/**
	 * Gets the data source related to the statements
	 * @return
	 */
	public DataSource getStatementsDataSource();
	
}
