package org.nextprot.api.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.TerminologyDao;
import org.nextprot.api.core.domain.Terminology;
import org.nextprot.api.core.utils.TerminologyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class TerminologyDaoImpl implements TerminologyDao {

	@Autowired private SQLDictionary sqlDictionary;

	@Autowired private DataSourceServiceLocator dsLocator;
	
	@Override
	public List<Terminology> findTermByAccessionAndTerminology(String accession, String terminology) {
		throw new RuntimeException("Not implemented");
	}


	
	@Override
	// TODO with Daniel: send appropriate exception if terms.size() > 1 => ambiguous accession
	// TODO normally only terminology + accession is supposed to be unique !!!!
	// SHOULD USE findTermByAccessionAndTerminology
	public Terminology findTerminologyByAccession(String accession) {
		Set<String> acs = new HashSet<String>();
		acs.add(accession);
		SqlParameterSource params = new MapSqlParameterSource("accessions", acs);
		List<Terminology> terms = new NamedParameterJdbcTemplate(
				dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("terminology-by-acs"), params, new DbTermRowMapper());
		
		if (terms.size()==0)
			return null;			
		return terms.get(0);
	}
	
	public List<Terminology> findTermByAccessionAndTerminology(String accession) {
		throw new RuntimeException("Not implemented");
	}

	
	@Override
	public List<Terminology> findTerminologyByAccessions(Set<String> accessions) {
		
		SqlParameterSource params = new MapSqlParameterSource("accessions", accessions);
		List<Terminology> terms = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("terminology-by-acs"), params, new DbTermRowMapper());
		return terms;
	}


	public List<Terminology> findTerminologyByOntology(String ontology) {
		SqlParameterSource params = new MapSqlParameterSource("ontology", ontology);
		List<Terminology> terms=new NamedParameterJdbcTemplate(
				dsLocator.getDataSource()).query(
						sqlDictionary.getSQLQuery("terminology-by-ontology"), params, new DbTermRowMapper());
		return terms;
	}

	@Override
	public List<Terminology> findAllTerminology() {
		SqlParameterSource params = new MapSqlParameterSource();
		List<Terminology> terms=new NamedParameterJdbcTemplate(
				dsLocator.getDataSource()).query(
						sqlDictionary.getSQLQuery("terminology-all"), params, new DbTermRowMapper());
		return terms;
	}
	
	
	@Override
	public List<Terminology> findTerminologByTitle(String title) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public List<Terminology> findTerminologyByName(String name) {
		throw new RuntimeException("Not implemented");
	}

	private static class DbTermRowMapper implements ParameterizedRowMapper<Terminology> {

		@Override
		public Terminology mapRow(ResultSet resultSet, int row) throws SQLException {
			Terminology term = new Terminology();
			term.setId(resultSet.getLong("id"));
			term.setAccession(resultSet.getString("accession"));
			term.setDescription(resultSet.getString("description"));
			term.setName(resultSet.getString("name"));
			term.setSynonyms(resultSet.getString("synonyms"));
			term.setProperties(TerminologyUtils.convertToProperties(resultSet.getString("properties"), term.getId(), term.getAccession()));
			term.setOntology(resultSet.getString("ontology"));
			term.setAncestorAccession(resultSet.getString("ancestor"));
			term.setChildAccession(resultSet.getString("children"));
			term.setXrefs(TerminologyUtils.convertToXrefs(resultSet.getString("xref")));
			return term;
		}
	}

	@Override
	public List<String> findEnzymeAcsByMaster(String entryName) {
		SqlParameterSource params = new MapSqlParameterSource("uniqueName", entryName);
		List<String> accessions = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).queryForList(sqlDictionary.getSQLQuery("enzyme-by-entry-name"), params, String.class);
		return accessions;
	}

	@Override
	public List<String> findTerminologyNamesList() {
		return  new JdbcTemplate(dsLocator.getDataSource()).queryForList(sqlDictionary.getSQLQuery("terminology-names"), String.class);
	}


}
