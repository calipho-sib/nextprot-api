package org.nextprot.api.rdf.dao.impl;

import static org.nextprot.api.commons.utils.SQLDictionary.getSQLQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.rdf.dao.SchemaDao;
import org.nextprot.api.rdf.domain.OWLAnnotation;
import org.nextprot.api.rdf.domain.OWLDatabase;
import org.nextprot.api.rdf.domain.OWLDatasource;
import org.nextprot.api.rdf.domain.OWLEvidence;
import org.nextprot.api.rdf.domain.OWLOntology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class SchemaDaoImpl implements SchemaDao {

	@Autowired private DataSourceServiceLocator dsLocator;


	@Override
	public List<OWLOntology> findAllOntology() {
		SqlParameterSource params = new MapSqlParameterSource();
		List<OWLOntology> ontologies=new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(getSQLQuery("schema-ontology-list"), params, new ParameterizedRowMapper<OWLOntology>() {

			@Override
			public OWLOntology mapRow(ResultSet resultSet, int row) throws SQLException {
				OWLOntology ontology = new OWLOntology();
				
				ontology.setOntology(resultSet.getString("ontology"));
				ontology.setDescription(resultSet.getString("description"));
				ontology.setName(resultSet.getString("name"));
				
				return ontology;
			}
		});
		return ontologies;	
	}


	@Override
	public List<OWLAnnotation> findAllAnnotation() {
		SqlParameterSource params = new MapSqlParameterSource();
		List<OWLAnnotation> annotations=new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(getSQLQuery("schema-annotation-list"), params, new ParameterizedRowMapper<OWLAnnotation>() {

			@Override
			public OWLAnnotation mapRow(ResultSet resultSet, int row) throws SQLException {
				OWLAnnotation annotation = new OWLAnnotation();
				
				annotation.setParent(resultSet.getString("parent"));
				annotation.setDescription(resultSet.getString("description"));
				annotation.setType(resultSet.getString("name"));
				
				return annotation;
			}
		});
		return annotations;	
	}


	@Override
	public List<OWLEvidence> findAllEvidence() {
		SqlParameterSource params = new MapSqlParameterSource();
		List<OWLEvidence> evidences=new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(getSQLQuery("schema-evidence-list"), params, new ParameterizedRowMapper<OWLEvidence>() {

			@Override
			public OWLEvidence mapRow(ResultSet resultSet, int row) throws SQLException {
				OWLEvidence evidence = new OWLEvidence();
				
				evidence.setType(resultSet.getString("type"));
				evidence.setDescription(resultSet.getString("description"));
				evidence.setCount(resultSet.getInt("n"));			
				return evidence;
			}
		});
		return evidences;	
	}


	@Override
	public List<OWLDatasource> findAllSource() {
		SqlParameterSource params = new MapSqlParameterSource();
		List<OWLDatasource> datasources=new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(getSQLQuery("schema-datasource-list"), params, new ParameterizedRowMapper<OWLDatasource>() {

			@Override
			public OWLDatasource mapRow(ResultSet resultSet, int row) throws SQLException {
				OWLDatasource datasource = new OWLDatasource();
				
				datasource.setName(resultSet.getString("name"));
				datasource.setDescription(resultSet.getString("description"));
				datasource.setURL(resultSet.getString("url"));
				return datasource;
			}
		});
		return datasources;			
	}
	
	@Override
	public List<OWLDatabase> findAllDatabase() {
		SqlParameterSource params = new MapSqlParameterSource();
		List<OWLDatabase> databases=new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(getSQLQuery("schema-database-list"), params, new ParameterizedRowMapper<OWLDatabase>() {

			@Override
			public OWLDatabase mapRow(ResultSet resultSet, int row) throws SQLException {
				OWLDatabase database = new OWLDatabase();
				
				database.setName(resultSet.getString("name"));
				database.setDescription(resultSet.getString("description"));
				database.setURL(resultSet.getString("url"));
				database.setCategory(resultSet.getString("category"));
				return database;
			}
		});
		return databases;			
	}
	
	/**
	 * same as findAllDatabase() union identifier type names as pseudo database name 
	 */
	@Override
	public List<OWLDatabase> findAllProvenance() {
		SqlParameterSource params = new MapSqlParameterSource();
		List<OWLDatabase> databases=new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(getSQLQuery("schema-provenance-list"), params, new ParameterizedRowMapper<OWLDatabase>() {

			@Override
			public OWLDatabase mapRow(ResultSet resultSet, int row) throws SQLException {
				OWLDatabase database = new OWLDatabase();
			    database.setName(resultSet.getString("name"));
				database.setDescription(resultSet.getString("description"));
				database.setURL(resultSet.getString("url"));
				database.setCategory(resultSet.getString("category"));
				return database;
			}
		});
		return databases;			
	}
	
}
