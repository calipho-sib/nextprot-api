package org.nextprot.api.user.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.JdbcTemplateUtils;
import org.nextprot.api.commons.utils.KeyValuesJdbcBatchUpdater;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.user.dao.UserDao;
import org.nextprot.api.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Repository
public class UserDaoImpl implements UserDao {

    private final Log Logger = LogFactory.getLog(UserDaoImpl.class);

	@Autowired
	private SQLDictionary sqlDictionary;

	@Autowired(required = false)
	private DataSourceServiceLocator dsLocator;

    @Override
    public long createUser(User user) {

        final String INSERT_USER_SQL = sqlDictionary.getSQLQuery("create-user");

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        namedParameters.addValue("user_name", user.getUsername());
        namedParameters.addValue("first_name", user.getFirstName());
        namedParameters.addValue("last_name", user.getLastName());

        long key = JdbcTemplateUtils.insertAndGetKey(INSERT_USER_SQL, "user_id", namedParameters, new NamedParameterJdbcTemplate(dsLocator.getUserDataSource())).longValue();

        if (user.getAuthorities() != null && !user.getAuthorities().isEmpty()) {

        	insertUserAuthorities(key, user.getAuthorities());
        }

        return key;
    }

    @Override
	public User getUserByUsername(String username) throws DataAccessException {

        Map<String, String> namedParams = new HashMap<String, String>();

		namedParams.put("user_name", username);

        String sql = sqlDictionary.getSQLQuery("read-user-by-name");

        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dsLocator.getUserDataSource());

        List<User> users = template.query(sql, namedParams, new UsersExtractor());

        if (users.isEmpty())
            throw new EmptyResultDataAccessException(1);

        return users.get(0);
	}

    @Override
    public List<User> getUserList() {

        String sql = sqlDictionary.getSQLQuery("read-user-list");

        return new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).query(sql, new UsersExtractor());
    }

	@Override
	public void updateUser(User src) {

        final String UPDATE_SQL = sqlDictionary.getSQLQuery("update-user");

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        // key to identify application to be updated
        namedParameters.addValue("user_id", src.getId());

        // values to update
        namedParameters.addValue("user_name", src.getUsername());
        namedParameters.addValue("first_name", src.getFirstName());
        namedParameters.addValue("last_name", src.getLastName());

        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dsLocator.getUserDataSource());

        int affectedRows = jdbcTemplate.update(UPDATE_SQL, namedParameters);

        if (affectedRows != 1){
            String msg = "oops something wrong occurred" + affectedRows + " rows were affected instead of only 1.";
            Logger.error(msg);
            throw new NextProtException(msg);
        }

        if (src.getAuthorities() != null && !src.getAuthorities().isEmpty()) {

            // 1. delete all roles for this user if roles exist in user_roles table of src
            deleteUserRoles(src.getId());

            // 2. insert roles with insertUserRoles(src.getKey(), src.getRoles())
            insertUserAuthorities(src.getId(), src.getAuthorities());
        }
	}

    private void insertUserAuthorities(final long userId, final Collection<GrantedAuthority> authorities) {

        String sql = sqlDictionary.getSQLQuery("create-user-roles");

        KeyValuesJdbcBatchUpdater updater = new KeyValuesJdbcBatchUpdater(new JdbcTemplate(dsLocator.getUserDataSource()), userId) {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {

                ps.setLong(1, getKey());
                ps.setString(2, getValue(i));
            }
        };

        List<String> roles = new ArrayList<String>();	
        for(GrantedAuthority ga : authorities){
        	roles.add(ga.getAuthority());
        }
        
        updater.batchUpdate(sql, new ArrayList<String>(roles));
    }

    @Override
    public void deleteUser(User user) {

        final String DELETE_SQL = sqlDictionary.getSQLQuery("delete-user");

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("user_id", user.getId());

        int affectedRows = new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).update(DELETE_SQL, params);

        if (affectedRows != 1){
            String msg = "oops something wrong occurred" + affectedRows + " rows were affected instead of only 1.";
            Logger.error(msg);
            throw new NextProtException(msg);
        }
    }

    private void deleteUserRoles(final long userId) {

        final String DELETE_SQL = sqlDictionary.getSQLQuery("delete-user-roles");

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("user_id", userId);

        new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).update(DELETE_SQL, params);
    }
}
