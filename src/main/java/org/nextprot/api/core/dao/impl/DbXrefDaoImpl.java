package org.nextprot.api.core.dao.impl;

import static org.nextprot.api.commons.utils.SQLDictionary.getSQLQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nextprot.api.core.dao.DbXrefDao;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.PublicationDbXref;
import org.nextprot.api.core.domain.DbXref.DbXrefProperty;
import org.nextprot.auth.core.service.DataSourceServiceLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class DbXrefDaoImpl implements DbXrefDao {
	
	@Autowired
	private DataSourceServiceLocator dsLocator;
	
	private final String getAllXrefIds = "select resource_id from nextprot.db_xrefs order by resource_id  ";

	private final String sqlHeader = "select x.resource_id, dbs.cv_name database_name, dbs.url database_url, dbs.link_url database_link, cat.cv_name database_category, x.accession ";
		
	private final String findDbXRefByIds = sqlHeader + " from nextprot.db_xrefs x " + 
			"inner join nextprot.cv_databases dbs on x.cv_database_id = dbs.cv_id " +
			"inner join nextprot.cv_database_categories cat on cat.cv_id = dbs.cv_category_id " +
			"where x.resource_id in (:resourceIds) ";
	
	private final String findDbXRefByPublicationIds = sqlHeader + ", assoc.publication_id " +
			"from nextprot.db_xrefs x " + 
			"inner join nextprot.publication_db_xref_assoc assoc on assoc.db_xref_id = x.resource_id " +
			"inner join nextprot.cv_databases dbs on x.cv_database_id = dbs.cv_id " +
			"inner join nextprot.cv_database_categories cat on cat.cv_id = dbs.cv_category_id " +
			"where assoc.publication_id in (:publicationIds) ";
	
	private final String findPropertiesByResourceIds = "select * " +
			"from nextprot.resource_properties rp " +
			"where rp.resource_id in (:resourceIds);";
	
	@Override
	public List<DbXref> findDbXRefsByPublicationId(Long publicationId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("publicationId", publicationId);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(getSQLQuery("dbxref-publication-by-id"), params, new DbXRefRowMapper());
	};
	
	@Override
	public List<PublicationDbXref> findDbXRefByPublicationIds(List<Long> publicationIds) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("publicationIds", publicationIds);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(findDbXRefByPublicationIds, params, new PublicationDbXRefRowMapper());
	}
	
	@Override
	public List<DbXref> findDbXRefByIds(List<Long> resourceIds) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("resourceIds", resourceIds);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(findDbXRefByIds, params, new DbXRefRowMapper());
	}
	
	
	@Override
	public List<DbXref> findDbXrefsByMaster(String uniqueName) {
		SqlParameterSource namedParams = new MapSqlParameterSource("uniqueName", uniqueName);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(getSQLQuery("dbxref-by-master"), namedParams, new DbXRefRowMapper());
	}
	
	public List<DbXrefProperty> findDbXrefsProperties(List<Long> resourceIds) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("resourceIds", resourceIds);
		
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(findPropertiesByResourceIds, params, new RowMapper<DbXrefProperty>() {

			@Override
			public DbXrefProperty mapRow(ResultSet resultSet, int row) throws SQLException {
				DbXrefProperty prop = new DbXref.DbXrefProperty();
				prop.setDbXrefId(resultSet.getLong("resource_id"));
				prop.setPropertyId(resultSet.getLong("resource_property_id"));
				prop.setName(resultSet.getString("property_name"));
				prop.setValue(resultSet.getString("property_value"));
				return prop;
			}
		});
	}

	@Override
	public List<Long> getAllDbXrefsIds() {
		// warning: doean't work: too many rows, memory error... should simply return a jdbc resultset 
		Map<String, Object> params = new HashMap<String, Object>();
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(getAllXrefIds, params, new RowMapper<Long>() {
			@Override
			public Long mapRow(ResultSet resultSet, int row) throws SQLException {
				return resultSet.getLong("resource_id");
			}
		});
	}
	
	
	private static class DbXRefRowMapper implements ParameterizedRowMapper<DbXref> {

		@Override
		public DbXref mapRow(ResultSet resultSet, int row) throws SQLException {
			DbXref dbXRef = new DbXref();
			
			dbXRef.setDbXrefId(resultSet.getLong("resource_id"));
			String acc = resultSet.getString("accession");
			// quick fix for single error on loading with fuseki:
			// see publication ce5476453bf570846e2baf8a893e33fd with DOI:10.1074/jbc.M414549200... and PubMed:16040616
			if (acc.endsWith("\\|[sect ]\\|")) acc = acc.substring(0,acc.length()-11); 
			dbXRef.setAccession(acc);
			dbXRef.setDatabaseCategory(resultSet.getString("database_category"));
			dbXRef.setDatabaseName(resultSet.getString("database_name"));
			dbXRef.setUrl(resultSet.getString("database_url"));
			dbXRef.setLinkUrl(resultSet.getString("database_link"));
			return dbXRef;
		}
	}
	
	private static class PublicationDbXRefRowMapper implements ParameterizedRowMapper<PublicationDbXref> {

		@Override
		public PublicationDbXref mapRow(ResultSet resultSet, int row) throws SQLException {
			PublicationDbXref dbXRef = new PublicationDbXref();
			
			dbXRef.setDbXrefId(resultSet.getLong("resource_id"));
			dbXRef.setAccession(resultSet.getString("accession"));
			dbXRef.setDatabaseCategory(resultSet.getString("database_category"));
			dbXRef.setDatabaseName(resultSet.getString("database_name"));
			dbXRef.setUrl(resultSet.getString("database_url"));
			dbXRef.setLinkUrl(resultSet.getString("database_link"));
			dbXRef.setPublicationId(resultSet.getLong("publication_id"));
			return dbXRef;
		}
	}

	@Override
	public List<DbXref> findDbXrefByAccession(String accession) {
		SqlParameterSource namedParams = new MapSqlParameterSource("accession", accession);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(getSQLQuery("dbxref-by-accession"), namedParams, new DbXRefRowMapper());
	}


	@Override
	public List<DbXref> findAllDbXrefs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DbXref> findDbXrefByResourceId(Long resourceId) {
		SqlParameterSource namedParams = new MapSqlParameterSource("resourceId", resourceId);
		//System.out.println("DbXrefDaoImpl.findDbXrefByResourceId() resourceId:" + resourceId);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(getSQLQuery("dbxref-by-resource-id"), namedParams, new DbXRefRowMapper());
	}


}
