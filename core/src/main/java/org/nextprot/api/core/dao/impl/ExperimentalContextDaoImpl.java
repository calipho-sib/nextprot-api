package org.nextprot.api.core.dao.impl;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.ExperimentalContextDao;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.ExperimentalContext;
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
public class ExperimentalContextDaoImpl implements ExperimentalContextDao {
	
	@Autowired private SQLDictionary sqlDictionary;

	@Autowired private DataSourceServiceLocator dsLocator;

	@Override
	public List<ExperimentalContext> findExperimentalContextsByIds(List<Long> ids) {
		SqlParameterSource namedParameters = new MapSqlParameterSource("ids", ids);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("experimental-contexts-by-ids"), namedParameters, new ExperimentalContextMapper());
	}

	@Override
	public List<ExperimentalContext> findAllExperimentalContexts() {
		SqlParameterSource namedParameters = new MapSqlParameterSource();
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("experimental-contexts-all"), namedParameters, new ExperimentalContextMapper());
	}

	private class ExperimentalContextMapper extends SingleColumnRowMapper<ExperimentalContext> {

		@Override
		public ExperimentalContext mapRow(ResultSet rs, int row) throws SQLException {

			ExperimentalContext ec = new ExperimentalContext();

			ec.setContextId(rs.getLong("context_id"));
			ec.setMetadataId(rs.getLong("metadataId"));
			ec.setCellLine(asTerminology(rs.getString("cellLineAC")));
			ec.setTissue(asTerminology(rs.getString("tissueAC")));
			ec.setOrganelle(asTerminology(rs.getString("organelleAC")));
			ec.setDetectionMethod(asTerminology(rs.getString("detectionMethodAC")));
			ec.setDisease(asTerminology(rs.getString("diseaseAC")));
			ec.setDevelopmentalStage(asTerminology(rs.getString("developmentalStageAC")));

			return ec;
		}

		private CvTerm asTerminology(String ac) {

			if (ac != null) {
				CvTerm term = new CvTerm();
				term.setAccession(ac);
				return term;
			}
			return null;
		}
	}
}
