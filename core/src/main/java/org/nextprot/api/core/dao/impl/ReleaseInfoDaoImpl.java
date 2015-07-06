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
import org.nextprot.api.core.domain.release.ReleaseContentsDataSource;
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
	public List<ReleaseContentsDataSource> findReleaseInfoDataSources() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cvNames", ReleaseDataSources.getDistinctCvNamesExcept(ReleaseDataSources.PeptideAtlas));
		List<ReleaseContentsDataSource> ds = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("release-contents"), params, new ReleaseInfoRowMapper(null));
		ds.addAll(new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("release-contents-peptide-atlas"), params, new ReleaseInfoRowMapper(ReleaseDataSources.PeptideAtlas)));
		return ds;
	}

	@Override
	public String findDatabaseRelease() {
		return new JdbcTemplate(dsLocator.getDataSource()).queryForObject(sqlDictionary.getSQLQuery("nextprot-release"), String.class);
	}

	private static class ReleaseInfoRowMapper implements ParameterizedRowMapper<ReleaseContentsDataSource> {
		
		private ReleaseDataSources datasource;
		
		public ReleaseInfoRowMapper(ReleaseDataSources datasource){
			this.datasource = datasource;
		}

		@Override
		public ReleaseContentsDataSource mapRow(ResultSet resultSet, int row) throws SQLException {
			ReleaseContentsDataSource releaseInfoDs = new ReleaseContentsDataSource();
			ReleaseDataSources ds = null;
			if(datasource == null){  ds = ReleaseDataSources.cvValueOf(resultSet.getString("cv_name"));
			}else { ds = this.datasource; }
			releaseInfoDs.setSource(ds.getDisplayName());
			releaseInfoDs.setLastImportDate(resultSet.getString("last_import"));
			releaseInfoDs.setReleaseVersion(resultSet.getString("release_version"));
			releaseInfoDs.setDescription(ds.getDescription());
			releaseInfoDs.setUrl(ds.getUrl());
			return releaseInfoDs;
		}

	}

}
