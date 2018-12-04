package org.nextprot.api.core.dao.impl;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.EntityNameDao;
import org.nextprot.api.core.domain.EntityName;
import org.nextprot.api.core.domain.Overview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class EntityNameDaoImpl implements EntityNameDao {

	@Autowired	private SQLDictionary sqlDictionary;
	@Autowired	private DataSourceServiceLocator dsLocator;

	@Override
	public List<EntityName> findNames(String uniqueName) {
		SqlParameterSource namedParameters = new MapSqlParameterSource("uniqueName", uniqueName);
		 List<EntityName> entityNames = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("entity-names"), namedParameters, new EntryNameRowMapper());
		 return entityNames;
	}

	private static class EntryNameRowMapper extends SingleColumnRowMapper<EntityName> {

		@Override
		public EntityName mapRow(ResultSet resultSet, int row) throws SQLException {
			EntityName entryName = new EntityName();
			entryName.setClazz(Overview.EntityNameClass.getValue(resultSet.getString("name_class")));
			entryName.setCategory(resultSet.getString("category"));
			entryName.setType(resultSet.getString("name_type"));
			entryName.setQualifier(resultSet.getString("name_qualifier"));
			entryName.setMain(resultSet.getBoolean("is_main"));
			entryName.setName(resultSet.getString("synonym_name"));
			entryName.setId(resultSet.getString("synonym_id"));
			entryName.setParentId(resultSet.getString("parent_id"));
			return entryName;
		}

	}


	
	@Override
	public List<EntityName> findAlternativeChainNames(String uniqueName) {
		SqlParameterSource namedParameters = new MapSqlParameterSource("uniqueName", uniqueName);
		 List<EntityName> entityNames = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("alternative-chain-names"), namedParameters, new EntryNameRowMapper());
		 return entityNames;
	}

}
