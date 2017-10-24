package org.nextprot.api.core.dao.impl;

import com.google.common.collect.Maps;
import org.apache.log4j.Logger;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.DateFormatter;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.CvJournalDao;
import org.nextprot.api.core.dao.PublicationDao;
import org.nextprot.api.core.domain.CvJournal;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.PublicationCvJournal;
import org.nextprot.api.core.domain.publication.JournalResourceLocator;
import org.nextprot.api.core.domain.publication.PublicationDirectLink;
import org.nextprot.api.core.domain.publication.PublicationProperty;
import org.nextprot.api.core.domain.publication.PublicationType;
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

	private static final Logger LOGGER = Logger.getLogger(PublicationDaoImpl.class);

	private static final DateFormatter DATE_FORMATTER = new DateFormatter();

	@Autowired private SQLDictionary sqlDictionary;
	@Autowired private CvJournalDao journalDao;

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
		List<PublicationCvJournal> journals = journalDao.findCvJournalsByPublicationIds(publicationIds);
		
		List<Publication> publications = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("publication-sorted-for-master"), params, new PublicationRowMapper(journals));

		 // get all entry publication properties     
        Map<Long, Map<PublicationProperty, TreeSet<PublicationDirectLink>>> publiPropMap =
                findEntryPublicationPropertiesByMasterId(masterId);

        // attach properties to each publication
		for (Publication pub: publications) {
		    long pubId = pub.getPublicationId();

			if (publiPropMap.containsKey(pubId)) {

				pub.setLinks(publiPropMap.get(pubId));
			}
		}
		
		return publications;
	}

	@Override
	public Publication findPublicationById(long publicationId) {
		SqlParameterSource namedParameters = new MapSqlParameterSource("resourceId", publicationId);

		// get the journal found for publication id
		List<PublicationCvJournal> journals = journalDao.findCvJournalsByPublicationIds(Collections.singletonList(publicationId));

		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).queryForObject(sqlDictionary.getSQLQuery("publication-by-resourceid"), namedParameters, new PublicationRowMapper(journals));
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
	public Publication findPublicationByDatabaseAndAccession(String database, String accession) {

		List<Long> ids = findPublicationIdsByDatabaseAndAccession(database, accession);

		if (!ids.isEmpty()) {
			return findPublicationById(ids.get(0));
		}

		return null;
	}

	@Override
	public Publication findPublicationByMD5(String md5) {
		List<Long> ids = findPublicationIdsByMD5(md5);

		if (!ids.isEmpty()) {
			return findPublicationById(ids.get(0));
		}

		return null;
	}

	private List<Long> findPublicationIdsByDatabaseAndAccession(String database, String accession) {
		
	  MapSqlParameterSource namedParameters = new MapSqlParameterSource();
	  namedParameters.addValue("database", database);
	  namedParameters.addValue("accession", accession);
	  
  	  return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).queryForList(sqlDictionary.getSQLQuery("publication-id-by-database-and-accession"), namedParameters, Long.class);
	}
	
	private List<Long> findPublicationIdsByTitle(String title) {
		SqlParameterSource namedParameters = new MapSqlParameterSource("title", title);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).queryForList(sqlDictionary.getSQLQuery("publication-id-by-title"), namedParameters, Long.class);
	}

	private List<Long> findPublicationIdsByMD5(String md5) {
		SqlParameterSource namedParameters = new MapSqlParameterSource("md5", md5);
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

		@Override
		public Long mapRow(ResultSet resultSet, int row) throws SQLException {
			return resultSet.getLong(columnName);
		}
	}

	@Override
	public Map<Long, Map<PublicationProperty, TreeSet<PublicationDirectLink>>> findEntryPublicationPropertiesByMasterId(Long masterId) {

		Map<String, Object> params = new HashMap<>();
		params.put("masterId", masterId);
		EntryPublicationPropertyRowMapper mapper = new EntryPublicationPropertyRowMapper();		
		new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("publication-properties-comment-or-scope-for-master"), params, mapper);
		return mapper.getResult();
		
	}
	
	private static class EntryPublicationPropertyRowMapper implements ParameterizedRowMapper<Object> {

        private Map<Long, Map<PublicationProperty, TreeSet<PublicationDirectLink>>> result = new HashMap<>();
				
		public Map<Long, Map<PublicationProperty, TreeSet<PublicationDirectLink>>> getResult() { return result; }
		
		@Override
		public Object mapRow(ResultSet resultSet, int row) throws SQLException {

			Long pubId = resultSet.getLong("pub_id");
			PublicationProperty propertyName =
                    PublicationProperty.valueOf(resultSet.getString("property_name").toUpperCase());
			String propertyValue = resultSet.getString("property_value");

			PublicationDirectLink publicationDirectLink = new PublicationDirectLink(pubId, propertyName, propertyValue);

            result
                    .computeIfAbsent(pubId, k -> new HashMap<>())
                    .computeIfAbsent(propertyName, k -> new TreeSet<>())
                    .add(publicationDirectLink);

            return null;
		}
	}
	
	private static class PublicationRowMapper implements ParameterizedRowMapper<Publication> {

		private final Map<Long, PublicationCvJournal> pubIdToJournalMap;

		PublicationRowMapper(List<PublicationCvJournal> journals) {

			this.pubIdToJournalMap = Maps.uniqueIndex(journals, journal -> journal.getPublicationId());
		}

		@Override
		public Publication mapRow(ResultSet resultSet, int row) throws SQLException {

			Publication publication = new Publication();

			// set publication id
			publication.setId(resultSet.getLong("resource_id"));

			// set publication md5
			publication.setMD5(resultSet.getString("md5"));

			// set publication abstract
			publication.setAbstractText(resultSet.getString("abstract_text"));

			// set publication type
			publication.setPublicationType(PublicationType.valueOfName(resultSet.getString("pub_type")).toString());

			// set publication date
			setPublicationDate(publication, resultSet);

			// set publication details
			publication.setIsLargeScale(resultSet.getLong("is_largescale")>0);
			publication.setIsCurated(resultSet.getLong("is_curated")>0);
			publication.setIsComputed(resultSet.getLong("is_computed")>0);

			// set infos on publication medium, volume, issue, pages, journal, book...
			setPublicationLocator(publication, resultSet);

			// set publication title
			setPublicationTitle(publication, resultSet);

			return publication;
		}

		private void setPublicationDate(Publication publication, ResultSet resultSet) throws SQLException {

			int cvDatePrecisionId = resultSet.getInt("cv_date_precision_id");

			// There is no date defined for online publication
			if (cvDatePrecisionId != 1) {

				Date date = resultSet.getDate("publication_date");

				publication.setPublicationDate(date);
				publication.setTextDate(DATE_FORMATTER.format(date, cvDatePrecisionId));
			}
		}

		private void setPublicationTitle(Publication publication, ResultSet resultSet) throws SQLException {

			PublicationType publicationType = PublicationType.valueOfName(publication.getPublicationType());
			String title;

			if (publicationType == PublicationType.ONLINE_PUBLICATION) {
				String titleForWebPage = resultSet.getString("title_for_web_resource");
				title = (titleForWebPage != null) ? titleForWebPage : "";
			}
			else if (publicationType == PublicationType.SUBMISSION) {
				String subDB = "Submitted to " + resultSet.getString("submission_database");
				title = resultSet.getString("title");
				// add the submission database is necessary
				if (!title.startsWith(subDB)) {
					publication.setSubmission(subDB);
				}
			}
			else {
				title = resultSet.getString("title");
			}

			// Post-process title
			if (title.startsWith("["))
				title = title.substring(1);
			if (title.endsWith("]"))
				title = title.substring(0, title.length() - 1);

			if(title.length() > 1) {
			  	String penultimate = title.substring(title.length() - 2,title.length() - 1);
			  	if("]".equals(penultimate)) // Sometimes the closing bracket is inconstantly placed before the final dot (eg: pubid 10665637)
					title = title.substring(0, title.length() - 2) + ".";
			}
			
			publication.setTitle(title);
		}

		private void setPublicationLocator(Publication publication, ResultSet resultSet) throws SQLException {

			PublicationType pubType = PublicationType.valueOfName(publication.getPublicationType());

			if (pubType == PublicationType.BOOK) {
				publication.setEditedVolumeBookLocation(resultSet.getString("volume"), resultSet.getString("publisher"), resultSet.getString("city"),
						resultSet.getString("first_page"), resultSet.getString("last_page"));
			}
			else if (pubType == PublicationType.ONLINE_PUBLICATION) {
				publication.setOnlineResourceLocation(resultSet.getString("volume"), resultSet.getString("title"));
			}
			else if (pubType == PublicationType.ARTICLE) {
				JournalResourceLocator journalLocation = new JournalResourceLocator();
				CvJournal journal;

				if (pubIdToJournalMap.containsKey(publication.getPublicationId())) {

					journal = pubIdToJournalMap.get(publication.getPublicationId());
				} else {
					String journalName = resultSet.getString("journal_from_property");

					if (journalName != null) {
						journal = new CvJournal();
						journal.setName(journalName);
					}
					else {
						LOGGER.error("Article with publication id '" + publication.getPublicationId() + "' could not be located in a journal");
						return;
					}
				}
				journalLocation.setJournal(journal);
				publication.setJournalResourceLocator(journalLocation, resultSet.getString("volume"), resultSet.getString("issue"),
						resultSet.getString("first_page"), resultSet.getString("last_page"));
			}
		}
	}
}
