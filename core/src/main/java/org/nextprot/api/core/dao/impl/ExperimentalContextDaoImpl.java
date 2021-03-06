package org.nextprot.api.core.dao.impl;

import org.apache.log4j.Logger;
import org.nextprot.api.commons.bio.experimentalcontext.ExperimentalContextStatement;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.ExperimentalContextDao;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.ExperimentalContext;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.utils.ExperimentalContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ExperimentalContextDaoImpl implements ExperimentalContextDao {
	
	@Autowired
	private SQLDictionary sqlDictionary;

	@Autowired
	private DataSourceServiceLocator dsLocator;

	@Autowired
	private TerminologyService terminologyService;

	@Autowired(required=false)
	private CacheManager cacheManager;

	private final long METADATA_ID = 29348710;

	protected final Logger LOGGER = Logger.getLogger(ExperimentalContextDaoImpl.class);

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

	// TODO: handle erase
	@Override
	public String loadExperimentalContexts(List<ExperimentalContextStatement> experimentalContextStatements, boolean load) throws SQLException {
		PreparedStatement pstmt = null;
		StringBuffer insertStatements = new StringBuffer();

		// Construct a set of MD5s of experimental context exist in DB
		List<ExperimentalContext> experimentalContexts = findAllExperimentalContexts();
		Set<String> existingContexts = experimentalContexts.stream()
				.map(experimentalContext -> experimentalContext.getMd5())
				.collect(Collectors.toSet());
		LOGGER.info("Existing experimental contexts " + existingContexts.size());

		try (Connection conn = dsLocator.getDataSource().getConnection()) {
			pstmt = conn.prepareStatement(
					"INSERT INTO nextprot.experimental_contexts ( tissue_id, developmental_stage_id, detection_method_id, md5, metadata_id ) "
							+ "VALUES ( ?,?,?,?,?)"
			);

			List<String> sqlStatements = new ArrayList<>();
			for (ExperimentalContextStatement expStatements : experimentalContextStatements) {
				String expMD5 = ExperimentalContextUtil.computeMd5ForBgee(
						expStatements.getTissueAC(),
						expStatements.getDevelopmentStageAC(),
						expStatements.getDetectionMethodAC());
				if (!existingContexts.contains(expMD5)) {
					String tissueAC = expStatements.getTissueAC();
					if (tissueAC == null) {
						LOGGER.error("Tissue accession is null, ignoring the experimental context statement");
						continue;
					}
					CvTerm tissue = terminologyService.findCvTermInOntology(tissueAC, TerminologyCv.NextprotAnatomyCv);
					if (tissue == null) {
						LOGGER.error("Term for accession " + tissueAC + " not found, ignoring the experimental context statement");
						continue;
					}
					long tissueID = tissue.getId();
					pstmt.setLong(1, tissueID);

					String devStageAC = expStatements.getDevelopmentStageAC();
					if (devStageAC == null) {
						LOGGER.error("Dev stage accession is null, ignoring the experimental context statement");
						continue;
					}
					CvTerm devStage = terminologyService.findCvTermInOntology(devStageAC, TerminologyCv.BgeeDevelopmentalStageCv);
					if (devStage == null) {
						LOGGER.error("Term for accession " + devStageAC + " not found, ignoring the experimental context statement");
						continue;
					}
					long devStageID = devStage.getId();
					pstmt.setLong(2, devStageID);

					String detectionMethodAC = expStatements.getDetectionMethodAC();
					if (detectionMethodAC == null) {
						LOGGER.error("Detection method accession is null, ignoring the experimental context statement");
						continue;
					}
					CvTerm detectionMethod = terminologyService.findCvTermInOntology(detectionMethodAC, TerminologyCv.EvidenceCodeOntologyCv);
					if (detectionMethod == null) {
						LOGGER.error("Term for accession " + detectionMethodAC + " not found, ignoring the experimental context statement");
						continue;
					}
					long detectionMethodID = detectionMethod.getId();
					pstmt.setLong(3, detectionMethodID);

					pstmt.setString(4, expMD5);
					pstmt.setLong(5, METADATA_ID);
					pstmt.addBatch();

					// Adds the record to the sql statement
					String insertStatement = "INSERT INTO nextprot.experimental_contexts ( tissue_id, developmental_stage_id, detection_method_id, md5, metadata_id ) VALUES";
					insertStatement += "( " + tissueID + "," + devStageID + "," + detectionMethodID + ",'" + expMD5 + "'," + METADATA_ID + ");\n";
					insertStatements.append(insertStatement);
					LOGGER.info("Adds values for insert: " + "( " + tissueID + "," + devStageID + "," + detectionMethodID + ",'" + expMD5 + "'," + METADATA_ID + ")");
				} else {
					LOGGER.info("Experimental context already exists for MD5 " + expMD5);
				}
			}
			LOGGER.info("Statements to be loaded : " + sqlStatements.size());
			if(load) {
				LOGGER.info("Loading the experimental contexts to " + dsLocator.getDataSource().getConnection().toString());
				pstmt.executeBatch();
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(pstmt  != null){
				pstmt.close();
			}
			LOGGER.info("SQL statement for the load " + insertStatements.toString());
			return insertStatements.toString();
		}
	}

	private class ExperimentalContextMapper extends SingleColumnRowMapper<ExperimentalContext> {

		@Override
		public ExperimentalContext mapRow(ResultSet rs, int row) throws SQLException {

			ExperimentalContext ec = new ExperimentalContext();

			ec.setContextId(rs.getLong("context_id"));
			ec.setMetadataId(rs.getLong("metadataId"));
			ec.setMD5(rs.getString("md5"));
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
