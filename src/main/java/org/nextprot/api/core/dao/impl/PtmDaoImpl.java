package org.nextprot.api.core.dao.impl;

import static org.nextprot.api.commons.utils.SQLDictionary.getSQLQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nextprot.api.core.dao.PtmDao;
import org.nextprot.api.core.domain.Feature;
import org.nextprot.auth.core.service.DataSourceServiceLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PtmDaoImpl implements PtmDao {

	@Autowired private DataSourceServiceLocator dsLocator;
	
	@Override
	public List<Feature> findPtmsByEntry(String uniqueName) {
		SqlParameterSource params = new MapSqlParameterSource("uniqueName", uniqueName);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(getSQLQuery("ptm-by-master"), params, new ParameterizedRowMapper<Feature>() {

			@Override
			public Feature mapRow(ResultSet resultSet, int row) throws SQLException {
				Feature feature = new Feature();
				
				feature.setAccession(resultSet.getString("accession"));
				feature.setIsoformAccession(resultSet.getString("unique_name"));
				feature.setCvName(resultSet.getString("cv_name"));
				feature.setDescription(resultSet.getString("description"));
				feature.setFirstPosition(resultSet.getInt("first_pos"));
				feature.setLastPosition(resultSet.getInt("last_pos"));
				feature.setQuality(resultSet.getInt("quality"));
				feature.setType(resultSet.getString("type"));
				
				return feature;
			}
		});
	}

}
