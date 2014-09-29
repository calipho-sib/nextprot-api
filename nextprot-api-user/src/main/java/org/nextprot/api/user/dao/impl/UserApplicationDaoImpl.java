package org.nextprot.api.user.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.user.dao.UserApplicationDao;
import org.nextprot.api.user.domain.UserApplication;
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

@Repository
@Lazy
public class UserApplicationDaoImpl implements UserApplicationDao {

	@Autowired private SQLDictionary sqlDictionary;

	private final Log Logger = LogFactory.getLog(UserApplicationDaoImpl.class);
	
	@Autowired
	private DataSourceServiceLocator dsLocator;

	@Override
	public List<UserApplication> getUserApplications(String username) {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("owner", username);
		return new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).query(sqlDictionary.getSQLQuery("user-applications-by-username"), namedParameters, new UserApplicationRowMapper());
	}

	@Override
	public void createUserApplication(final UserApplication userApplication) {

		final String INSERT_SQL = sqlDictionary.getSQLQuery("insert-user-application");
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dsLocator.getUserDataSource());
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(
		    new PreparedStatementCreator() {
		        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
		            PreparedStatement ps =
		                connection.prepareStatement(INSERT_SQL, new String[] {"application_id"});
		            ps.setString(1, userApplication.getName());
		            ps.setString(2, userApplication.getDescription());
		            ps.setString(3, userApplication.getOrganisation());
		            ps.setString(4, userApplication.getResponsibleEmail());
		            ps.setString(5, userApplication.getResponsibleName());
		            ps.setString(6, userApplication.getOwner());
		        	ps.setString(7, userApplication.getToken());
	
		            return ps;
		        }
		    },
		    keyHolder);
		
		long applicationId =  keyHolder.getKey().longValue();
		userApplication.setId(applicationId);


	}

	@Override
	public void updateUserApplication(final UserApplication userApplication) {

	}

	@Override
	public void deleteUserApplication(final UserApplication userApplication) {

	}

	@Override
	public UserApplication getUserApplicationById(long id) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("id", id);
		List<UserApplication> queries = new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).query(sqlDictionary.getSQLQuery("user-application-by-id"), namedParameters, new UserApplicationRowMapper());
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
			app.setId(resultSet.getLong("application_id"));
			app.setName(resultSet.getString("application_name"));
			app.setDescription(resultSet.getString("description"));
			app.setOwner(resultSet.getString("owner"));
			app.setOrganisation(resultSet.getString("organisation"));
			app.setResponsibleEmail(resultSet.getString("responsible_email"));
			app.setResponsibleName(resultSet.getString("responsible_name"));

        	
			return app;
		}
	}

	@Override
	public UserApplication getUserApplication(long id) {

		String sql = "select * from np_users.user_applications where application_id = :application_id";
		SqlParameterSource namedParams = new MapSqlParameterSource("application_id", id);
		return new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).queryForObject(sql, namedParams, new UserApplicationRowMapper());

	}

	@Override
	public void deleteApplication(Long id) {

		String sql = "delete from np_users.user_applications where application_id = :application_id";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("application_id", id);
		
		int affectedRows = new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).update(sql, params);
		
		
		if(affectedRows != 1){
			String msg = "Ups something wrong occured" + affectedRows + " rows were affected instead of only 1.";
			Logger.error(msg);
			throw new NextProtException(msg);
		}
		
	}

}
