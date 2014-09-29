package org.nextprot.api.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.ExperimentalContextDao;
import org.nextprot.api.core.domain.ExperimentalContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ExperimentalContextDaoImpl implements ExperimentalContextDao {
	
	@Autowired private SQLDictionary sqlDictionary;

	@Autowired private DataSourceServiceLocator dsLocator;

	@Override
	public List<ExperimentalContext> findExperimentalContextsByIds(List<String> ids) {
		SqlParameterSource namedParameters = new MapSqlParameterSource("ids", ids);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("experimental-contexts-by-ids"), namedParameters, new EcRowMapper());
	}

	@Override
	public List<ExperimentalContext> findAllExperimentalContexts() {
		SqlParameterSource namedParameters = new MapSqlParameterSource();
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("experimental-contexts-all"), namedParameters, new EcRowMapper());
	}
	
	private static class EcRowMapper implements ParameterizedRowMapper<ExperimentalContext> {

		@Override
		public ExperimentalContext mapRow(ResultSet rs, int row) throws SQLException {
			ExperimentalContext ec = new ExperimentalContext();
			ec.setContextId(rs.getLong("context_id"));
			ec.setCellLineAC(rs.getString("cellLineAC"));
			ec.setTissueAC(rs.getString("tissueAC"));
			ec.setOrganelleAC(rs.getString("organelleAC"));
			ec.setDetectionMethodAC(rs.getString("detectionMethodAC"));
			ec.setDiseaseAC(rs.getString("diseaseAC"));
			ec.setDevelopmentalStageAC(rs.getString("developmentalStageAC"));
			ec.setMetadataAC(rs.getString("metadataAC"));
			return ec;
		}
		
	}

}
