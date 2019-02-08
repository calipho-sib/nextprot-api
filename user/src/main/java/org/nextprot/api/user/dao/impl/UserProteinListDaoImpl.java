package org.nextprot.api.user.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.JdbcUtils;
import org.nextprot.api.commons.utils.KeyValuesJdbcBatchUpdater;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.user.dao.UserProteinListDao;
import org.nextprot.api.user.domain.UserProteinList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Lazy
@Repository
public class UserProteinListDaoImpl implements UserProteinListDao {

	private final Log Logger = LogFactory.getLog(UserProteinListDaoImpl.class);

	@Autowired private SQLDictionary sqlDictionary;

	@Autowired(required = false)
	private DataSourceServiceLocator dsLocator;

	@Override
	public List<UserProteinList> getUserProteinLists(String username) {

		String sql = sqlDictionary.getSQLQuery("read-user-protein-lists-by-username");

		SqlParameterSource namedParameters = new MapSqlParameterSource("user_name", username);

		return new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).query(sql, namedParameters, new ProteinListRowMapper());
	}

	@Override
	public UserProteinList getUserProteinListById(long listId) throws DataAccessException {

		String sql = sqlDictionary.getSQLQuery("read-user-protein-list-by-id");

		SqlParameterSource namedParams = new MapSqlParameterSource("list_id", listId);

		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dsLocator.getUserDataSource());

		UserProteinList userProteinList = template.queryForObject(sql, namedParams, new ProteinListRowMapper());
		userProteinList.setAccessions(getAccessionsByListId(listId));

		return userProteinList;
	}

    @Override
    public UserProteinList getUserProteinListByPublicId(String publicId) throws DataAccessException {

        String sql = sqlDictionary.getSQLQuery("read-user-protein-list-by-pubid");

        SqlParameterSource namedParams = new MapSqlParameterSource("public_id", publicId);

        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dsLocator.getUserDataSource());

        UserProteinList userProteinList = template.queryForObject(sql, namedParams, new ProteinListRowMapper());
        userProteinList.setAccessions(getAccessionsByListId(userProteinList.getId()));

        return userProteinList;
    }

	@Override
	public UserProteinList getUserProteinListByName(String userIdentifier, String listName) throws DataAccessException {

		String sql = sqlDictionary.getSQLQuery("read-user-protein-list-by-username-listname");

		Map<String, String> namedParams = new HashMap<String, String>();

		namedParams.put("user_name", userIdentifier);
		namedParams.put("list_name", listName);

		UserProteinList userProteinList = new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).queryForObject(sql, namedParams, new ProteinListRowMapper());
		userProteinList.setAccessions(getAccessionsByListId(userProteinList.getId()));

		return userProteinList;
	}

	/**
	 * Get the accession numbers that belongs to the list {@code listId}
	 *
	 * @param listId the list identifier
	 * @return a set of proteins
	 */
	@Override
	public Set<String> getAccessionsByListId(long listId) {

		SqlParameterSource namedParameters = new MapSqlParameterSource("list_id", listId);

		List<String> accs = new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).queryForList(sqlDictionary.getSQLQuery("read-protein-list-items-by-listid"), namedParameters, String.class);

		return new HashSet<String>(accs);
	}

	@Override
	public long createUserProteinList(final UserProteinList userProteinList) {

		final String INSERT_SQL = sqlDictionary.getSQLQuery("create-user-protein-list");

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		namedParameters.addValue("list_name", userProteinList.getName());
		namedParameters.addValue("description", userProteinList.getDescription());
		namedParameters.addValue("owner_id", userProteinList.getOwnerId());
        namedParameters.addValue("public_id", userProteinList.getPublicId());

		return JdbcUtils.insertAndGetKey(INSERT_SQL, "list_id", namedParameters,
				new NamedParameterJdbcTemplate(dsLocator.getUserDataSource())).longValue();
	}

	@Override
	public void createUserProteinListItems(final long listId, final Set<String> accessions) {

		final String INSERT_SQL = sqlDictionary.getSQLQuery("create-protein-list-item");

		KeyValuesJdbcBatchUpdater updater = new KeyValuesJdbcBatchUpdater(new JdbcTemplate(dsLocator.getUserDataSource()), listId) {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {

				ps.setString(1, getValue(i));
				ps.setLong(2, listId);
			}
		};

		updater.batchUpdate(INSERT_SQL, new ArrayList<String>(accessions));
	}

	@Override
	public int deleteProteinListItems(long listId, Set<String> accessions) {

		if(!accessions.isEmpty()){
			
			final String DELETE_SQL = sqlDictionary.getSQLQuery("delete-protein_list_items");

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("accession_numbers", accessions);
			params.put("list_id", listId);
			
			return new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).update(DELETE_SQL, params);

		}
		
		return 0;
	}

	@Override
	public int deleteAllProteinListItems(long listId) {

		final String DELETE_SQL = sqlDictionary.getSQLQuery("delete-all-protein-list-items");

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("list_id", listId);
		
		return new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).update(DELETE_SQL, params);
	}
	
	@Override
	public void updateUserProteinListMetadata(UserProteinList src) {

		final String UPDATE_SQL = sqlDictionary.getSQLQuery("update-user-protein-list");

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		// key to identify application to be updated
		namedParameters.addValue("list_id", src.getId());

		// values to update
		namedParameters.addValue("description", src.getDescription());
		namedParameters.addValue("list_name", src.getName());

		NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dsLocator.getUserDataSource());

		int affectedRows = jdbcTemplate.update(UPDATE_SQL, namedParameters);

		if (affectedRows != 1) {
			String msg = "oops something wrong occurred" + affectedRows + " rows were affected instead of only 1.";

			Logger.error(msg);
			throw new NextProtException(msg);
		}
	}

	@Override
	public int deleteUserProteinList(long listId) {
		
		final String DELETE_SQL = sqlDictionary.getSQLQuery("delete-user-protein-list");

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("list_id", listId);

		return new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).update(DELETE_SQL, params);
	}

	class ProteinListRowMapper extends SingleColumnRowMapper<UserProteinList> {

		@Override
		public UserProteinList mapRow(ResultSet rs, int row) throws SQLException {

			UserProteinList pl = new UserProteinList();

			pl.setId(rs.getLong("list_id"));
			pl.setName(rs.getString("list_name"));
			pl.setDescription(rs.getString("description"));
			pl.setOwnerId(rs.getLong("owner_id"));
			pl.setOwner(rs.getString("user_name"));
            pl.setPublicId(rs.getString("public_id"));
			pl.setEntriesCount(rs.getInt("protCount"));

			return pl;
		}
	}
}

