package org.nextprot.api.rdf.dao.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.rdf.dao.SchemaDao;
import org.nextprot.api.rdf.domain.OWLAnnotation;
import org.nextprot.api.rdf.domain.OWLDatabase;
import org.nextprot.api.rdf.domain.OWLDatasource;
import org.nextprot.api.rdf.domain.OWLEvidence;
import org.nextprot.api.rdf.domain.OWLOntology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SchemaDaoImpl implements SchemaDao {

	@Autowired private DataSourceServiceLocator dsLocator;

	@Autowired
	private SQLDictionary sqlDictionary;


	@Override
	public List<OWLOntology> findAllOntology() {
		SqlParameterSource params = new MapSqlParameterSource();
		List<OWLOntology> ontologies=new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("schema-ontology-list"), params, new SingleColumnRowMapper<OWLOntology>() {

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
	public List<OWLEvidence> findAllEvidence() {
		SqlParameterSource params = new MapSqlParameterSource();
		List<OWLEvidence> evidences=new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("schema-evidence-list"), params, new SingleColumnRowMapper<OWLEvidence> () {

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
		List<OWLDatasource> datasources=new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("schema-datasource-list"), params, new SingleColumnRowMapper<OWLDatasource>() {

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
		List<OWLDatabase> databases=new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("schema-database-list"), params, new SingleColumnRowMapper<OWLDatabase>() {

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
		List<OWLDatabase> databases=new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("schema-provenance-list"), params, new SingleColumnRowMapper<OWLDatabase>() {

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

	// stupid little class used in next method...
	private class NameDescr {
		public String name;
		public String descr;
		public NameDescr(String name, String descr) {
			this.name=name;
			this.descr=descr;
		}
	}

	@Override
	public List<OWLAnnotation> findAllAnnotation() {
		// get description for annotations that exist in db
		AnnotationCategory[] cats = AnnotationCategory.values();
		List<Long> typeIds = new ArrayList<Long>();
		for (AnnotationCategory cat: cats) typeIds.add(new Long(cat.getDbId()));
		SqlParameterSource params = new MapSqlParameterSource("typeIds", typeIds);
		List<NameDescr> nds = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("schema-instantiated-annotation-list"), params, new SingleColumnRowMapper<NameDescr>() {
			@Override
			public NameDescr mapRow(ResultSet rs, int row) throws SQLException {
				NameDescr nd = new NameDescr(rs.getString("cv_name"), rs.getString("description"));
				//System.out.println("rs.cv_name=" + nd.name + " description="+ nd.descr);
				return nd;
			}
		});
		// inject descriptions found in db into the OWLAnnotationCategory enum values
		for (NameDescr nd : nds) {
			AnnotationCategory m = AnnotationCategory.getByDbAnnotationTypeName(nd.name);
			//System.out.println("before descr: " + m.toString());
			m.setDescription(nd.descr);
			//System.out.println("after descr: " + m.toString());
			//AnnotationCategory.getByDbAnnotationTypeName(nd.name).setDescription(nd.descr);
		}
		// encapsulate OWLAnnotationCategory into OWLAnnotation to be compatible with the rest
		List<OWLAnnotation> annotations = new ArrayList<OWLAnnotation>();
		for (AnnotationCategory cat: AnnotationCategory.values()) {
			annotations.add(new OWLAnnotation(cat));
		}
		return annotations;	
	}
	
	
}
