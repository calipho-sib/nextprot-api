package org.nextprot.api.etl.service.impl;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.etl.NextProtSource;
import org.nextprot.api.etl.service.StatementLoaderService;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.constants.StatementTableNames;
import org.nextprot.commons.statements.specs.CoreStatementField;
import org.nextprot.commons.statements.specs.StatementField;
import org.nextprot.commons.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Service
public class JDBCStatementLoaderServiceImpl implements StatementLoaderService {

	@Autowired private DataSourceServiceLocator dataSourceServiceLocator = null;
	
	private String entryTable =  StatementTableNames.ENTRY_TABLE;
	private String rawTable = StatementTableNames.RAW_TABLE;
	

	@Override
	public void loadRawStatementsForSource(Collection<Statement> statements, NextProtSource source) throws SQLException {
		load(statements, rawTable, source);
	}

	@Override
	public void loadStatementsMappedToEntrySpecAnnotationsForSource(Collection<Statement> statements, NextProtSource source) throws SQLException {
		load(statements, entryTable, source);
	}
	
	private void load(Collection<Statement> statements, String tableName, NextProtSource source) throws SQLException {
		
		java.sql.Statement deleteStatement = null;
		PreparedStatement pstmt = null;
		
		try (Connection conn = dataSourceServiceLocator.getStatementsDataSource().getConnection()) {

			
			deleteStatement = conn.createStatement();
			deleteStatement.addBatch("DELETE FROM nxflat." + tableName + " WHERE SOURCE = '" + source.getSourceName() + "'");

			
			String columnNames = StringUtils.mkString(CoreStatementField.values(), "", ",", "");
			List<String> bindVariablesList = new ArrayList<>();
			for (int i=0 ; i<CoreStatementField.values().length; i++) {
				bindVariablesList.add("?");
			}
			String bindVariables = StringUtils.mkString(bindVariablesList, "",",", "");

			pstmt = conn.prepareStatement(
					"INSERT INTO nxflat." + tableName + " (" + columnNames + ") VALUES ( " + bindVariables + ")"
			);

			for (Statement s : statements) {
				for (int i = 0; i < CoreStatementField.values().length; i++) {
					StatementField sf = CoreStatementField.values()[i];
					String value = null;
					if(CoreStatementField.SOURCE.equals(sf)){
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