package com.nextprot.api.annotation.builder.statement.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.commons.statements.RawStatement;
import org.nextprot.commons.statements.StatementField;
import org.nextprot.commons.statements.StatementBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nextprot.api.annotation.builder.statement.dao.RawStatementDao;

@SuppressWarnings("unchecked")
@Repository
public class RawStatementDaoImpl implements RawStatementDao {

	private static class RawStatementMapper implements RowMapper<RawStatement>
	{
		public RawStatement mapRow(ResultSet rs, int rowNum) throws SQLException {
			StatementBuilder sfbuilder = StatementBuilder.createNew();
			for(StatementField key : StatementField.values()){
				String value = rs.getString(key.name());
				sfbuilder.addStatementField(key, value);
			}
			return sfbuilder.build();
		}
		
	}
	
	@Autowired
	private SQLDictionary sqlDictionary;
	@Autowired
	private DataSourceServiceLocator dsLocator;

	@Override
	@Cacheable("raw-statements")
	public List<RawStatement> findPhenotypeRawStatements(String entryName) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("source", "bioeditor");
		params.put("entry_accession", entryName);

		return new NamedParameterJdbcTemplate(dsLocator.getStatementsDataSource())
				.query("select * from mapped_statements where annotation_category = 'phenotype' and entry_accession = :entry_accession", params, 
						new RawStatementMapper());
	}

	@Override
	@Cacheable("raw-statements-by-annot-hash")
	public List<RawStatement> findRawStatementsByAnnotHash(String annotHash) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("annot_hash", annotHash);
		return new NamedParameterJdbcTemplate(dsLocator.getStatementsDataSource()).
				query("select * from mapped_statements ms where ms.annot_hash = :annot_hash", params, new RawStatementMapper());
	}

	@Override
	@Cacheable("normal-statements-by-entry-name")
	public List<RawStatement> findNormalRawStatements(String entryName) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("entry_accession", entryName);

		// Add entry
		return new NamedParameterJdbcTemplate(dsLocator.getStatementsDataSource())
				.query("select * from mapped_statements ms where annotation_category != 'phenotype' and entry_accession = :entry_accession", params, new RawStatementMapper());
	}

}
