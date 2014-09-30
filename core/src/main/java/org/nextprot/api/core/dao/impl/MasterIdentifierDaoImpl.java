package org.nextprot.api.core.dao.impl;

import java.util.List;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.MasterIdentifierDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

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
	public List<String> findUniqueNamesOfChromossome(String chromossome) {
		SqlParameterSource namedParameters = new MapSqlParameterSource("chromossome", chromossome);
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
