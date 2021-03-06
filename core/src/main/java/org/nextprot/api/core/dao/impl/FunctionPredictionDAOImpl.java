package org.nextprot.api.core.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.FunctionPredictionDAO;
import org.nextprot.api.core.domain.FunctionPrediction;
import org.nextprot.api.core.domain.PredictionEvidence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.StringTokenizer;

@Repository
public class FunctionPredictionDAOImpl implements FunctionPredictionDAO {

    private final Log Logger = LogFactory.getLog(FunctionPredictionDAOImpl.class);

    @Autowired
    private SQLDictionary sqlDictionary;

    @Autowired(required = false)
    private DataSourceServiceLocator dsLocator;

    @Override
    public List<FunctionPrediction> getPredictions(String entryAccession) {

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("entry_ac", entryAccession);
        return new NamedParameterJdbcTemplate(dsLocator.getUserDataSource())
                .query(sqlDictionary.getSQLQuery("read-prediction-list"), namedParameters, new FunctionPredictionRowMapper());
    }

    private static class FunctionPredictionRowMapper extends SingleColumnRowMapper<FunctionPrediction> {

        public FunctionPrediction mapRow(ResultSet resultSet, int row) throws SQLException {
            String cvTermAccession = resultSet.getString("cv_term_ac");
            String entryAccession = resultSet.getString("entry_ac");

            FunctionPrediction prediction = new FunctionPrediction(cvTermAccession);
            prediction.setEntryAC(entryAccession);

            //Evidence
            String ecoCodeAccession = resultSet.getString("evidence_code_ac");
            String statementID = resultSet.getString("statement_id");
            String userOrcIDs = resultSet.getString( "user_orcid");
            boolean userHidden = resultSet.getBoolean( "user_hidden");
            String publicationAccession = resultSet.getString( "publication_ac");
            String publicationDB = resultSet.getString( "publication_db");

            PredictionEvidence predictionEvidence = new PredictionEvidence(ecoCodeAccession);
            predictionEvidence.setStatementID(statementID);
            predictionEvidence.setPublicationAc(publicationAccession);
            predictionEvidence.setPublicationDatabaseName(publicationDB);
            predictionEvidence.setUserHidden(userHidden);
            StringTokenizer tokenizer = new StringTokenizer(userOrcIDs,",");
            while(tokenizer.hasMoreTokens()) {
                predictionEvidence.adduserOrcIDs(tokenizer.nextToken());
            }

            prediction.addEvidence(predictionEvidence);
            return prediction;
        }
    }
}

