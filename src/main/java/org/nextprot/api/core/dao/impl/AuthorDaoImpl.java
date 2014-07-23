package org.nextprot.api.core.dao.impl;

import static org.nextprot.api.commons.utils.SQLDictionary.getSQLQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.core.dao.AuthorDao;
import org.nextprot.api.core.domain.PublicationAuthor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class AuthorDaoImpl implements AuthorDao {

	@Autowired
	private DataSourceServiceLocator dsLocator;

	private String sqlHeader = "select pubauthor_id, last_name, fore_name, suffix, rank, initials, is_person ";

	

	public List<PublicationAuthor> findAuthorsByPublicationId(Long publicationId) {
		SqlParameterSource namedParameters = new MapSqlParameterSource("publicationId", publicationId);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(getSQLQuery("publication-authors-by-publication-id"), namedParameters, new AuthorRowMapper());
	}

	
	@Override
	public List<PublicationAuthor> findAuthorsByPublicationIds(List<Long> publicationIds) {
		SqlParameterSource namedParameters = new MapSqlParameterSource("publicationIds", publicationIds);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(getSQLQuery("publication-authors-by-publication-ids"), namedParameters, new PublicationAuthorRowMapper());
	};

	
	
	
	private static class AuthorRowMapper implements ParameterizedRowMapper<PublicationAuthor> {

		public PublicationAuthor mapRow(ResultSet resultSet, int row) throws SQLException {

			// Need to use a mapper, but it is not so bad if we don't want to use reflection since the database may use different names
			PublicationAuthor author = new PublicationAuthor();
			author.setAuthorId(resultSet.getLong("pubauthor_id"));
			author.setLastName(resultSet.getString("last_name"));
			String name = resultSet.getString("fore_name").isEmpty() ? resultSet.getString("initials")  : resultSet.getString("fore_name");
			author.setPerson(resultSet.getBoolean("is_person"));
			author.setForeName(name);
			author.setSuffix(resultSet.getString("suffix"));
			author.setRank(resultSet.getInt("rank"));
			return author;
		}
	}
	
	private static class PublicationAuthorRowMapper implements ParameterizedRowMapper<PublicationAuthor> {

		@Override
		public PublicationAuthor mapRow(ResultSet resultSet, int row) throws SQLException {
			PublicationAuthor author = new PublicationAuthor();
			author.setAuthorId(resultSet.getLong("pubauthor_id"));
			author.setLastName(resultSet.getString("last_name"));
			author.setPerson(resultSet.getBoolean("is_person"));
			String name = resultSet.getString("fore_name").isEmpty() ? resultSet.getString("initials")  : resultSet.getString("fore_name");
			author.setForeName(name);
			author.setSuffix(resultSet.getString("suffix"));
			author.setRank(resultSet.getInt("rank"));
			author.setPublicationId(resultSet.getLong("publication_id"));
			return author;
		}
		
	}

}
