package com.nextprot.api.annotation.builder.statement.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.commons.statements.RawStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nextprot.api.annotation.builder.statement.dao.RawStatementDao;

@Repository
public class RawStatementDaoImpl implements RawStatementDao {

	@Autowired
	private SQLDictionary sqlDictionary;
	@Autowired
	private DataSourceServiceLocator dsLocator;

	@Override
	@Cacheable("modified-entry-statements-by-entry-accession")
	public List<RawStatement> findPhenotypeRawStatements(String entryName) {
	
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("entry_accession", entryName);

		return new NamedParameterJdbcTemplate(dsLocator.getStatementsDataSource())
				.query(sqlDictionary.getSQLQuery("modified-entry-statements-by-entry-accession"), 
					params, new RawStatementMapper());
	}

	@Override
	@Cacheable("statements-by-annot-hash")
	public List<RawStatement> findRawStatementsByAnnotHash(String annotHash) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("annot_hash", annotHash);
		
		return new NamedParameterJdbcTemplate(dsLocator.getStatementsDataSource())
				.query(sqlDictionary.getSQLQuery("statements-by-annot-hash"), 
						params, new RawStatementMapper());
	}

	@Override
	@Cacheable("entry-statements-by-entry-accession")
	public List<RawStatement> findNormalRawStatements(String entryName) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("entry_accession", entryName);

		return new NamedParameterJdbcTemplate(dsLocator.getStatementsDataSource())
				.query(sqlDictionary.getSQLQuery("entry-statements-by-entry-accession"), 
						params, new RawStatementMapper());
	}

}
