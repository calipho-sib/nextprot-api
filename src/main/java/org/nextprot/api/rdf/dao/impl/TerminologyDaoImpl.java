package org.nextprot.api.rdf.dao.impl;

import static org.nextprot.api.commons.utils.SQLDictionary.getSQLQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nextprot.api.rdf.dao.TerminologyDao;
import org.nextprot.api.rdf.domain.Terminology;
import org.nextprot.auth.core.service.DataSourceServiceLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class TerminologyDaoImpl implements TerminologyDao {

	@Autowired private DataSourceServiceLocator dsLocator;

	@Override
	public Terminology findTerminologyByAccession(String accession) {
		SqlParameterSource params = new MapSqlParameterSource("accession", accession);
		List<Terminology> terms=new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(getSQLQuery("terminology-by-ac"), params, new ParameterizedRowMapper<Terminology>() {

			@Override
			public Terminology mapRow(ResultSet resultSet, int row) throws SQLException {
				Terminology term = new Terminology();
				
				term.setAccession(resultSet.getString("accession"));
				term.setDescription(resultSet.getString("description"));
				term.setName(resultSet.getString("name"));
				term.setOntology(resultSet.getString("ontology"));
				term.setAncestorAccession(resultSet.getString("ancestor"));
				term.setSameAs(resultSet.getString("sameas"));
				
				return term;
			}
		});
		
		//
		// TODO check that terms count is only 1 item
		if (terms.size()==0)
			return null;
			
		return terms.get(0);
	}

	@Override
	public List<Terminology> findTerminologByTitle(String title) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public List<Terminology> findTerminologyByName(String name) {
		throw new RuntimeException("Not implemented");
	}

	public List<Terminology> findTerminologyByOntology(String ontology) {
		SqlParameterSource params = new MapSqlParameterSource("ontology", ontology);
		List<Terminology> terms=new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(getSQLQuery("terminology-by-ontology"), params, new ParameterizedRowMapper<Terminology>() {

			@Override
			public Terminology mapRow(ResultSet resultSet, int row) throws SQLException {
				Terminology term = new Terminology();
				
				term.setAccession(resultSet.getString("accession"));
				term.setDescription(resultSet.getString("description"));
				term.setName(resultSet.getString("name"));
				term.setOntology(resultSet.getString("ontology"));
				term.setAncestorAccession(resultSet.getString("ancestor"));
				term.setSameAs(resultSet.getString("sameas"));
				
				return term;
			}
		});
		return terms;
	}

	@Override
	public List<Terminology> findAllTerminology() {
		SqlParameterSource params = new MapSqlParameterSource();
		List<Terminology> terms=new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(getSQLQuery("terminology-all"), params, new ParameterizedRowMapper<Terminology>() {

			@Override
			public Terminology mapRow(ResultSet resultSet, int row) throws SQLException {
				Terminology term = new Terminology();
				
				term.setAccession(resultSet.getString("accession"));
				term.setDescription(resultSet.getString("description"));
				term.setName(resultSet.getString("name"));
				term.setOntology(resultSet.getString("ontology"));
				term.setAncestorAccession(resultSet.getString("ancestor"));
				term.setSameAs(resultSet.getString("sameas"));
				
				return term;
			}
		});
		return terms;
	}
	
}
