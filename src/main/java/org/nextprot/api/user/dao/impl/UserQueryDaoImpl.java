package org.nextprot.api.user.dao.impl;

import static org.nextprot.api.commons.utils.SQLDictionary.getSQLQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.user.dao.UserQueryDao;
import org.nextprot.api.user.domain.UserQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
@Lazy
public class UserQueryDaoImpl implements UserQueryDao {

	@Autowired private DataSourceServiceLocator dsLocator;
	
	@Override
	public List<UserQuery> getUserQueries(String username) {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("username", username);
		return new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).query(getSQLQuery("advanced-user-query-by-username"), namedParameters, new UserQueryRowMapper());
	}

	@Override
	public List<UserQuery> getPublicQueries() {
		return new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).query(getSQLQuery("advanced-public-query"), new UserQueryRowMapper());
	}

	
	
	private static class UserQueryRowMapper implements ParameterizedRowMapper<UserQuery> {

		public UserQuery mapRow(ResultSet resultSet, int row) throws SQLException {

			UserQuery query = new UserQuery();
			query.setUserQueryId(resultSet.getInt("advanced_user_query_id"));
			query.setTitle(resultSet.getString("title"));
			query.setDescription(resultSet.getString("description"));
			query.setSparql(resultSet.getString("sparql"));
			query.setPublished(resultSet.getString("public").equals("Y"));
			query.setSubmitted(resultSet.getString("submitted"));
			query.setUsername(resultSet.getString("username"));
			return query;
		}
	}

	@Override
	public long saveUserQuery(final UserQuery userQuery) {
		final String INSERT_SQL = getSQLQuery("advanced-user-query-insert");

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

		final String UPDATE_SQL = getSQLQuery("advanced-user-query-update");
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

		final String DELETE_SQL = getSQLQuery("advanced-user-query-delete");
		JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dsLocator.getUserDataSource());

		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(DELETE_SQL);
				ps.setLong(1, id);
				return ps;
			}
		});

	}

	@Override
	public List<UserQuery> getNextprotQueries() {
		return new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).query(getSQLQuery("advanced-nextprot-query"), new UserQueryRowMapper());
	}

	@Override
	public UserQuery getUserQueryById(long id) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("id", id);
		List<UserQuery> queries = new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).query(getSQLQuery("advanced-user-query-by-id"), namedParameters, new UserQueryRowMapper());
		NPreconditions.checkTrue(queries.size() == 1, "User query not found");
		return queries.get(0);
	}

}
