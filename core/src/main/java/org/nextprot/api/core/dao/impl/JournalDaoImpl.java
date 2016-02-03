package org.nextprot.api.core.dao.impl;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.JournalDao;
import org.nextprot.api.core.domain.publication.PublicationType;
import org.nextprot.api.core.domain.publication.Journal;
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
public class JournalDaoImpl implements JournalDao {
	
	@Autowired private SQLDictionary sqlDictionary;

	@Autowired private DataSourceServiceLocator dsLocator;
	
	@Override
	public List<Journal> findJournalsByPublicationIds(List<Long> publicationIds) {

		Map<String, Object> params = new HashMap<>();
		params.put("publicationIds", publicationIds);

		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("cvjournal-by-publication-ids"), params, new PublicationCvJournalRowMapper());
	}
	
	// Why two row mappers (JournalRowMapper and PublicationCvJournalRowMapper) ?
	private static class PublicationCvJournalRowMapper implements ParameterizedRowMapper<Journal>{

		@Override
		public Journal mapRow(ResultSet resultSet, int row) throws SQLException {

			Journal journal = new Journal(PublicationType.ARTICLE);

			journal.setJournalId(resultSet.getLong("cv_id"));
			journal.setName(resultSet.getString("journal_name"));
			journal.setAbbrev(resultSet.getString("iso_abbrev"));
			journal.setMedAbbrev(resultSet.getString("med_abbrev"));
			journal.setPublicationId(resultSet.getLong("publication_id"));
			journal.setNLMid(resultSet.getString("nlmid"));

			return journal;
		}
	}
}
