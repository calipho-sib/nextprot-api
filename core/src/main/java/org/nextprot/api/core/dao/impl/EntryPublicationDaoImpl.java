package org.nextprot.api.core.dao.impl;

import org.nextprot.api.commons.dao.MasterIdentifierDao;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.EntryPublicationDao;
import org.nextprot.api.core.domain.publication.EntryPublication;
import org.nextprot.api.core.domain.publication.PublicationDirectLink;
import org.nextprot.api.core.domain.publication.PublicationProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
public class EntryPublicationDaoImpl implements EntryPublicationDao {

    @Autowired
    private MasterIdentifierDao masterIdentifierDao;
    @Autowired
    private DataSourceServiceLocator dsLocator;
    @Autowired
    private SQLDictionary sqlDictionary;

    @Override
    public EntryPublication buildEntryPublication(String entryAccession, long publicationId) {

        EntryPublication entryPublication = new EntryPublication(entryAccession, publicationId);
        entryPublication.setDirectLinks(findPublicationDirectLinkList(entryAccession, publicationId));

        return entryPublication;
    }

    @Override
    public List<PublicationDirectLink> findPublicationDirectLinkList(String entryAccession, long publicationId) {

        Map<String, Object> params = new HashMap<>();
        params.put("masterId", masterIdentifierDao.findIdByUniqueName(entryAccession));
        params.put("pubId", publicationId);

        EntryPublicationPropertyRowMapper mapper = new EntryPublicationPropertyRowMapper(publicationId);

        return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("scope-and-comment-for-publication-of-master"), params, mapper);
    }


    private static class EntryPublicationPropertyRowMapper implements ParameterizedRowMapper<PublicationDirectLink> {

        private final long pubId;

        private EntryPublicationPropertyRowMapper(long pubId) {

            this.pubId = pubId;
        }

        @Override
        public PublicationDirectLink mapRow(ResultSet resultSet, int row) throws SQLException {

            PublicationProperty propertyName =
                    PublicationProperty.valueOf(resultSet.getString("property_name").toUpperCase());
            String propertyValue = resultSet.getString("property_value");

            return new PublicationDirectLink(pubId, propertyName, propertyValue);
        }
    }
}
