package org.nextprot.api.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.SimpleDao;
import org.nextprot.api.core.domain.CvDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class SimpleDaoImpl implements SimpleDao {

	@Autowired private SQLDictionary sqlDictionary;
	@Autowired private DataSourceServiceLocator dsLocator;

	public List<CvDatabase> findAllCvDatabases() {
		SqlParameterSource params = new MapSqlParameterSource();
		List<CvDatabase> terms = new NamedParameterJdbcTemplate(
				dsLocator.getDataSource()).query(
				sqlDictionary.getSQLQuery("cv-database-all"), params, new CvDatabaseRowMapper());
		return terms;
	}
	
	private static class CvDatabaseRowMapper extends SingleColumnRowMapper<CvDatabase> {

		public CvDatabase mapRow(ResultSet rs, int row) throws SQLException {
			return new CvDatabase(
				rs.getLong("db_id"), rs.getString("db_name"),
				rs.getLong("cat_id"),rs.getString("cat_name"));
		}
	}
	
}
