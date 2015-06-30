package org.nextprot.api.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.ReleaseInfoDao;
import org.nextprot.api.core.domain.release.ReleaseInfoDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ReleaseInfoDaoImpl implements ReleaseInfoDao {
	
	@Autowired private DataSourceServiceLocator dsLocator;
	@Autowired private SQLDictionary sqlDictionary;
	
	@Override
	public List<ReleaseInfoDataSource> findReleaseInfoDataSources() {
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("release-info-datasources"), new ReleaseInfoRowMapper());
	}
	
	private static class ReleaseInfoRowMapper implements ParameterizedRowMapper<ReleaseInfoDataSource> {

		@Override
		public ReleaseInfoDataSource mapRow(ResultSet resultSet, int row) throws SQLException {
			ReleaseInfoDataSource releaseInfoDs = new ReleaseInfoDataSource();
			releaseInfoDs.setDescription(resultSet.getString("description"));
			releaseInfoDs.setSource(resultSet.getString("cv_name"));
			releaseInfoDs.setUrl(resultSet.getString("url"));
			return releaseInfoDs;
		}
		
	}

}
