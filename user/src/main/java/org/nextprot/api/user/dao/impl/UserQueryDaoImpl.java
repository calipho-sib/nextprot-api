package org.nextprot.api.user.dao.impl;

import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.user.dao.UserQueryDao;
import org.nextprot.api.user.domain.UserQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@Lazy
public class UserQueryDaoImpl implements UserQueryDao {

	@Autowired private SQLDictionary sqlDictionary;

	@Autowired private DataSourceServiceLocator dsLocator;
	
	@Override
	public List<UserQuery> getUserQueries(String username) {

		String sql = sqlDictionary.getSQLQuery("read-user-queries-by-username");

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		namedParameters.addValue("user_name", username);

		List<UserQuery> userQueryList = new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).query(sql, namedParameters, new UserQueryRowMapper());

		for (UserQuery userQuery : userQueryList) {

			userQuery.setTags(getQueryTagsById(userQuery.getUserQueryId()));
		}

		return userQueryList;
	}

	/**
	 * Get the tag names that belongs to the query {@code queryId}
	 *
	 * @param queryId the query identifier
	 * @return a set of tags
	 */
	private Set<String> getQueryTagsById(long queryId) {

		SqlParameterSource namedParameters = new MapSqlParameterSource("query_id", queryId);

		List<String> tags = new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).queryForList(sqlDictionary.getSQLQuery("read-user-query-tags-by-id"), namedParameters, String.class);

		return new HashSet<String>(tags);
	}

	@Override
	public UserQuery getUserQueryById(long id) {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("id", id);
		List<UserQuery> queries = new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).query(sqlDictionary.getSQLQuery("advanced-user-query-by-id"), namedParameters, new UserQueryRowMapper());
		NPreconditions.checkTrue(queries.size() == 1, "User query not found");
		return queries.get(0);
	}

	@Override
	public List<UserQuery> getUserQueriesByTag(String tag) {

		return new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).query(sqlDictionary.getSQLQuery("advanced-public-query"), new UserQueryRowMapper());
	}

	@Override
	public List<UserQuery> getPublishedQueries() {
		return new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).query(sqlDictionary.getSQLQuery("advanced-public-query"), new UserQueryRowMapper());
	}

	@Override
	public long createUserQuery(final UserQuery userQuery) {
		final String INSERT_SQL = sqlDictionary.getSQLQuery("advanced-user-query-insert");

		JdbcTemplate jdbcTemplate = new JdbcTemplate(dsLocator.getUserDataSource());

		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[] { "advanced_user_query_id" });
				ps.setString(1, userQuery.getTitle());
				ps.setString(2, userQuery.getDescription());
				ps.setString(3, userQuery.getSparql());
				ps.setString(4, userQuery.getUsername());
				return ps;
			}
		}, keyHolder);

		return keyHolder.getKey().longValue();

	}

	@Override
	public void updateUserQuery(final UserQuery userQuery) {

		final String UPDATE_SQL = sqlDictionary.getSQLQuery("advanced-user-query-update");
		JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dsLocator.getUserDataSource());

		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(UPDATE_SQL);
				ps.setString(1, userQuery.getTitle());
				ps.setString(2, userQuery.getDescription());
				ps.setString(3, userQuery.getSparql());
				ps.setString(4, userQuery.getPublished() ? "Y" : "N");
				ps.setLong(5, userQuery.getUserQueryId());
				return ps;
			}
		});
	}

	@Override
	public void deleteUserQuery(final long id) {

		final String DELETE_SQL = sqlDictionary.getSQLQuery("advanced-user-query-delete");
		JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dsLocator.getUserDataSource());

		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(DELETE_SQL);
				ps.setLong(1, id);
				return ps;
			}
		});

	}

	private static class UserQueryRowMapper implements ParameterizedRowMapper<UserQuery> {

		public UserQuery mapRow(ResultSet resultSet, int row) throws SQLException {

			UserQuery query = new UserQuery();
			query.setUserQueryId(resultSet.getInt("query_id"));
			query.setTitle(resultSet.getString("title"));
			query.setDescription(resultSet.getString("description"));
			query.setSparql(resultSet.getString("sparql"));
			query.setPublished(resultSet.getString("published").equals("Y"));
			query.setUsername(resultSet.getString("user_name"));
			return query;
		}
	}
}
