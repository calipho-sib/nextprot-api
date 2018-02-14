package org.nextprot.api.core.dao.impl;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.ReleaseInfoDao;
import org.nextprot.api.core.domain.ProteinExistence;
import org.nextprot.api.core.domain.release.ReleaseContentsDataSource;
import org.nextprot.api.core.domain.release.ReleaseDataSources;
import org.nextprot.api.core.domain.release.ReleaseStatsTag;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ReleaseInfoDaoImpl implements ReleaseInfoDao {

	@Autowired
	private DataSourceServiceLocator dsLocator;
	@Autowired
	private SQLDictionary sqlDictionary;
	@Autowired
	private MasterIdentifierService masterIdentifierService;

	@Override
	public List<ReleaseContentsDataSource> findReleaseInfoDataSources() {

		Map<String, Object> params = new HashMap<>();

		params.put("cvNames", ReleaseDataSources.getDistinctCvNamesExcept(ReleaseDataSources.PeptideAtlas));
		List<ReleaseContentsDataSource> ds = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("release-contents"), params, new ReleaseInfoRowMapper(null));
		ds.addAll(new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("release-contents-peptide-atlas"), params, new ReleaseInfoRowMapper(ReleaseDataSources.PeptideAtlas)));

		ds.sort((ds1, ds2) -> ds1.getSource().compareToIgnoreCase(ds2.getSource()));

		return ds;
	}

	@Override
	public String findDatabaseRelease() {
		return new JdbcTemplate(dsLocator.getDataSource()).queryForObject(sqlDictionary.getSQLQuery("nextprot-release"), String.class);
	}

	@Override
	public List<ReleaseStatsTag> findTagStatistics() {

		Map<String, Integer> proteinExistencesCount = new HashMap<>();

		proteinExistencesCount.put("PROTEIN_LEVEL_MASTER", masterIdentifierService.findEntryAccessionsByProteinExistence(ProteinExistence.PROTEIN_LEVEL).size());
		proteinExistencesCount.put("TRANSCRIPT_LEVEL_MASTER", masterIdentifierService.findEntryAccessionsByProteinExistence(ProteinExistence.TRANSCRIPT_LEVEL).size());
		proteinExistencesCount.put("HOMOLOGY_MASTER", masterIdentifierService.findEntryAccessionsByProteinExistence(ProteinExistence.HOMOLOGY).size());
		proteinExistencesCount.put("PREDICTED_MASTER", masterIdentifierService.findEntryAccessionsByProteinExistence(ProteinExistence.PREDICTED).size());
		proteinExistencesCount.put("UNCERTAIN_MASTER", masterIdentifierService.findEntryAccessionsByProteinExistence(ProteinExistence.UNCERTAIN).size());

		return new JdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("release-stats"), new ReleaseStatsTagRowMapper(proteinExistencesCount));
	}

	private static class ReleaseInfoRowMapper implements ParameterizedRowMapper<ReleaseContentsDataSource> {

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

	private static class ReleaseStatsTagRowMapper implements ParameterizedRowMapper<ReleaseStatsTag> {

		private final Map<String, Integer> proteinExistencesCount;

		public ReleaseStatsTagRowMapper(Map<String, Integer> proteinExistencesCount){
			this.proteinExistencesCount = proteinExistencesCount;
		}

		@Override
		public ReleaseStatsTag mapRow(ResultSet resultSet, int row) throws SQLException {

			ReleaseStatsTag tagStat = new ReleaseStatsTag();

			tagStat.setDescription(resultSet.getString("description"));
			tagStat.setTag(resultSet.getString("tag"));
			tagStat.setCategroy(resultSet.getString("category"));
			tagStat.setSortOrder(resultSet.getInt("sort_order"));

			if ("Protein existence".equals(tagStat.getCategroy())) {
				tagStat.setCount(proteinExistencesCount.get(tagStat.getTag()));
			}
			else {
				tagStat.setCount(resultSet.getInt("count"));
			}

			return tagStat;
		}
	}
}
