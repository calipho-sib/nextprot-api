package org.nextprot.api.commons.spring.jdbc.impl;

import javax.sql.DataSource;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;

public class DataSourceServiceLocatorImpl implements DataSourceServiceLocator {
	
	private DataSource dataSource;
	private DataSource userDataSource;
	private DataSource statementsDataSource;
	
	public DataSource getStatementsDataSource() {
		return statementsDataSource;
	}
	public void setStatementsDataSource(DataSource statementsDataSource) {
		this.statementsDataSource = statementsDataSource;
	}
	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	public DataSource getUserDataSource() {
		return userDataSource;
	}
	public void setUserDataSource(DataSource userDataSource) {
		this.userDataSource = userDataSource;
	}

}
