package org.nextprot.api.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.ReleaseInfoDao;
import org.nextprot.api.core.domain.release.ReleaseDataSources;
import org.nextprot.api.core.domain.release.ReleaseInfoDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ReleaseInfoDaoImpl implements ReleaseInfoDao {

	@Autowired
	private DataSourceServiceLocator dsLocator;
	@Autowired
	private SQLDictionary sqlDictionary;

	@Override
	public List<ReleaseInfoDataSource> findReleaseInfoDataSources() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cvNames", ReleaseDataSources.getDistinctCvNames());
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("release-contents"), params, new ReleaseInfoRowMapper());
	}

	@Override
	public String findNextProtRelease() {
		return new JdbcTemplate(dsLocator.getDataSource()).queryForObject(sqlDictionary.getSQLQuery("nextprot-release"), String.class);
	}

	private static class ReleaseInfoRowMapper implements ParameterizedRowMapper<ReleaseInfoDataSource> {

		@Override
		public ReleaseInfoDataSource mapRow(ResultSet resultSet, int row) throws SQLException {
			ReleaseInfoDataSource releaseInfoDs = new ReleaseInfoDataSource();
			ReleaseDataSources ds = ReleaseDataSources.cvValueOf(resultSet.getString("cv_name"));
			releaseInfoDs.setLastImportDate(resultSet.getString("last_import"));
			releaseInfoDs.setRelease(resultSet.getString("release_version"));
			
			releaseInfoDs.setDescription(ds.getDescription());
			releaseInfoDs.setUrl(ds.getUrl());
			
			
			return releaseInfoDs;
		}

	}

}
