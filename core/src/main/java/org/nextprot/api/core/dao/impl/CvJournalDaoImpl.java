package org.nextprot.api.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.CvJournalDao;
import org.nextprot.api.core.domain.CvJournal;
import org.nextprot.api.core.domain.PublicationCvJournal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class CvJournalDaoImpl implements CvJournalDao {
	
	@Autowired private SQLDictionary sqlDictionary;

	@Autowired private DataSourceServiceLocator dsLocator;
	
	@Override
	public CvJournal findById(Long journalId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("journalId", journalId);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).queryForObject(sqlDictionary.getSQLQuery("cvjournal-by-id"), params, new JournalRowMapper());
	}
	
	@Override
	public List<CvJournal> findByPublicationId(Long publicationId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("publicationId", publicationId);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("cvjournal-by-publication-id"), params, new JournalRowMapper());
	}	
	
	@Override
	public List<PublicationCvJournal> findCvJournalsByPublicationIds(List<Long> publicationIds) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("publicationIds", publicationIds);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("cvjournal-by-publication-ids"), params, new PublicationCvJournalRowMapper());
	}
	
	private static class JournalRowMapper implements ParameterizedRowMapper<CvJournal> {

		@Override
		public CvJournal mapRow(ResultSet resultSet, int row) throws SQLException {
			CvJournal journal = new CvJournal();
			journal.setJournalId(resultSet.getLong("cv_id"));
			journal.setName(resultSet.getString("journal_name"));
			return journal;
		}
		
	}
	
	private static class PublicationCvJournalRowMapper implements ParameterizedRowMapper<PublicationCvJournal>{

		@Override
		public PublicationCvJournal mapRow(ResultSet resultSet, int row) throws SQLException {
			PublicationCvJournal journal = new PublicationCvJournal();
			journal.setJournalId(resultSet.getLong("cv_id"));
			journal.setName(resultSet.getString("journal_name"));
			journal.setPublicationId(resultSet.getLong("publication_id"));
			return journal;
		}
	}
}
