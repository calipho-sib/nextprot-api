package org.nextprot.api.etl.service.impl;

import org.apache.log4j.Logger;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.core.app.StatementSource;
import org.nextprot.api.etl.service.StatementLoaderService;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.constants.StatementTableNames;
import org.nextprot.commons.statements.specs.CoreStatementField;
import org.nextprot.commons.statements.specs.CustomStatementField;
import org.nextprot.commons.statements.specs.StatementField;
import org.nextprot.commons.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class JDBCStatementLoaderServiceImpl implements StatementLoaderService {

	protected static final Logger LOGGER = Logger.getLogger(JDBCStatementLoaderServiceImpl.class);

	@Autowired private DataSourceServiceLocator dataSourceServiceLocator = null;
	
	private String entryMappedTable =  StatementTableNames.ENTRY_TABLE;
	private String rawTable = StatementTableNames.RAW_TABLE;

	private final String entryMappedAccessionIndex = "CREATE INDEX idx_ems_by_entry_ac ON nxflat.entry_mapped_statements USING btree (entry_accession)";
	private final String entryMappedAnnotationIndex = "CREATE INDEX idx_ems_by_annot_id ON nxflat.entry_mapped_statements USING btree (annotation_id)";

	private final String rawAccessionIndex = "CREATE INDEX new_raw_statem_entry_ac_idx ON nxflat.raw_statements USING btree (entry_accession)";
	private final String rawAnnotationIndex = "CREATE INDEX statem_annot_id_idx ON nxflat.raw_statements USING btree (annotation_id)";


	@Override
	public void loadRawStatementsForSource(Collection<Statement> statements, StatementSource source) throws SQLException {
		load(statements, rawTable, source);
	}

	@Override
	public void loadEntryMappedStatementsForSource(Collection<Statement> statements, StatementSource source) throws SQLException {
		load(statements, entryMappedTable, source);
	}

	@Override
	public void deleteRawStatements(StatementSource source)  {
		deleteStatements(rawTable, source);
		
	}

	@Override
	public void deleteEntryMappedStatements(StatementSource source)  {
		deleteStatements(entryMappedTable, source);
	}

		
	private void deleteStatements(String tableName, StatementSource source)  {

		java.sql.Statement delStmt = null; ResultSet rs = null;
		
		try (Connection conn = dataSourceServiceLocator.getStatementsDataSource().getConnection())  {
			
			String cntSql = "select count(*) as cnt FROM nxflat." + tableName + " WHERE SOURCE = '" + source.getSourceName() + "'";
			System.out.println(new Date() + " - Counting "  + source.getSourceName() + " statements in " + tableName + "...");
			rs = conn.createStatement().executeQuery(cntSql);
			rs.next();
			long cnt = rs.getLong("cnt");
			System.out.println(new Date() + " - Number of " + source.getSourceName() + " statements in " + tableName + " : " + cnt);
			if (cnt > 0) {
				System.out.println(new Date() + " - Deleting "  + source.getSourceName() + " statements in " + tableName + "...");
				String delSql = "DELETE FROM nxflat." + tableName + " WHERE SOURCE = '" + source.getSourceName() + "'";
				conn.createStatement().execute(delSql);
				System.out.println(new Date() + " - Deleted "  + source.getSourceName() + " statements in " + tableName );
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			
		} finally {
			try {
				if (rs != null) rs.close();
				if (delStmt != null) delStmt.close();	
				
	        } catch (SQLException e) {	
	        	e.printStackTrace();        
	        }

		}

		
	}
	
	private void load(Collection<Statement> statements, String tableName, StatementSource source) throws SQLException {

		List<StatementField> customFields = source.getFields().stream()
				.filter(field -> field instanceof CustomStatementField)
				.collect(Collectors.toList());
		
		List<StatementField> coreFields = source.getFields().stream()
				.filter(field -> field instanceof CoreStatementField)
				.collect(Collectors.toList());

		PreparedStatement pstmt = null;

		try (Connection conn = dataSourceServiceLocator.getStatementsDataSource().getConnection())  {

			String columnNames = StringUtils.mkString(coreFields, "", ",", "");
			if (customFields.size()>0) columnNames += ",EXTRA_FIELDS";
			LOGGER.debug("JDBCStatementLoaderServiceImpl columnsNames: "+columnNames);
			List<String> bindVariablesList = new ArrayList<>();
			for (int i=0 ; i<coreFields.size(); i++) bindVariablesList.add("?");
			if (customFields.size()>0) bindVariablesList.add("?");
			String bindVariables = StringUtils.mkString(bindVariablesList, "",",", "");
			LOGGER.debug("JDBCStatementLoaderServiceImpl bindVariables: "+bindVariables);

			pstmt = conn.prepareStatement(
					"INSERT INTO nxflat." + tableName + " (" + columnNames + ") VALUES ( " + bindVariables + ")"
			);

			for (Statement s : statements) {
				int i=1;
				for (StatementField sf : coreFields) {
					String value = CoreStatementField.SOURCE.equals(sf) ? source.getSourceName() : s.getValue(sf);
					pstmt.setString(i, value==null ? null :  value.replace("'", "''"));
					i++;
				}
				if (!customFields.isEmpty()) {
					Map<String,String> extraMap = new HashMap<>();
					for (StatementField sf : customFields) extraMap.put(sf.getName(), s.getValue(sf));
					String extraValue = StringUtils.serializeAsJsonStringOrNull(extraMap);
					LOGGER.debug("JDBCStatementLoaderServiceImplextraValue:" + extraValue);
					pstmt.setString(i, extraValue==null ? null : extraValue.replace("'", "''"));
				}
				pstmt.addBatch();
			}

			pstmt.executeBatch();

		} catch(Exception e) {
			e.printStackTrace();

		} finally {
			if(pstmt  != null) pstmt.close();
		}
	}

	/**
	 * Drops the indices on raw and entry mapped tables
	 * @return List of index definitions
	 */
	@Override
	public List<String> dropIndexes() {
		List<String> indexDefinitions = new ArrayList<>();

		try (Connection conn = dataSourceServiceLocator.getStatementsDataSource().getConnection())  {
			java.sql.Statement dropStatement = conn.createStatement();
			// Read the indexname and indexdef from the pg_indexes table
            // Index def is the SQL statement, which created the index, so that this can be used to recreate the index
			ResultSet indices = conn.createStatement().executeQuery("SELECT indexname,indexdef FROM pg_indexes WHERE tablename = 'raw_statements'");
			while(indices.next()) {
				// Drop each index
				String indexName = indices.getString(1);
				String indexDefinition = indices.getString(2);
				dropStatement.executeUpdate("DROP INDEX nxflat." + indexName);
				LOGGER.info("Dropped the index " + indexName + " on " + rawTable);
				indexDefinitions.add(indexDefinition);
			}

			// Raw table
			ResultSet entryMappedIndices = conn.createStatement().executeQuery("SELECT indexname,indexdef FROM pg_indexes WHERE tablename = 'entry_mapped_statements'");
			while(entryMappedIndices.next()) {
				// Drop each index
				String indexName = entryMappedIndices.getString(1);
				String indexDefinition = entryMappedIndices.getString(2);
				dropStatement.executeUpdate("DROP INDEX nxflat." + indexName);
				LOGGER.info("Dropped the index " + indexName + " on " + entryMappedTable);
				indexDefinitions.add(indexDefinition);
			}

		} catch(Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			return indexDefinitions;
		}
	}


	/**
	 * Creates indices with given index definition SQL statements
	 */
	@Override
	public List<String> createIndexes() {

		List<String>  indexdefinitions = new ArrayList<>();
		indexdefinitions.add(entryMappedAccessionIndex);
		indexdefinitions.add(entryMappedAnnotationIndex);
		indexdefinitions.add(rawAccessionIndex);
		indexdefinitions.add(rawAnnotationIndex);
		LOGGER.info("Creating 4 indexes on entry mapped and raw tables");
		try (Connection conn = dataSourceServiceLocator.getStatementsDataSource().getConnection()) {
			java.sql.Statement createStatement = conn.createStatement();
			for(String indexDefinition : indexdefinitions) {
				LOGGER.info("Executing index update statements : " + indexDefinition);
				try {
					createStatement.executeUpdate(indexDefinition);
				} catch(SQLException e) {
					LOGGER.error(e.getMessage());
				}
			}
			return indexdefinitions;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}