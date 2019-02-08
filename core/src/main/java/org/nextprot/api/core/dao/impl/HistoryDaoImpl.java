package org.nextprot.api.core.dao.impl;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.HistoryDao;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.domain.Overview.History;
import org.nextprot.api.core.domain.ProteinExistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Repository
public class HistoryDaoImpl implements HistoryDao {

	@Autowired private SQLDictionary sqlDictionary;

	@Autowired private DataSourceServiceLocator dsLocator;
	
	
	@Override
	public List<History> findHistoryByEntry(String uniqueName) {
		SqlParameterSource namedParameters = new MapSqlParameterSource("uniqueName", uniqueName);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("history-by-entry"), namedParameters, new HistoryEntryRowMapper());
	}
	
	private static class HistoryEntryRowMapper extends SingleColumnRowMapper<History> {

		@Override
		public History mapRow(ResultSet resultSet, int row) throws SQLException {
			History historyEntry = new Overview.History(); 
			historyEntry.setProteinExistenceUniprot(ProteinExistence.valueOfKey(resultSet.getString("protein_existence")));
			historyEntry.setNextprotIntegrationDate(new Date(resultSet.getTimestamp("nextprot_integrated").getTime()));
			historyEntry.setNextprotUpdateDate(new Date(resultSet.getTimestamp("nextprot_updated").getTime()));
			historyEntry.setUniprotIntegrationDate(new Date(resultSet.getTimestamp("uniprot_integrated").getTime()));
			historyEntry.setUniprotUpdateDate(new Date(resultSet.getTimestamp("uniprot_updated").getTime()));
			historyEntry.setUniprotVersion(resultSet.getString("uniprot_version"));
			historyEntry.setLastSequenceUpdate(resultSet.getDate("last_sequence_update"));
			historyEntry.setSequenceVersion(resultSet.getString("sequence_version"));
			return historyEntry;
		}
		
	}

}
