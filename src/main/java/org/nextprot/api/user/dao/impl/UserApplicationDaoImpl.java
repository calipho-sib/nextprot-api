package org.nextprot.api.user.dao.impl;

import static org.nextprot.api.commons.utils.SQLDictionary.getSQLQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.user.dao.UserApplicationDao;
import org.nextprot.api.user.domain.UserApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

@Repository
@Lazy
public class UserApplicationDaoImpl implements UserApplicationDao {

	@Autowired
	private DataSourceServiceLocator dsLocator;

	@Override
	public List<UserApplication> getUserApplications(String username) {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("username", username);
		return new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).query(getSQLQuery("user-applications-by-username"), namedParameters, new UserApplicationRowMapper());
	}

	@Override
	public void createUserApplication(final UserApplication userApplication) {

		JdbcTemplate jdbcTemplate = new JdbcTemplate(dsLocator.getUserDataSource());

		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(getSQLQuery("advanced-user-query-insert"));
				ps.setString(1, userApplication.getId());
				return ps;
			}
		});

	}

	@Override
	public void updateUserApplication(final UserApplication userApplication) {

		JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dsLocator.getUserDataSource());

		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(getSQLQuery("advanced-user-query-update"));
				ps.setString(1, userApplication.getId());
				return ps;
			}
		});
	}

	@Override
	public void deleteUserApplication(final UserApplication userApplication) {

		JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dsLocator.getUserDataSource());

		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(getSQLQuery("user-application-delete"));
				ps.setString(1, userApplication.getId());
				return ps;
			}
		});

	}

	@Override
	public UserApplication getUserApplicationById(long id) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("id", id);
		List<UserApplication> queries = new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).query(getSQLQuery("user-application-by-id"), namedParameters, new UserApplicationRowMapper());
		NPreconditions.checkTrue(queries.size() == 1, "User application not found");
		return queries.get(0);
	}

	/**
	 * Row mapper
	 * 
	 * @author dteixeira
	 */
	private static class UserApplicationRowMapper implements ParameterizedRowMapper<UserApplication> {

		public UserApplication mapRow(ResultSet resultSet, int row) throws SQLException {

			UserApplication app = new UserApplication();
			app.setName(resultSet.getString("name"));
			app.setDescription(resultSet.getString("description"));
			app.setOrganization(resultSet.getString("organization"));
			return app;
		}
	}

}
