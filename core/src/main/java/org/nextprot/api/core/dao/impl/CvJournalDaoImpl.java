package org.nextprot.api.core.dao.impl;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.CvJournalDao;
import org.nextprot.api.core.domain.CvJournal;
import org.nextprot.api.core.domain.PublicationCvJournal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	private static class JournalRowMapper extends SingleColumnRowMapper<CvJournal> {

		@Override
		public CvJournal mapRow(ResultSet resultSet, int row) throws SQLException {
			CvJournal journal = new CvJournal();
			journal.setJournalId(resultSet.getLong("cv_id"));
			journal.setAbbrev(resultSet.getString("iso_abbrev"));
			journal.setMedAbbrev(resultSet.getString("med_abbrev"));
			journal.setName(resultSet.getString("journal_name"));
			journal.setNLMid(resultSet.getString("nlmid"));
			return journal;
		}
		
	}
	
	// Why two row mappers (JournalRowMapper and PublicationCvJournalRowMapper) ?
	private static class PublicationCvJournalRowMapper extends SingleColumnRowMapper<PublicationCvJournal>{

		@Override
		public PublicationCvJournal mapRow(ResultSet resultSet, int row) throws SQLException {
			PublicationCvJournal journal = new PublicationCvJournal();
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
