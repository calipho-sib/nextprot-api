package org.nextprot.api.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.Pair;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.MasterIsoformMappingDao;
import org.nextprot.api.core.domain.TemporaryIsoformSpecificity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class MasterIsoformMappingDaoImpl implements MasterIsoformMappingDao {
	
	@Autowired private SQLDictionary sqlDictionary;

	@Autowired private DataSourceServiceLocator dsLocator;
	
	
	@Override
	public List<TemporaryIsoformSpecificity> findIsoformMappingByMaster(String ac) {
		SqlParameterSource namedParams = new MapSqlParameterSource("entryName", ac);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("master-isoform-mapping-by-entry-name"), namedParams, new RowMapper<TemporaryIsoformSpecificity>() {

			@Override
			public TemporaryIsoformSpecificity mapRow(ResultSet resultSet, int row) throws SQLException {
				TemporaryIsoformSpecificity spec = new TemporaryIsoformSpecificity(resultSet.getString("isoform_ac"));
				spec.addPosition(Pair.pair(resultSet.getInt("first_pos"), resultSet.getInt("last_pos")));
				return spec;
			}			
		});
	}

	

}
