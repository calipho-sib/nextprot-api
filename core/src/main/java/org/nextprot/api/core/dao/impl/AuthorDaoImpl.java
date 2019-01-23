package org.nextprot.api.core.dao.impl;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.AuthorDao;
import org.nextprot.api.core.domain.PublicationAuthor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AuthorDaoImpl implements AuthorDao {

	@Autowired private SQLDictionary sqlDictionary;

	@Autowired
	private DataSourceServiceLocator dsLocator;


	public List<PublicationAuthor> findAuthorsByPublicationId(Long publicationId) {
		SqlParameterSource namedParameters = new MapSqlParameterSource("publicationId", publicationId);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("publication-authors-by-publication-id"), namedParameters, new AuthorRowMapper(publicationId));
	}

	@Override
	public List<PublicationAuthor> findAuthorsByPublicationIds(List<Long> publicationIds) {
		SqlParameterSource namedParameters = new MapSqlParameterSource("publicationIds", publicationIds);
		
		if(publicationIds.isEmpty()) {
			return new ArrayList<>();
		}
		
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("publication-authors-by-publication-ids"), namedParameters, new PublicationAuthorRowMapper());
	}

	private static class AuthorRowMapper extends SingleColumnRowMapper<PublicationAuthor> {

		private final long publicationId;

		AuthorRowMapper(long publicationId) {

			this.publicationId = publicationId;
		}

		public PublicationAuthor mapRow(ResultSet resultSet, int row) throws SQLException {

			// Need to use a mapper, but it is not so bad if we don't want to use reflection since the database may use different names
			PublicationAuthor author = new PublicationAuthor();
			author.setAuthorId(resultSet.getLong("pubauthor_id"));
			author.setLastName(resultSet.getString("last_name"));
			author.setInitials(resultSet.getString("initials"));
			//String name = resultSet.getString("fore_name").isEmpty() ? resultSet.getString("initials")  : resultSet.getString("fore_name");
			// copiing initials when forename is empty was causing discrepencies in new solr indexes
			author.setForeName(resultSet.getString("fore_name"));
			author.setPerson(resultSet.getBoolean("is_person"));
			author.setEditor(resultSet.getBoolean("is_editor"));
			author.setSuffix(resultSet.getString("suffix"));
			author.setRank(resultSet.getInt("rank"));
			author.setPublicationId(publicationId);
			return author;
		}
	}
	
	private static class PublicationAuthorRowMapper extends SingleColumnRowMapper<PublicationAuthor> {

		@Override
		public PublicationAuthor mapRow(ResultSet resultSet, int row) throws SQLException {
			PublicationAuthor author = new PublicationAuthor();
			author.setAuthorId(resultSet.getLong("pubauthor_id"));
			author.setInitials(resultSet.getString("initials"));
			author.setLastName(resultSet.getString("last_name"));
			author.setPerson(resultSet.getBoolean("is_person"));
			author.setEditor(resultSet.getBoolean("is_editor"));
			String name = resultSet.getString("fore_name").isEmpty() ? resultSet.getString("initials")  : resultSet.getString("fore_name");
			author.setForeName(name);
			author.setSuffix(resultSet.getString("suffix"));
			author.setRank(resultSet.getInt("rank"));
			author.setPublicationId(resultSet.getLong("publication_id"));
			return author;
		}
		
	}
}
