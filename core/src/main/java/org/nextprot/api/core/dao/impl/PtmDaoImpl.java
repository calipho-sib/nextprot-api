package org.nextprot.api.core.dao.impl;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.PtmDao;
import org.nextprot.api.core.domain.Feature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class PtmDaoImpl implements PtmDao {

	@Autowired private SQLDictionary sqlDictionary;

	@Autowired private DataSourceServiceLocator dsLocator;
	
	@Override
	public List<Feature> findPtmsByEntry(String uniqueName) {
		SqlParameterSource params = new MapSqlParameterSource("uniqueName", uniqueName);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("ptm-by-master"), params, new SingleColumnRowMapper<Feature> () {

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
