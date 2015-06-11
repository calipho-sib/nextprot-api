package org.nextprot.api.core.dao.impl;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.ExperimentalContextDao;
import org.nextprot.api.core.dao.TerminologyDao;
import org.nextprot.api.core.domain.ExperimentalContext;
import org.nextprot.api.core.domain.Terminology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class ExperimentalContextDaoImpl implements ExperimentalContextDao {
	
	@Autowired private SQLDictionary sqlDictionary;

	@Autowired private DataSourceServiceLocator dsLocator;

	@Autowired private TerminologyDao terminologyDao;

	@Override
	public List<ExperimentalContext> findExperimentalContextsByIds(List<Long> ids) {
		SqlParameterSource namedParameters = new MapSqlParameterSource("ids", ids);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("experimental-contexts-by-ids"), namedParameters, new EcRowMapperSimple());
	}

	@Override
	public List<ExperimentalContext> findAllExperimentalContexts() {
		SqlParameterSource namedParameters = new MapSqlParameterSource();
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("experimental-contexts-all"), namedParameters, new EcRowMapperSimple());
	}
	
	private class EcRowMapper implements ParameterizedRowMapper<ExperimentalContext> {

		@Override
		public ExperimentalContext mapRow(ResultSet rs, int row) throws SQLException {
			ExperimentalContext ec = new ExperimentalContext();

			ec.setContextId(rs.getLong("context_id"));
			ec.setMetadataAC(rs.getString("metadataAC"));

			String cellLineAc = rs.getString("cellLineAC");
			String tissueAC = rs.getString("tissueAC");
			String organelleAC = rs.getString("organelleAC");
			String detectionMethodAC = rs.getString("detectionMethodAC");
			String diseaseAC = rs.getString("diseaseAC");
			String developmentalStageAC = rs.getString("developmentalStageAC");

			Map<String, Terminology> map = terminologyDao.findTerminologyByAccessions(filter(cellLineAc, tissueAC, organelleAC,
					detectionMethodAC, diseaseAC, developmentalStageAC));

			if (map.containsKey(cellLineAc)) ec.setCellLine(map.get(cellLineAc));
			if (map.containsKey(tissueAC)) ec.setTissue(map.get(tissueAC));
			if (map.containsKey(organelleAC)) ec.setOrganelle(map.get(organelleAC));
			if (map.containsKey(detectionMethodAC)) ec.setDetectionMethod(map.get(detectionMethodAC));
			if (map.containsKey(diseaseAC)) ec.setDisease(map.get(diseaseAC));
			if (map.containsKey(developmentalStageAC)) ec.setDevelopmentalStage(map.get(developmentalStageAC));

			return ec;
		}

		private List<String> filter(String... elements) {

			List<String> list = new ArrayList<>();

			for (String element : elements) {

				if (element != null)
					list.add(element);
			}

			return list;
		}
	}

	private class EcRowMapperSimple implements ParameterizedRowMapper<ExperimentalContext> {

		@Override
		public ExperimentalContext mapRow(ResultSet rs, int row) throws SQLException {
			ExperimentalContext ec = new ExperimentalContext();

			ec.setContextId(rs.getLong("context_id"));
			ec.setMetadataAC(rs.getString("metadataAC"));

			String cellLineAc = rs.getString("cellLineAC");
			String tissueAC = rs.getString("tissueAC");
			String organelleAC = rs.getString("organelleAC");
			String detectionMethodAC = rs.getString("detectionMethodAC");
			String diseaseAC = rs.getString("diseaseAC");
			String developmentalStageAC = rs.getString("developmentalStageAC");

			Terminology term = terminologyDao.findTerminologyByAccession(cellLineAc);
			if (term != null) ec.setCellLine(term);

			term = terminologyDao.findTerminologyByAccession(tissueAC);
			if (term != null) ec.setTissue(term);

			term = terminologyDao.findTerminologyByAccession(organelleAC);
			if (term != null) ec.setOrganelle(term);

			term = terminologyDao.findTerminologyByAccession(detectionMethodAC);
			if (term != null) ec.setDetectionMethod(term);

			term = terminologyDao.findTerminologyByAccession(diseaseAC);
			if (term != null) ec.setDisease(term);

			term = terminologyDao.findTerminologyByAccession(developmentalStageAC);
			if (term != null) ec.setDevelopmentalStage(term);

			return ec;
		}
	}

}
