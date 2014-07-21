package org.nextprot.api.dao.impl;

import static org.nextprot.utils.SQLDictionary.getSQLQuery;

import java.util.List;

import org.nextprot.api.dao.MasterIdentifierDao;
import org.nextprot.auth.core.service.DataSourceServiceLocator;
import org.nextprot.utils.SQLDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class MasterIdentifierDaoImpl implements MasterIdentifierDao {

	@Autowired private DataSourceServiceLocator dsLocator;
	
	@Override
	public Long findIdByUniqueName(String uniqueName) {
		SqlParameterSource namedParameters = new MapSqlParameterSource("uniqueName", uniqueName);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).queryForObject(getSQLQuery("master-id-by-name"), namedParameters, Long.class);
	}

	@Override
	public List<String> findUniqueNamesOfChromossome(String chromossome) {
		SqlParameterSource namedParameters = new MapSqlParameterSource("chromossome", chromossome);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).queryForList(SQLDictionary.getSQLQuery("unique-names-of-chromosome"), namedParameters, String.class);
	}
	
	@Override
	public List<String> findUniqueNames() {
		return new JdbcTemplate(dsLocator.getDataSource()).queryForList(SQLDictionary.getSQLQuery("unique-names"), String.class);
	}
	
	@Override
	public List<String> findMasterSequenceUniqueNames() {
		return new JdbcTemplate(dsLocator.getDataSource()).queryForList("select unique_name from nextprot.sequence_identifiers where cv_type_id = 1 order by unique_name", String.class);
	}

}
