package org.nextprot.api.core.dao.impl;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.ReleaseStatsDao;
import org.nextprot.api.core.domain.release.ReleaseContentsDataSource;
import org.nextprot.api.core.domain.release.ReleaseDataSources;
import org.nextprot.api.core.domain.release.ReleaseStatsTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ReleaseStatsDaoImpl implements ReleaseStatsDao {

	@Autowired
	private DataSourceServiceLocator dsLocator;
	@Autowired
	private SQLDictionary sqlDictionary;

	@Override
	public List<ReleaseContentsDataSource> findReleaseInfoDataSources() {

		Map<String, Object> params = new HashMap<>();

		params.put("cvNames", ReleaseDataSources.getDistinctCvNamesExcept(ReleaseDataSources.PeptideAtlas));
		List<ReleaseContentsDataSource> ds = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("release-contents"), params, new ReleaseInfoRowMapper(null));
		ds.addAll(new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("release-contents-peptide-atlas"), params, new ReleaseInfoRowMapper(ReleaseDataSources.PeptideAtlas)));

		ds.sort((ds1, ds2) -> ds1.getSource().compareToIgnoreCase(ds2.getSource()));

		return ds;
	}

	public List<ReleaseStatsTag> findTagStatistics() {
		return new JdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("release-stats"), new ReleaseStatsTagRowMapper());
	}

	private static class ReleaseInfoRowMapper extends SingleColumnRowMapper<ReleaseContentsDataSource> {

		private ReleaseDataSources datasource;
		
		public ReleaseInfoRowMapper(ReleaseDataSources datasource){
			this.datasource = datasource;
		}

		@Override
		public ReleaseContentsDataSource mapRow(ResultSet resultSet, int row) throws SQLException {

			ReleaseContentsDataSource releaseInfoDs = new ReleaseContentsDataSource();
			ReleaseDataSources ds;
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

	private static class ReleaseStatsTagRowMapper extends SingleColumnRowMapper<ReleaseStatsTag> {

		@Override
		public ReleaseStatsTag mapRow(ResultSet resultSet, int row) throws SQLException {

			ReleaseStatsTag tagStat = new ReleaseStatsTag();

			tagStat.setDescription(resultSet.getString("description"));
			tagStat.setTag(resultSet.getString("tag"));
			tagStat.setCategroy(resultSet.getString("category"));
			tagStat.setSortOrder(resultSet.getInt("sort_order"));
			tagStat.setCount(resultSet.getInt("count"));

			return tagStat;
		}
	}
}
