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
	public List<UserApplication> getUserApplicationsByOwnerId(long userId) {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        namedParameters.addValue("owner_id", userId);

		return new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).query(sqlDictionary.getSQLQuery("read-user-applications-by-owner-id"), namedParameters, new UserApplicationRowMapper());
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
	public void updateUserApplication(final UserApplication src) {

        final String UPDATE_SQL = sqlDictionary.getSQLQuery("update-user-application");

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        // key to identify application to be updated
        namedParameters.addValue("application_id", src.getId());

        // values to update
        namedParameters.addValue("application_name", src.getName());
        namedParameters.addValue("description", src.getDescription());
        namedParameters.addValue("organisation", src.getOrganisation());
        namedParameters.addValue("responsible_name", src.getResponsibleName());
        namedParameters.addValue("responsible_email", src.getResponsibleEmail());
        namedParameters.addValue("website", src.getWebsite());
        namedParameters.addValue("token", src.getToken());
        namedParameters.addValue("status", src.getStatus());
        namedParameters.addValue("user_data_access", src.getUserDataAccess());
        namedParameters.addValue("origins", src.getOrigins());

        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dsLocator.getUserDataSource());

        int affectedRows = jdbcTemplate.update(UPDATE_SQL, namedParameters);

        if (affectedRows != 1) {

            String msg = "something wrong occurred: " + affectedRows + " rows were affected (expected=1).";
            Logger.error(msg);

            throw new NextProtException(msg);
        }
	}

	@Override
	public void deleteUserApplication(final UserApplication userApplication) {

        final String DELETE_SQL = sqlDictionary.getSQLQuery("delete-user-application");

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("application_id", userApplication.getId());
		
		int affectedRows = new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).update(DELETE_SQL, params);

		if(affectedRows != 1){
			String msg = "oops something wrong occured" + affectedRows + " rows were affected instead of only 1.";
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
            app.setOrganisation(resultSet.getString("organisation"));
            app.setResponsibleName(resultSet.getString("responsible_name"));
            app.setResponsibleEmail(resultSet.getString("responsible_email"));
            app.setWebsite(resultSet.getString("website"));
            app.setOwnerId(resultSet.getLong("owner_id"));
            app.setOwner(resultSet.getString("owner"));
			app.setToken(resultSet.getString("token"));
            app.setStatus(resultSet.getString("status"));
            app.setUserDataAccess(resultSet.getString("user_data_access"));
            app.setOrigins(resultSet.getString("origins"));
            app.setCreationDate(resultSet.getDate("creation_date"));

			return app;
		}
	}
}
