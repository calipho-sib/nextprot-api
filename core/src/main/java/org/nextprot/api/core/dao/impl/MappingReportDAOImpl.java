package org.nextprot.api.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.MappingReportDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class MappingReportDAOImpl implements MappingReportDAO {

	@Autowired private DataSourceServiceLocator dsLocator;
	@Autowired private SQLDictionary sqlDictionary;
	
	@Override
	public List<String> findHpaMapping() {
		Map<String,Object> params = new HashMap<String,Object>();
		SqlParameterSource namedParams = new MapSqlParameterSource(params);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("hpa-mapping-report"), namedParams, new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int row) throws SQLException {
				return rs.getString("nx_ac") + "|" + rs.getString("hpa_ac");
			}
		});
	}







}
