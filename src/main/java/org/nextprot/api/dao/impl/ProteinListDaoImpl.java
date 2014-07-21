package org.nextprot.api.dao.impl;

import static org.nextprot.utils.SQLDictionary.getSQLQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nextprot.api.dao.ProteinListDao;
import org.nextprot.api.domain.ProteinList;
import org.nextprot.auth.core.service.DataSourceServiceLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
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
public class ProteinListDaoImpl implements ProteinListDao {
	
	@Autowired private DataSourceServiceLocator dsLocator;

	@Override
	public List<ProteinList> getProteinListsMetadata(String username) {
		
		SqlParameterSource namedParameters = new MapSqlParameterSource("username", username);
		return new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).query(getSQLQuery("proteinlists-by-username"), namedParameters, new ProteinListRowMapper());
	}
	
	@Override
	public ProteinList getProteinListById(long listId) {
		String sql = "select * from np_users.protein_lists where list_id = :listId";
			
		SqlParameterSource namedParams = new MapSqlParameterSource("listId", listId);
		
		return new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).queryForObject(sql, namedParams, new ProteinListRowMapper());
	}
	
	@Override
	public ProteinList getProteinListByNameForUserIdentifier(String userIdentifier, String listName) {
		String sql = "select l.* " + 
				"from np_users.users u " + 
				"inner join np_users.protein_lists l on u.user_id = l.owner_id " + 
				"where u.identifier = identifier " + 
				"and l.name = :listName";
		
		Map<String, String> namedParams = new HashMap<String, String>();
		namedParams.put("username", userIdentifier);
		namedParams.put("listName", listName);
		return new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).queryForObject(sql, namedParams, new ProteinListRowMapper());
	}
	
	
	@Override
	public ProteinList getProteinListByNameForUser(String username, String listName) {
		String sql = "select l.* " + 
				"from np_users.users u " + 
				"inner join np_users.protein_lists l on u.user_id = l.owner_id " + 
				"where u.username = :username " + 
				"and l.name = :listName";
		
		Map<String, String> namedParams = new HashMap<String, String>();
		namedParams.put("username", username);
		namedParams.put("listName", listName);
		return new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).queryForObject(sql, namedParams, new ProteinListRowMapper());
	}

	@Override
	public Set<String> getAccessionsByListId(Long listId) {
		SqlParameterSource namedParameters = new MapSqlParameterSource("listId", listId);
		List<String> accs = new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).queryForList(getSQLQuery("proteinlists-accessions-by-listid"), namedParameters, String.class);
		return new HashSet<String>(accs);
	}
	
	@Override
	public Long saveProteinList(final ProteinList proteinList) {
		final String INSERT_SQL = getSQLQuery("proteinlist-insert");
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dsLocator.getUserDataSource());
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(
		    new PreparedStatementCreator() {
		        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
		            PreparedStatement ps =
		                connection.prepareStatement(INSERT_SQL, new String[] {"list_id"});
		            ps.setString(1, proteinList.getName());
		            ps.setString(2, proteinList.getDescription());
		            ps.setLong(3, proteinList.getOwnerId());
		            return ps;
		        }
		    },
		    keyHolder);
		
		return keyHolder.getKey().longValue();
	}
	
	@Override
	public void saveProteinListAccessions(final Long listId, final Set<String> accessions) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dsLocator.getUserDataSource());
	
		final int batchSize = 200;
		String sql = "insert into np_users.list_proteins values(?, ?)";
		List<String> accList = new ArrayList<String>(accessions);
		
		for(int j = 0; j < accessions.size(); j += batchSize) {
			final List<String> batchList = accList.subList(j, j + batchSize > accessions.size() ? accessions.size() : j + batchSize);
			
			jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ps.setLong(1, listId);
					ps.setString(2, batchList.get(i));
				}
				
				@Override
				public int getBatchSize() {
					return batchList.size();
				}
			});
		}
	}
	
	@Override
	public int deleteProteinListAccessions(Long listId, Set<String> accessions) {
		final String DELETE_SQL = "delete from np_users.list_proteins where entry in (:accessions)";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("accessions", accessions);
		
		int affectedRows = new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).update(DELETE_SQL, params);
		
		return affectedRows;
	}
	
	@Override
	public Set<Long> getAccessionIds(Set<String> accessions) {
		SqlParameterSource namedParams = new MapSqlParameterSource("accessions", accessions);
		return new HashSet<Long> (new NamedParameterJdbcTemplate(dsLocator.getUserDataSource()).queryForList(getSQLQuery("proteinlists-accid-by-acc"), namedParams, Long.class));
	}

	@Override
	public void updateProteinList(ProteinList proteinList) {
		String sql = "update np_users.protein_lists set name=?, description=? where list_id=?";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dsLocator.getUserDataSource());
		
		jdbcTemplate.update(sql, proteinList.getName(), proteinList.getDescription(), proteinList.getId());
	}

	//TODO: need to add the cascade
	@Override
	public int deleteProteinList(long listId) {
		
		String sql1 = "delete from np_users.list_proteins where list_id = ?";
		String sql2 = "delete from np_users.protein_lists where list_id = ?";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dsLocator.getUserDataSource());
		
		int affectedRows = jdbcTemplate.update(sql1, listId);
		affectedRows += jdbcTemplate.update(sql2, listId);
		return affectedRows;
	}

	
	class ProteinListRowMapper implements ParameterizedRowMapper<ProteinList> {

		@Override
		public ProteinList mapRow(ResultSet rs, int row) throws SQLException {
			ProteinList pl = new ProteinList();
			pl.setId(rs.getLong("list_id"));
			pl.setName(rs.getString("name"));
			pl.setDescription(rs.getString("description"));
			pl.setOwnerId(rs.getLong("owner_id"));
			
			if(rs.getMetaData().getColumnCount() > 4)
				pl.setAccSize(rs.getInt("count"));
			return pl;
		}
		
	}


	

}

