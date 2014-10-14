package org.nextprot.api.user.dao.impl;

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
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
		return new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).query(sqlDictionary.getSQLQuery("read-user-applications-by-username"), namedParameters, new UserApplicationRowMapper());
	}

	@Override
	public long createUserApplication(final UserApplication userApplication) {

		final String INSERT_SQL = sqlDictionary.getSQLQuery("create-user-application");

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        namedParameters.addValue("application_name", userApplication.getName());
        namedParameters.addValue("description", userApplication.getDescription());
        namedParameters.addValue("organisation", userApplication.getOrganisation());
        namedParameters.addValue("responsible_email", userApplication.getResponsibleEmail());
        namedParameters.addValue("responsible_name", userApplication.getResponsibleName());
        namedParameters.addValue("owner_id", userApplication.getOwnerId());
        namedParameters.addValue("token", userApplication.getToken());

        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dsLocator.getUserDataSource());
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(INSERT_SQL, namedParameters, keyHolder, new String[] {"application_id"});

		return  keyHolder.getKey().longValue();
	}

	@Override
	public long updateUserApplication(final UserApplication userApplication) {
		throw new NextProtException("Should implement this method");
	}

	@Override
	public void deleteUserApplication(final UserApplication userApplication) {

		//TODO should put this sql on a external file
		String sql = "delete from np_users.user_applications where application_id = :application_id";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("application_id", userApplication.getId());
		
		int affectedRows = new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).update(sql, params);

		if(affectedRows != 1){
			String msg = "Ups something wrong occured" + affectedRows + " rows were affected instead of only 1.";
			Logger.error(msg);
			throw new NextProtException(msg);
		}
	}

	@Override
	public UserApplication getUserApplicationById(long id) {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("application_id", id);

		String sql = sqlDictionary.getSQLQuery("read-user-application-by-id");

		List<UserApplication> queries =
                new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).query(sql, namedParameters, new UserApplicationRowMapper());

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
            app.setOwnerId(resultSet.getLong("owner_id"));
            app.setOwner(resultSet.getString("owner"));
			app.setOrganisation(resultSet.getString("organisation"));
			app.setResponsibleEmail(resultSet.getString("responsible_email"));
			app.setResponsibleName(resultSet.getString("responsible_name"));

			return app;
		}
	}


}
