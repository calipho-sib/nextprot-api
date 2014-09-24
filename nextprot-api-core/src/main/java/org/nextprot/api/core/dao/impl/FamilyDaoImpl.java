package org.nextprot.api.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.core.dao.FamilyDao;
import org.nextprot.api.core.domain.Family;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class FamilyDaoImpl implements FamilyDao {

	@Autowired private DataSourceServiceLocator dsLocator;
	
	private final String findFamilies = "select distinct m.identifier_id as master_id, m.unique_name as unique_name, xr.accession as accession, ap.property_value as family_region, cv.cv_name as family_name, a.description as description " +
			"from nextprot.sequence_identifiers m " +
			"inner join nextprot.annotations a on (m.identifier_id = a.identifier_id and a.cv_annotation_type_id = 1059) " + 
			"inner join nextprot.cv_terms cv on (a.cv_term_id = cv.cv_id) " +
			"inner join nextprot.db_xrefs xr on (cv.db_xref_id = xr.resource_id) " +
			"left outer join nextprot.annotation_properties ap on (a.annotation_id = ap.annotation_id and ap.property_name = 'family region') " +
			"where m.cv_type_id = 1 " +
			"and m.cv_status_id = 1 " +
			"and m.unique_name = :uniqueName ";
	
	@Override
	public List<Family> findFamilies(String uniqueName) {
		SqlParameterSource namedParameters = new MapSqlParameterSource("uniqueName", uniqueName);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(findFamilies, namedParameters, new FamilyRowMapper());
	}
	
	static class FamilyRowMapper implements ParameterizedRowMapper<Family> {

		@Override
		public Family mapRow(ResultSet resultSet, int row) throws SQLException {
			Family family = new Family();
			family.setAccession(resultSet.getString("accession"));
			family.setName(resultSet.getString("family_name"));
			family.setDescription(resultSet.getString("description"));
			family.setRegion(resultSet.getString("family_region"));
			
			return family;
		}
		
	}

}
