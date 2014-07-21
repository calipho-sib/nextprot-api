package org.nextprot.api.dao.impl;

import static org.nextprot.utils.SQLDictionary.getSQLQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nextprot.api.dao.BioPhyChemPropsDao;
import org.nextprot.auth.core.service.DataSourceServiceLocator;
import org.nextprot.utils.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class BioPhyChemPropsDaoImpl implements BioPhyChemPropsDao {

	@Autowired private DataSourceServiceLocator dsLocator;
	
	@Override
	public List<Pair<String, String>> findPropertiesByUniqueName(String uniqueName) {
		SqlParameterSource namedParams = new MapSqlParameterSource("uniqueName", uniqueName);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(getSQLQuery("biophychem-by-entry"), namedParams, new ParameterizedRowMapper<Pair<String, String>>() {

			@Override
			public Pair<String, String> mapRow(ResultSet resultSet, int row) throws SQLException {
				return Pair.pair(resultSet.getString("display_name") , resultSet.getString("property_value"));
			}
		});
	}
	
	

}
