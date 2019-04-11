package org.nextprot.api.etl.service.impl;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.etl.NextProtSource;
import org.nextprot.api.etl.service.StatementLoaderService;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementField;
import org.nextprot.commons.statements.constants.StatementTableNames;
import org.nextprot.commons.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class JDBCStatementLoaderServiceImpl implements StatementLoaderService {

	@Autowired private DataSourceServiceLocator dataSourceServiceLocator = null;
	
	private String entryTable =  StatementTableNames.ENTRY_TABLE;
	private String rawTable = StatementTableNames.RAW_TABLE;
	

	@Override
	public void loadRawStatementsForSource(Set<Statement> statements, NextProtSource source) throws SQLException {
		load(statements, rawTable, source);
	}

	@Override
	public void loadStatementsMappedToEntrySpecAnnotationsForSource(Set<Statement> statements, NextProtSource source) throws SQLException {
		load(statements, entryTable, source);
	}
	
	private void load(Set<Statement> statements, String tableName, NextProtSource source) throws SQLException {
		
		java.sql.Statement deleteStatement = null;
		PreparedStatement pstmt = null;
		
		try (Connection conn = dataSourceServiceLocator.getStatementsDataSource().getConnection()) {

			
			deleteStatement = conn.createStatement();
			deleteStatement.addBatch("DELETE FROM nxflat." + tableName + " WHERE SOURCE = '" + source.getSourceName() + "'");

			
			String columnNames = StringUtils.mkString(StatementField.values(), "", ",", "");
			List<String> bindVariablesList = new ArrayList<>();
			for (int i=0 ; i<StatementField.values().length; i++) {
				bindVariablesList.add("?");
			}
			String bindVariables = StringUtils.mkString(bindVariablesList, "",",", "");

			pstmt = conn.prepareStatement(
					"INSERT INTO nxflat." + tableName + " (" + columnNames + ") VALUES ( " + bindVariables + ")"
			);

			for (Statement s : statements) {
				for (int i = 0; i < StatementField.values().length; i++) {
					StatementField sf = StatementField.values()[i];
					String value = null;
					if(StatementField.SOURCE.equals(sf)){
						value = source.getSourceName();
					}else value = s.getValue(sf); 
					if (value != null) {
						pstmt.setString(i + 1, value.replace("'", "''"));
					} else {
						pstmt.setNull(i + 1, java.sql.Types.VARCHAR);
					}
				}
				pstmt.addBatch();
			}

			deleteStatement.executeBatch();
			pstmt.executeBatch();

		} finally {

			if(deleteStatement != null){
				deleteStatement.close();
			}
			
			if(pstmt  != null){
				pstmt.close();
			}

		}
		
	}


}