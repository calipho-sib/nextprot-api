package org.nextprot.api.commons.dao.impl;

import org.nextprot.api.commons.dao.MasterIdentifierDao;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MasterIdentifierDaoImpl implements MasterIdentifierDao {

	@Autowired private SQLDictionary sqlDictionary;

	@Autowired private DataSourceServiceLocator dsLocator;
	
	@Override
	public Long findIdByUniqueName(String uniqueName) {
		SqlParameterSource namedParameters = new MapSqlParameterSource("uniqueName", uniqueName);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).queryForObject(sqlDictionary.getSQLQuery("master-id-by-name"), namedParameters, Long.class);
	}
	
	@Override
	public List<String> findUniqueNamesByGeneName(String geneName) {
		SqlParameterSource namedParameters = new MapSqlParameterSource("geneName", geneName);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).queryForList(sqlDictionary.getSQLQuery("accessions-by-gene-name"), namedParameters, String.class);
	}

	@Override
	public List<String> findUniqueNamesOfChromosome(String chromosome) {
		SqlParameterSource namedParameters = new MapSqlParameterSource("chromosome", chromosome);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).queryForList(sqlDictionary.getSQLQuery("unique-names-of-chromosome"), namedParameters, String.class);
	}
	
	@Override
	public List<String> findUniqueNames() {
		return new JdbcTemplate(dsLocator.getDataSource()).queryForList(sqlDictionary.getSQLQuery("unique-names"), String.class);
	}
	
	@Override
	public List<String> findMasterSequenceUniqueNames() {
		return new JdbcTemplate(dsLocator.getDataSource()).queryForList("select unique_name from nextprot.sequence_identifiers where cv_type_id = 1 order by unique_name", String.class);
	}

}
