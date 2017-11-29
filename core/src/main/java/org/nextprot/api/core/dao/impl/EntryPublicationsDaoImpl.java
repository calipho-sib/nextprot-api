package org.nextprot.api.core.dao.impl;

import org.nextprot.api.commons.dao.MasterIdentifierDao;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.JdbcUtils;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.EntryPublicationsDao;
import org.nextprot.api.core.domain.publication.EntryPublication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
public class EntryPublicationsDaoImpl implements EntryPublicationsDao {

	@Autowired private SQLDictionary sqlDictionary;
    @Autowired private MasterIdentifierDao masterIdentifierDao;

	@Autowired
	private DataSourceServiceLocator dsLocator;


    @Override
    public List<Long> findSortedPublicationIds(String entryAccession) {

        Map<String, Object> params = new HashMap<>();
        params.put("identifierId", masterIdentifierDao.findIdByUniqueName(entryAccession));
        params.put("publicationTypes", Arrays.asList(10, 20, 30, 40, 50, 60, 70, 80));

        return new NamedParameterJdbcTemplate(dsLocator.getDataSource())
                .query(sqlDictionary.getSQLQuery("publication-sorted-for-master"),
                        params,
                        new JdbcUtils.LongRowMapper("resource_id"));
    }

    @Override
    public List<EntryPublication> findSortedEntryPublications(String entryAccession) {

        return null;
    }
}
