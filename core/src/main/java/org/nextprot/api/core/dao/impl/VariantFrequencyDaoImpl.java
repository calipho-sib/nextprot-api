package org.nextprot.api.core.dao.impl;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.VariantFrequencyDao;
import org.nextprot.api.core.domain.VariantFrequency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class VariantFrequencyDaoImpl implements VariantFrequencyDao {

    @Autowired
    private SQLDictionary sqlDictionary;

    @Autowired
    private DataSourceServiceLocator dataSourceLocator;

    /**
     * Return the variant frequency given the RSID
     * @param RSID
     * @return
     */
    @Override
    public VariantFrequency findVariantFrequency(String RSID) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("rsid", RSID);
        SqlParameterSource parameterSource = new MapSqlParameterSource(parameters);

        VariantFrequency variantFrequency= new NamedParameterJdbcTemplate(dataSourceLocator.getDataSource())
                .queryForObject(sqlDictionary.getSQLQuery(
                        "gene-entry-by-chromosomal-location"),
                        parameterSource,
                        new VariantFrequencyRowMapper()
                        );
        return variantFrequency;
    }

    /**
     * Insert variant frequency
     * @param variantFrequency
     */
    @Override
    public void insertVariantFrequency(List<VariantFrequency> variantFrequency) {

    }


    class VariantFrequencyRowMapper extends SingleColumnRowMapper<VariantFrequency> {
        @Override
        public VariantFrequency mapRow(ResultSet resultSet, int row) throws SQLException {
            VariantFrequency variantFrequency = new VariantFrequency();
            variantFrequency.setSource(resultSet.getString("source"));
            variantFrequency.setAlleleCount(resultSet.getInt("allele_count"));
            variantFrequency.setAllelNumber(resultSet.getInt("allele_number"));
            variantFrequency.setAllelFrequency(resultSet.getInt("allele_frequency"));
            return variantFrequency;
        }
    }
}
