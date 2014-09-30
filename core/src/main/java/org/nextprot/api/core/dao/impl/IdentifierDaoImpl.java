package org.nextprot.api.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.core.dao.IdentifierDao;
import org.nextprot.api.core.domain.Identifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class IdentifierDaoImpl implements IdentifierDao {
	
	@Autowired private DataSourceServiceLocator dsLocator;
	
	private final String findIdentifiersByMaster = "select * " +
			"from nextprot.view_master_identifier_identifiers vmii " +
			"where vmii.unique_name = :uniqueName " +
			"order by vmii.type";
	
	@Override
	public List<Identifier> findIdentifiersByMaster(String uniqueName) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uniqueName", uniqueName);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(findIdentifiersByMaster, params, new IdentifierRowMapper());
	}
	
	private static class IdentifierRowMapper implements ParameterizedRowMapper<Identifier> {

		@Override
		public Identifier mapRow(ResultSet resultSet, int row) throws SQLException {
			Identifier identifier = new Identifier();
			identifier.setName(resultSet.getString("identifier_name"));
			identifier.setType(resultSet.getString("type"));
			identifier.setDatabase(resultSet.getString("db_name"));
			//identifier.setId(resultSet.getString("identifier_id"));
			//identifier.setSynonymId(resultSet.getLong("syn_id"));
			//identifier.setXrefId(resultSet.getLong("xref_id"));
			return identifier;
		}
		
	}

}
