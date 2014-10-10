package org.nextprot.api.user.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.user.dao.UserDao;
import org.nextprot.api.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl implements UserDao {

	@Autowired
	private SQLDictionary sqlDictionary;
	@Autowired
	private DataSourceServiceLocator dsLocator;

	@Override
	public User getUserByUsername(String username) {
		Map<String, String> namedParams = new HashMap<String, String>();
		namedParams.put("username", "username");
		String sql = sqlDictionary.getSQLQuery("get-user-by-username");
		return new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).queryForObject(sql, namedParams, new UserRowMapper());
	}

	private static class UserRowMapper implements ParameterizedRowMapper<User> {
		public User mapRow(ResultSet resultSet, int row) throws SQLException {
			User user = new User();
			user.setId(resultSet.getLong("username"));
			user.setUsername(resultSet.getString("username"));
			user.setName(resultSet.getString("name"));
			return user;
		}
	}

	@Override
	public List<User> getUserList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateUser(User user) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getUserAuthorities(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * private static class UserListExtractor implements
	 * ResultSetExtractor<List<User>> { public List<User> extractData(ResultSet
	 * rs) throws SQLException, DataAccessException {
	 * 
	 * Map<String, User> userMap = new HashMap<String, User>(); while
	 * (rs.next()) { userList.add(user);
	 * 
	 * userMap.get(rs)
	 * 
	 * User user = new User(); userList.add(user); } return userList; } }
	 */

}
