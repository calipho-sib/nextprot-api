package org.nextprot.api.core.dao.impl;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.DbXrefDao;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.DbXref.DbXrefProperty;
import org.nextprot.api.core.domain.PublicationDbXref;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class DbXrefDaoImpl implements DbXrefDao {
	
	@Autowired private SQLDictionary sqlDictionary;

	@Autowired
	private DataSourceServiceLocator dsLocator;
	
	@Override
	public List<DbXref> findDbXRefsByPublicationId(Long publicationId) {
		Map<String, Object> params = new HashMap<>();
		params.put("publicationId", publicationId);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("dbxref-publication-by-id"), params, new DbXRefRowMapper());
	};
	
	@Override
	public List<PublicationDbXref> findDbXRefByPublicationIds(List<Long> publicationIds) {
		Map<String, Object> params = new HashMap<>();
		params.put("publicationIds", publicationIds);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("dbxref-by-publication-ids"), params, new PublicationDbXRefRowMapper());
	}

	@Override
	public List<DbXref> findDbXRefByIds(List<Long> resourceIds) {
		Map<String, Object> params = new HashMap<>();
		params.put("resourceIds", resourceIds);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("dbxref-by-resource-ids"), params, new DbXRefRowMapper());
	}
	
	@Override
	public List<DbXref> findDbXrefsByMaster(String uniqueName) {
		SqlParameterSource namedParams = new MapSqlParameterSource("uniqueName", uniqueName);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("dbxref-by-master"), namedParams, new DbXRefRowMapper());
	}

	
	@Override
	public List<DbXref> findDbXrefsAsAnnotByMaster(String uniqueName) {
		SqlParameterSource namedParams = new MapSqlParameterSource("uniqueName", uniqueName);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("dbxref-as-annot-by-master"), namedParams, new DbXRefRowMapper());
	}
	
	// helper function to split a list
	private List<List> splitList(List list) {
		List<List> result = new ArrayList<List>();
		//System.out.println("AAA splitting list of size " + list.size());
		for (int i=0;i<list.size();i+=10000) {
			int maxIndex = Math.min(i+10000, list.size());
			//System.out.println("AAA creating sublist from " + i + " to " + maxIndex);
			result.add(list.subList(i, maxIndex));
		}	
		return result;
	}
	
	
	@Override
	public List<DbXrefProperty> findDbXrefsProperties(List<Long> resourceIds) {
		
		List<DbXrefProperty> result = new ArrayList<DbXrefProperty>();
		// we must split the query into multiple queries otherwise we get an SQL error:
		// the number of parameters (list of resource id) cannot exceed 32767 and miss titin has now 43012 xrefs !
		List<List> paramsList = splitList(resourceIds);
		for (List l: paramsList) {
			Map<String,Object> params = new HashMap();
			params.put("resourceIds", l);
			//System.out.println("AAA sending query with " + l.size() + " elements");
			List<DbXrefProperty> someProps = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("dbxref-props-by-resource-ids"), params, new DbXrefPropertyRowMapper());
			result.addAll(someProps);
		}
		//System.out.println("AAA final size is " + result.size());
		return result;
	}

	@Override
	public List<DbXref.EnsemblInfos> findDbXrefEnsemblInfos(String uniqueName, List<Long> xrefIds) {

		if (!xrefIds.isEmpty()) {

			Map<String, Object> params = new HashMap<>();
			params.put("uniqueName", uniqueName);
			params.put("xrefIds", xrefIds);

			return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("ensembl-props-by-xref-accession"), params, new EnsemblInfosRowMapper());
		}

		return new ArrayList<>();
	}

	@Override
	public List<Long> getAllDbXrefsIds() {
		// warning: doean't work: too many rows, memory error... should simply return a jdbc resultset 
		Map<String, Object> params = new HashMap<>();
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("dbxref-all-ids"), params, new RowMapper<Long>() {
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

	private static class DbXrefPropertyRowMapper implements ParameterizedRowMapper<DbXrefProperty> {

		@Override
		public DbXrefProperty mapRow(ResultSet resultSet, int row) throws SQLException {
			DbXrefProperty prop = new DbXref.DbXrefProperty();
			prop.setDbXrefId(resultSet.getLong("resource_id"));
			prop.setPropertyId(resultSet.getLong("resource_property_id"));
			prop.setName(resultSet.getString("property_name"));
			prop.setValue(resultSet.getString("property_value"));
			return prop;
		}
	}

	private static class EnsemblInfosRowMapper implements ParameterizedRowMapper<DbXref.EnsemblInfos> {

		@Override
		public DbXref.EnsemblInfos mapRow(ResultSet resultSet, int row) throws SQLException {

			return new DbXref.EnsemblInfos(
					resultSet.getLong("db_xref_id"),
					resultSet.getString("gene_ac"),
					resultSet.getLong("gt_link_id"),
					resultSet.getString("protein_ac"),
					resultSet.getLong("tp_link_id"));
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
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("dbxref-by-accession"), namedParams, new DbXRefRowMapper());
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
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("dbxref-by-resource-id"), namedParams, new DbXRefRowMapper());
	}

	// - - - - - - 
	// new stuff
	// - - - - - - 
	
	@Override
	public Set<DbXref> findEntryAnnotationsEvidenceXrefs(String entryName) {
		SqlParameterSource namedParams = new MapSqlParameterSource("uniqueName", entryName);
		List<DbXref> xrefs = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("dbxref-by-master-via-annotation-evidences"), namedParams, new DbXRefRowMapper());
		return new HashSet<>(xrefs);
	}

	@Override
	public Set<DbXref> findEntryIdentifierXrefs(String entryName) {
		SqlParameterSource namedParams = new MapSqlParameterSource("uniqueName", entryName);
		List<DbXref> xrefs = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("dbxref-by-master-via-identifiers"), namedParams, new DbXRefRowMapper());
		return new HashSet<>(xrefs);
	}

	@Override
	public Set<DbXref> findEntryAttachedXrefs(String entryName) {
		SqlParameterSource namedParams = new MapSqlParameterSource("uniqueName", entryName);
		List<DbXref> xrefs = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("dbxref-by-master-via-entry"), namedParams, new DbXRefRowMapper());
		return new HashSet<>(xrefs);
	}

	@Override
	public Set<DbXref> findEntryInteractionXrefs(String entryName) {
		SqlParameterSource namedParams = new MapSqlParameterSource("uniqueName", entryName);
		List<DbXref> xrefs = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("dbxref-by-master-via-interactions"), namedParams, new DbXRefRowMapper());
		return new HashSet<>(xrefs);
	}

	@Override
	public Set<DbXref> findEntryInteractionInteractantsXrefs(String entryName) {
		SqlParameterSource namedParams = new MapSqlParameterSource("uniqueName", entryName);
		List<DbXref> xrefs = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("dbxref-by-master-via-interactions-interactants"), namedParams, new DbXRefRowMapper());
		return new HashSet<>(xrefs);
	}

	
	
	@Override
	public Set<DbXref> findPeptideXrefs(List<String> names) {
		SqlParameterSource namedParams = new MapSqlParameterSource("names", names);
		List<DbXref> xrefs = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("dbxref-by-peptide-names"), namedParams, new DbXRefRowMapper());
		return new HashSet<>(xrefs);
	}

	@Override
	public Set<DbXref> findAntibodyXrefs(List<Long> ids) {
		SqlParameterSource namedParams = new MapSqlParameterSource("ids", ids);
		List<DbXref> xrefs = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("dbxref-by-antibody-ids"), namedParams, new DbXRefRowMapper());
		return new HashSet<>(xrefs);
	}
}
