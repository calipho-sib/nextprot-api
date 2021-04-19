package org.nextprot.api.user.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.user.dao.FunctionPredictionDAO;
import org.nextprot.api.user.domain.FunctionPrediction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class FunctionPredictionDAOImpl implements FunctionPredictionDAO {

    private final Log Logger = LogFactory.getLog(FunctionPredictionDAOImpl.class);

    @Autowired
    private SQLDictionary sqlDictionary;

    @Autowired(required = false)
    private DataSourceServiceLocator dsLocator;

    @Override
    public List<FunctionPrediction> getPredictions() {

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        return new NamedParameterJdbcTemplate(dsLocator.getUserDataSource())
                .query(sqlDictionary.getSQLQuery("read-predictions"), namedParameters, new FunctionPredictionRowMapper());
    }

    private static class FunctionPredictionRowMapper extends SingleColumnRowMapper<FunctionPrediction> {

        public FunctionPrediction mapRow(ResultSet resultSet, int row) throws SQLException {
            FunctionPrediction prediction = new FunctionPrediction(resultSet.getString("cv_term_ac"));
            String userOrcIDs = resultSet.getString( "user_orcid");
            String ecoCode = resultSet.getString("evidence_code_ac");
            prediction.addEvidence(ecoCode, userOrcIDs);
            return prediction;
        }
    }
}

