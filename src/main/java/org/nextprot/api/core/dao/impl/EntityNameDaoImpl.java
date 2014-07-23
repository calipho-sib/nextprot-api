package org.nextprot.api.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.core.dao.EntityNameDao;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.domain.Overview.EntityName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class EntityNameDaoImpl implements EntityNameDao {
	@Autowired private DataSourceServiceLocator dsLocator;
	
	private final String findNames = "select * " +
			"from nextprot.view_master_identifier_names " +
			"where unique_name = :uniqueName";
	
	@Override
	public List<EntityName> findNames(String uniqueName) {
		SqlParameterSource namedParameters = new MapSqlParameterSource("uniqueName", uniqueName);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(findNames, namedParameters, new EntryNameRowMapper());
	}
	
	private static class EntryNameRowMapper implements ParameterizedRowMapper<EntityName> {

		@Override
		public EntityName mapRow(ResultSet resultSet, int row) throws SQLException {
			EntityName entryName = new Overview.EntityName();
			entryName.setClazz(Overview.EntityNameClass.getValue(resultSet.getString("name_class")));
			entryName.setType(resultSet.getString("name_type"));
			entryName.setQualifier(resultSet.getString("name_qualifier"));
			entryName.setMain(resultSet.getBoolean("is_main"));
			entryName.setValue(resultSet.getString("synonym_name"));
			entryName.setSynonymId(resultSet.getString("synonym_id"));
			entryName.setParentId(resultSet.getString("parent_id"));
			return entryName;
		}
		
	}

}
