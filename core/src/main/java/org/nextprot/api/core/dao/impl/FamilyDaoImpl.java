package org.nextprot.api.core.dao.impl;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.FamilyDao;
import org.nextprot.api.core.domain.Family;
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
public class FamilyDaoImpl implements FamilyDao {

	@Autowired private DataSourceServiceLocator dsLocator;
	@Autowired private SQLDictionary sqlDictionary;
	
	@Override
	public List<Family> findFamilies(String uniqueName) {
		String sql = sqlDictionary.getSQLQuery("families-by-entry");	
		SqlParameterSource namedParameters = new MapSqlParameterSource("uniqueName", uniqueName);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sql, namedParameters, new FamilyRowMapper());
	}
	
	@Override
	public Family findParentOfFamilyId(Long familyId) {
		String sql = sqlDictionary.getSQLQuery("parent-family-by-term-id");	
		SqlParameterSource namedParameters = new MapSqlParameterSource("familyId", familyId);
		List<Family> parents = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sql, namedParameters, new FamilyRowMapper());
		if (parents==null || parents.size()==0) return null;
		return parents.get(0);
	}

	
	static class FamilyRowMapper extends SingleColumnRowMapper<Family> {

		@Override
		public Family mapRow(ResultSet resultSet, int row) throws SQLException {
			Family family = new Family();
			family.setFamilyId(resultSet.getLong("family_id"));
			family.setAccession(resultSet.getString("accession"));
			family.setName(resultSet.getString("family_name"));
			family.setDescription(resultSet.getString("description"));
			family.setRegion(resultSet.getString("family_region"));
			return family;
		}
		
	}

}
