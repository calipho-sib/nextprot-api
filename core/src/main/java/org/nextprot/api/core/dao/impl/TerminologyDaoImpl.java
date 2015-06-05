package org.nextprot.api.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.TerminologyDao;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Terminology;
import org.nextprot.api.core.utils.TerminologyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class TerminologyDaoImpl implements TerminologyDao {

	@Autowired private SQLDictionary sqlDictionary;

	@Autowired private DataSourceServiceLocator dsLocator;

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
			term.setXrefs(TerminologyUtils.convertToXrefs(resultSet.getString("xref")));
			return term;
		}
	}
	
	@Override
	public Terminology findTerminologyByAccession(String accession) {
		SqlParameterSource params = new MapSqlParameterSource("accession", accession);
		List<Terminology> terms=new NamedParameterJdbcTemplate(
				dsLocator.getDataSource()).query(
						sqlDictionary.getSQLQuery("terminology-by-ac"), params, new DbTermRowMapper());
		
		// TODO with Daniel: send appropriate exception if terms.size() > 1 => ambiguous accession
		// TODO normally only database + accession is supposed to be unique !!!!
		if (terms.size()==0)
			return null;			
		return terms.get(0);
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

	
}
