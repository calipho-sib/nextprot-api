package org.nextprot.api.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nextprot.api.dao.IsoformDAO;
import org.nextprot.api.domain.Isoform;
import org.nextprot.api.domain.IsoformEntityName;
import org.nextprot.auth.core.service.DataSourceServiceLocator;
import org.nextprot.utils.SQLDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class IsoformDAOImpl implements IsoformDAO {

	@Autowired
	private DataSourceServiceLocator dsLocator;

	@Override
	public List<Isoform> findIsoformsByEntryName(String entryName) {

		String sql = SQLDictionary.getSQLQuery("isoforms-by-entry-name");

		SqlParameterSource namedParameters = new MapSqlParameterSource("unique_name", entryName);
		List<Isoform> isoforms = null;
		isoforms = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sql, namedParameters, new IsoformRowMapper());
		
		if(isoforms.isEmpty()){
			//If nothing is found, remove the condition for the synonym type
			isoforms = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sql.replace("and syn.cv_type_id = 1 ", ""), namedParameters, new IsoformRowMapper());
		}
		
		return isoforms;

	}

	private static class IsoformRowMapper implements ParameterizedRowMapper<Isoform> {

		@Override
		public Isoform mapRow(ResultSet resultSet, int row) throws SQLException {
			Isoform isoform = new Isoform();
			isoform.setUniqueName(resultSet.getString("unique_name"));
			isoform.setSequence(resultSet.getString("bio_sequence"));
			isoform.setMd5(resultSet.getString("md5"));
			isoform.setSwissProtDisplayedIsoform(resultSet.getBoolean("is_swissprot_display"));

			// Set the main entity
			
			String type = resultSet.getString("syn_type");
			
			if (!type.equals("accession code")) {
				IsoformEntityName mainEntity = new IsoformEntityName();
				mainEntity.setQualifier(resultSet.getString("syn_qualifier"));
				mainEntity.setType(resultSet.getString("syn_type"));
				mainEntity.setValue(resultSet.getString("synonym_name"));

				isoform.setMainEntityName(mainEntity);
			}

			return isoform;
		}
	}

	@Override
	public List<IsoformEntityName> findIsoformsSynonymsByEntryName(String entryName) {

		String sql = SQLDictionary.getSQLQuery("isoforms-synonyms-by-entry-name");
		
		SqlParameterSource namedParameters = new MapSqlParameterSource("unique_name", entryName);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sql, namedParameters, new EntityNameRowMapper());

	}

	private static class EntityNameRowMapper implements ParameterizedRowMapper<IsoformEntityName> {

		@Override
		public IsoformEntityName mapRow(ResultSet resultSet, int row) throws SQLException {

			IsoformEntityName entityName = new IsoformEntityName();
			entityName.setQualifier(resultSet.getString("syn_qualifier"));
			entityName.setType(resultSet.getString("syn_type"));
			entityName.setValue(resultSet.getString("synonym_name"));
			entityName.setMainEntityName(resultSet.getString("unique_name"));

			return entityName;
		}
	}

}
