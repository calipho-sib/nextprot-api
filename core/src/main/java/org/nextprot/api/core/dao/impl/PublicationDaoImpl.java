package org.nextprot.api.core.dao.impl;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.DateFormatter;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.PublicationDao;
import org.nextprot.api.core.dao.JournalDao;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.publication.Journal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


@Repository
public class PublicationDaoImpl implements PublicationDao {

	private final static DateFormatter DATE_FORMATTER = new DateFormatter();

	@Autowired private SQLDictionary sqlDictionary;
	@Autowired private JournalDao journalDao;

	@Autowired
	private DataSourceServiceLocator dsLocator;

	@Override
	public List<Long> findSortedPublicationIdsByMasterId(Long masterId) {
		Map<String, Object> params = new HashMap<>();
		params.put("identifierId", masterId);
		params.put("publicationTypes", Arrays.asList(10, 20, 30, 40, 50, 60, 70, 80));

		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("publication-sorted-for-master"), params, new LongRowMapper("resource_id"));
	}

	@Override
	public List<Publication> findSortedPublicationsByMasterId(Long masterId) {
		Map<String, Object> params = new HashMap<>();
		params.put("identifierId", masterId);
		params.put("publicationTypes", Arrays.asList(10, 20, 30, 40, 50, 60, 70, 80));

		List<Long> publicationIds = findSortedPublicationIdsByMasterId(masterId);

		// get all journals found for all publication ids
		List<Journal> journals = journalDao.findJournalsByPublicationIds(publicationIds);

		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("publication-sorted-for-master"), params, new PublicationRowMapper(journals));
	}

	@Override
	public Publication findPublicationById(long publicationId) {
		SqlParameterSource namedParameters = new MapSqlParameterSource("resourceId", publicationId);

		// get all journals found for publication id
		List<Journal> cvJournals = journalDao.findJournalsByPublicationIds(Collections.singletonList(publicationId));

		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).queryForObject(sqlDictionary.getSQLQuery("publication-by-resourceid"), namedParameters, new PublicationRowMapper(cvJournals));
	}

	@Override
	public List<Publication> findPublicationByTitle(String title) {
		List<Long> ids = findPublicationIdsByTitle(title);

		if (!ids.isEmpty()) {
			return Collections.singletonList(findPublicationById(ids.get(0)));
		}

		return Collections.emptyList();
	}

	@Override
	public Publication findPublicationByMD5(String md5) {
		List<Long> ids = findPublicationIdsByMD5(md5);

		if (!ids.isEmpty()) {
			return findPublicationById(ids.get(0));
		}

		return null;
	}

	private List<Long> findPublicationIdsByTitle(String title) {
		SqlParameterSource namedParameters = new MapSqlParameterSource("title", title);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).queryForList(sqlDictionary.getSQLQuery("publication-id-by-title"), namedParameters, Long.class);
	}

	private List<Long> findPublicationIdsByMD5(String title) {
		SqlParameterSource namedParameters = new MapSqlParameterSource("title", title);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).queryForList(sqlDictionary.getSQLQuery("publication-id-by-md5"), namedParameters, Long.class);
	}

	@Override
	public List<Long> findAllPublicationsIds() {
		SqlParameterSource namedParameters = new MapSqlParameterSource();
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("publication-allids"), namedParameters, new LongRowMapper("pub_id"));
	}

	private static class LongRowMapper implements ParameterizedRowMapper<Long> {

		private final String columnName;

		LongRowMapper(String columnName) {
			this.columnName = columnName;
		}

		public Long mapRow(ResultSet resultSet, int row) throws SQLException {
			return resultSet.getLong(columnName);
		}
	}

	private static class PublicationRowMapper implements ParameterizedRowMapper<Publication> {

		private final Map<Long, Journal> journalMap;

		PublicationRowMapper(List<Journal> journals) {

			this.journalMap = Maps.uniqueIndex(journals, new Function<Journal, Long>() {
				@Override
				public Long apply(Journal journal) {
					return journal.getPublicationId();
				}
			});;
		}

		public Publication mapRow(ResultSet resultSet, int row) throws SQLException {

			Publication publication = new Publication();

			// set publication id
			publication.setId(resultSet.getLong("resource_id"));

			// set publication md5
			publication.setMD5(resultSet.getString("md5"));

			// set publication abstract
			publication.setAbstractText(resultSet.getString("abstract_text"));

			// set publication type
			setPublicationType(publication, resultSet);

			// set publication date
			setPublicationDate(publication, resultSet);

			// set publication details
			publication.setIsLargeScale(resultSet.getLong("is_largescale")>0);
			publication.setIsCurated(resultSet.getLong("is_curated")>0);
			publication.setIsComputed(resultSet.getLong("is_computed")>0);

			// set infos on publication medium, volume, issue, pages, journal, book...
			setPublicationMedium(publication, resultSet);

			// set publication title
			setPublicationTitle(publication, resultSet);

			return publication;
		}

		private void setPublicationDate(Publication publication, ResultSet resultSet) throws SQLException {

			int cvDatePrecisionId = resultSet.getInt("cv_date_precision_id");

			if (cvDatePrecisionId != 1) {

				Date date = resultSet.getDate("publication_date");

				publication.setPublicationDate(date);
				publication.setTextDate(DATE_FORMATTER.format(date, cvDatePrecisionId));
			}
		}

		private void setPublicationType(Publication publication, ResultSet resultSet) throws SQLException {

			String pubType = resultSet.getString("pub_type");

			if (pubType.equals("ONLINE PUBLICATION")) {
				publication.setPublicationType("ONLINE_PUBLICATION");
			} else if (pubType.equals("SUBMISSION")) {
				publication.setPublicationType(pubType);
			} else {
				publication.setPublicationType(pubType);
			}
		}

		private void setPublicationTitle(Publication publication, ResultSet resultSet) throws SQLException {

			String pubType = publication.getPublicationType();

			if (pubType.equals("ONLINE_PUBLICATION")) {
				// In case it is a online publication
				publication.setTitle(resultSet.getString("volume"));
			} else if (pubType.equals("SUBMISSION")) {
				String title = resultSet.getString("title");
				publication.setTitle(title);

				String subDB = "Submitted to " + resultSet.getString("submission_database");
				if (subDB != null) {
					if (!title.startsWith(subDB)) { // add the submission database is necessary
						//publication.setTitle(subDB + title);
						publication.setSubmission(subDB);
					}
				}

			} else {
				publication.setTitle(resultSet.getString("title"));
			}

			String title = publication.getTitle();
			if (title.startsWith("["))
				title = title.substring(1);
			if (title.endsWith("]"))
				title = title.substring(0, title.length() - 1);

			publication.setTitle(title.replace("[", "(").replace("]", ")"));
		}

		private void setPublicationMedium(Publication publication, ResultSet resultSet) throws SQLException {

			String pubType = publication.getPublicationType();

			// volume is only defined in "article" and "online publication"
			if ("BOOK".equals(pubType)) {
				publication.setEditedVolumeBook(resultSet.getString("volume"), resultSet.getString("publisher"), resultSet.getString("city"),
						resultSet.getString("first_page"), resultSet.getString("last_page"));
			}
			else if ("ARTICLE".equals(pubType)) {
				Journal journal = journalMap.get(publication.getPublicationId());
				publication.setJournal(journal, resultSet.getString("volume"), resultSet.getString("issue"),
						resultSet.getString("first_page"), resultSet.getString("last_page"));
			}
			else if ("ONLINE_PUBLICATION".equals(pubType)) {
				publication.setOnlineResource(resultSet.getString("volume"), resultSet.getString("title"));
			}
		}
	}
}
