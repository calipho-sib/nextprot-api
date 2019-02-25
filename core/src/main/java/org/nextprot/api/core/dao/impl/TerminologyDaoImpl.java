package org.nextprot.api.core.dao.impl;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.dao.TerminologyDao;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.utils.TerminologyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class TerminologyDaoImpl implements TerminologyDao {

	@Autowired
	private SQLDictionary sqlDictionary;

	@Autowired
	private DataSourceServiceLocator dsLocator;

	@Override
	public List<CvTerm> findTermByAccessionAndTerminology(String accession, String terminology) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	// TODO normally only terminology + accession is supposed to be unique !!!!
	// SHOULD USE findTermByAccessionAndTerminology
	@Cacheable(value = "term-by-accession", sync = true)
	public CvTerm findTerminologyByAccession(String accession) {
		Set<String> acs = new HashSet<>();
		acs.add(accession);
		SqlParameterSource params = new MapSqlParameterSource("accessions", acs);
		List<CvTerm> terms = new NamedParameterJdbcTemplate(
				dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("terminology-by-acs"), params, new DbTermRowMapper());

		if (terms.size() == 0)
			return null;
		else if (terms.size() > 1) {
			throw new NextProtException("Found " + terms.size() + " terms that corresponds to the same accession. Use the method findTerminologyByAccessionForTerminology (accession, terminology) instead");
		}
		return terms.get(0);
	}

	public List<CvTerm> findTermByAccessionAndTerminology(String accession) {
		throw new RuntimeException("Not implemented");
	}


	@Override
	public List<CvTerm> findTerminologyByAccessions(Set<String> accessions) {

		SqlParameterSource params = new MapSqlParameterSource("accessions", accessions);
		List<CvTerm> terms = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("terminology-by-acs"), params, new DbTermRowMapper());
		return terms;
	}


	public List<CvTerm> findTerminologyByOntology(String ontology) {
		SqlParameterSource params = new MapSqlParameterSource("ontology", ontology);
		List<CvTerm> terms = new NamedParameterJdbcTemplate(
				dsLocator.getDataSource()).query(
				sqlDictionary.getSQLQuery("terminology-by-ontology"), params, new DbTermRowMapper());
		return terms;
	}

	@Override
	public List<CvTerm> findAllTerminology() {
		SqlParameterSource params = new MapSqlParameterSource();
		List<CvTerm> terms = new NamedParameterJdbcTemplate(
				dsLocator.getDataSource()).query(
				sqlDictionary.getSQLQuery("terminology-all"), params, new DbTermRowMapper());
		return terms;
	}


	static List<CvTerm.TermAccessionRelation> extractPipeDelimitedRelations (String accession){
		if (accession == null)
			return null;
		else {
			return Arrays.asList(accession.split("\\|")).stream().map(s -> {
				int separatorIndex = s.indexOf("->");
				return new CvTerm.TermAccessionRelation(s.substring(0, separatorIndex), s.substring(separatorIndex + 2, s.length()));
			}).collect(Collectors.toList());
		}
	}



	private static class DbTermRowMapper extends SingleColumnRowMapper<CvTerm> {

		@Override
		public CvTerm mapRow(ResultSet resultSet, int row) throws SQLException {
			List<DbXref> xrefs = null;
			CvTerm term = new CvTerm();
			term.setId(resultSet.getLong("id"));
			term.setAccession(resultSet.getString("accession"));
			xrefs = TerminologyUtils.convertToXrefs(resultSet.getString("selfxref"));
			term.setSelfXref(xrefs==null || xrefs.isEmpty() ? null : xrefs.get(0));
			term.setDescription(resultSet.getString("description"));
			term.setName(resultSet.getString("name"));

			List<CvTerm.TermProperty> props = new ArrayList<>();
			props.addAll(TerminologyUtils.convertToProperties(resultSet.getString("properties"), term.getId(), term.getAccession()));
			props.addAll(TerminologyUtils.convertToProperties(resultSet.getString("abbreviations"), term.getId(), term.getAccession()));
			if (! props.isEmpty()) term.setProperties(props);
		
			term.setOntology(resultSet.getString("ontology"));
			term.setOntologyAltname(resultSet.getString("ontologyAltname"));
			term.setOntologyDisplayName(resultSet.getString("ontologyDisplayName"));
			term.setAncestorsRelations(extractPipeDelimitedRelations(resultSet.getString("ancestor")));
			term.setChildrenRelations(extractPipeDelimitedRelations(resultSet.getString("children")));
			term.setXrefs(TerminologyUtils.convertToXrefs(resultSet.getString("xref")));

			term.setSynonyms(TerminologyUtils.filterSynonyms(term.getOntology(), term.getName(), term.getDescription(), resultSet.getString("synonyms")));

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
		return new JdbcTemplate(dsLocator.getDataSource()).queryForList(sqlDictionary.getSQLQuery("terminology-names"), String.class).stream()
				.map(StringUtils::camelToKebabCase)
				.collect(Collectors.toList());
	}
}