package org.nextprot.api.user.dao.impl;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.SetMultimap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.JdbcTemplateUtils;
import org.nextprot.api.commons.utils.KeyValuesJdbcBatchUpdater;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.commons.utils.SqlBoolean;
import org.nextprot.api.user.dao.UserQueryDao;
import org.nextprot.api.user.domain.UserQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Lazy
public class UserQueryDaoImpl implements UserQueryDao {

	private final Log Logger = LogFactory.getLog(UserQueryDaoImpl.class);

	@Autowired private SQLDictionary sqlDictionary;

	@Autowired private DataSourceServiceLocator dsLocator;

	@Override
	public List<UserQuery> getUserQueries(String username) {

		String sql = sqlDictionary.getSQLQuery("read-user-queries-by-username");

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		namedParameters.addValue("user_name", username);

		return queryList(sql, namedParameters);
	}

	@Override
	public UserQuery getUserQueryById(long queryId) {

		String sql = sqlDictionary.getSQLQuery("read-user-query-by-id");

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("query_id", queryId);

		List<UserQuery> queries = new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).query(sql, namedParameters, new UserQueryRowMapper());

		if (queries.isEmpty())
			return null;

		UserQuery query = queries.get(0);

		SetMultimap<Long, String> tags = getQueryTags(Arrays.asList(queries.get(0).getUserQueryId()));

		query.setTags(tags.get(queryId));

		return query;
	}

	@Override
	public List<UserQuery> getUserQueriesByTag(String tag) {

		String sql = sqlDictionary.getSQLQuery("read-user-queries-by-tag");

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		namedParameters.addValue("tag_name", tag);

		return queryList(sql, namedParameters);
	}

	@Override
	public List<UserQuery> getPublishedQueries() {

		String sql = sqlDictionary.getSQLQuery("read-published-user-queries");

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		return queryList(sql, namedParameters);
	}

	@Override
	public List<UserQuery> getTutorialQueries() {

		String sql = sqlDictionary.getSQLQuery("read-tutorial-queries");

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		return queryList(sql, namedParameters);
	}

	
	@Override
	public SetMultimap<Long, String> getQueryTags(Collection<Long> queryIds) {

		String sql = sqlDictionary.getSQLQuery("read-tags-by-user-query-ids");

		SqlParameterSource namedParameters = new MapSqlParameterSource("query_ids", queryIds);

		List<Tag> tags = new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).query(sql, namedParameters, new UserQueryTagRowMapper());

		SetMultimap<Long, String> map = HashMultimap.create();

		for (Tag tag : tags) {

			map.put(tag.getQueryId(), tag.getName());
		}

		return map;
	}

	@Override
	public long createUserQuery(final UserQuery userQuery) {

		final String INSERT_SQL = sqlDictionary.getSQLQuery("create-user-query");

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		namedParameters.addValue("title", userQuery.getTitle());
		namedParameters.addValue("description", userQuery.getDescription());
		namedParameters.addValue("sparql", userQuery.getSparql());
		namedParameters.addValue("published", userQuery.getPublished() ? 'Y' : 'N');
		namedParameters.addValue("owner_id", userQuery.getOwnerId());

		return JdbcTemplateUtils.insertAndGetKey(INSERT_SQL, "query_id", namedParameters, new NamedParameterJdbcTemplate(dsLocator.getUserDataSource())).longValue();
	}

	@Override
	public void createUserQueryTags(final long queryId, final Set<String> tags) {

		final String INSERT_SQL = sqlDictionary.getSQLQuery("create-user-query-tag");

		KeyValuesJdbcBatchUpdater updater = new KeyValuesJdbcBatchUpdater(new JdbcTemplate(dsLocator.getUserDataSource()), queryId) {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {

				ps.setString(1, getValue(i));
				ps.setLong(2, queryId);
			}
		};

		updater.batchUpdate(INSERT_SQL, new ArrayList<String>(tags));
	}

	@Override
	public void updateUserQuery(final UserQuery src) {

		final String UPDATE_SQL = sqlDictionary.getSQLQuery("update-user-query");

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		// key to identify query to update
		namedParameters.addValue("query_id", src.getUserQueryId());

		// values to update
		namedParameters.addValue("title", src.getTitle());
		namedParameters.addValue("description", src.getDescription());
		namedParameters.addValue("sparql", src.getSparql());
		namedParameters.addValue("published", SqlBoolean.toChar(src.getPublished()));

		NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dsLocator.getUserDataSource());

		int affectedRows = jdbcTemplate.update(UPDATE_SQL, namedParameters);

		if (affectedRows != 1) {
			String msg = "oops something wrong occurred" + affectedRows + " rows were affected instead of only 1.";

			Logger.error(msg);
			throw new NextProtException(msg);
		}
	}

	@Override
	public int deleteUserQuery(final long queryId) {

		final String DELETE_SQL = sqlDictionary.getSQLQuery("delete-user-query");

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("query_id", queryId);

		return new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).update(DELETE_SQL, params);
	}

	@Override
	public int deleteUserQueryTags(long queryId, Set<String> tags) {

		final String DELETE_SQL = sqlDictionary.getSQLQuery("delete-user-query-tags");

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("tags", tags);

		return new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).update(DELETE_SQL, params);
	}

	/**
	 * Get user query list and extract tags
	 *
	 * @param sql the select from user_queries sql query
	 * @param source
	 * @return
	 */
	private List<UserQuery> queryList(String sql, SqlParameterSource source) {

		List<UserQuery> userQueryList = new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).query(sql, source, new UserQueryRowMapper());

		if (!userQueryList.isEmpty()) {

			List<Long> queryIds = Lists.transform(userQueryList, UserQuery.EXTRACT_QUERY_ID);

			SetMultimap<Long, String> tags = getQueryTags(queryIds);

			for (UserQuery query : userQueryList) {

				query.setTags(tags.get(query.getUserQueryId()));
			}
		}

		return userQueryList;
	}

	private static class UserQueryRowMapper implements ParameterizedRowMapper<UserQuery> {

		public UserQuery mapRow(ResultSet resultSet, int row) throws SQLException {

			UserQuery query = new UserQuery();
			query.setUserQueryId(resultSet.getInt("query_id"));
			query.setTitle(resultSet.getString("title"));
			query.setDescription(resultSet.getString("description"));
			query.setSparql(resultSet.getString("sparql"));
			query.setPublished(resultSet.getString("published").equals("Y"));
			query.setOwner(resultSet.getString("user_name"));
			return query;
		}
	}

	private static class Tag {

		private String name;
		private long queryId;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public long getQueryId() {
			return queryId;
		}

		public void setQueryId(long queryId) {
			this.queryId = queryId;
		}
	}

	private static class UserQueryTagRowMapper implements ParameterizedRowMapper<Tag> {

		public Tag mapRow(ResultSet resultSet, int row) throws SQLException {

			Tag tag = new Tag();

			tag.setName(resultSet.getString("tag_name"));
			tag.setQueryId(resultSet.getLong("query_id"));

			return tag;
		}
	}
}
