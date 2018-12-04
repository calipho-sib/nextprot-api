package org.nextprot.api.core.dao.impl;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.EntryPropertiesDao;
import org.nextprot.api.core.domain.EntryProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class EntryPropertiesDaoImpl implements EntryPropertiesDao {

	@Autowired private SQLDictionary sqlDictionary;
	@Autowired private DataSourceServiceLocator dsLocator;

	@Override
	public EntryProperties findEntryProperties(String uniqueName) {
		
		SqlParameterSource namedParameters = new MapSqlParameterSource("uniqueName", uniqueName);
		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dsLocator.getDataSource());

		return template.queryForObject(sqlDictionary.getSQLQuery("entry-properties"), namedParameters, new EntryPropertyRowMapper());
	}
	
	static class EntryPropertyRowMapper extends SingleColumnRowMapper<EntryProperties> {

		@Override
		public EntryProperties mapRow(ResultSet resultSet, int row) throws SQLException {
			int res;
			EntryProperties properties = new EntryProperties();
			res = resultSet.getInt("intcnt");
			properties.setInteractionCount(res == -1? 0:res);
			properties.setMaxSeqLen(resultSet.getInt("maxlen"));
			properties.setFilterstructure(resultSet.getInt("structure") != -1);
			properties.setFilterdisease(resultSet.getInt("disease") != -1);
			properties.setFilterexpressionprofile(resultSet.getInt("expression") != -1);
			return properties;
		}
	}
}
