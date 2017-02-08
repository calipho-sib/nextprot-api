package org.nextprot.api.blast.dao.impl;

import org.nextprot.api.blast.dao.BlastDAO;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BlastDAOImpl implements BlastDAO {

    @Autowired
    private SQLDictionary sqlDictionary;

    @Autowired
    private DataSourceServiceLocator dsLocator;

    @Override
    public Map<String, String> getAllIsoformSequences() {

        Map<String, String> map = new HashMap<>();

        List<Map<String,Object>> results = new JdbcTemplate(dsLocator.getDataSource())
                .queryForList(sqlDictionary.getSQLQuery("all-isoaccessions-with-sequence"));

        for (Map<String,Object> row : results) {

            String isoAccession = (String) row.get("iso_accession");
            String bioSequence = (String) row.get("bio_sequence");

            map.put(isoAccession, bioSequence);
        }

        return map;
    }
}