package org.nextprot.api.core.dao.impl;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.BioPhyChemPropsDao;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
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
public class BioPhyChemPropsDaoImpl implements BioPhyChemPropsDao {

	@Autowired private SQLDictionary sqlDictionary;

	@Autowired private DataSourceServiceLocator dsLocator;
	
	@Override
	public List<AnnotationProperty> findPropertiesByUniqueName(String uniqueName) {
		SqlParameterSource namedParams = new MapSqlParameterSource("uniqueName", uniqueName);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("biophychem-by-entry"), namedParams, new AnnotationIsoformRowMapper());
	}

	private static class AnnotationIsoformRowMapper extends SingleColumnRowMapper<AnnotationProperty> {

		@Override
		public AnnotationProperty mapRow(ResultSet resultSet, int row) throws SQLException {

			AnnotationProperty property = new AnnotationProperty();

			property.setName(resultSet.getString("display_name"));
			property.setValue(resultSet.getString("property_value"));
			property.setAnnotationId(Long.parseLong(resultSet.getString("identifier_property_id")));

			return property;
		}
	}
}
