package com.nextprot.api.annotation.builder.statement.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.commons.statements.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nextprot.api.annotation.builder.statement.dao.StatementDao;

@Repository
public class StatementDaoImpl implements StatementDao {

	@Autowired
	private SQLDictionary sqlDictionary;
	@Autowired
	private DataSourceServiceLocator dsLocator;

	
	private String getSQL(String sqlQueryName){
		String sql = sqlDictionary.getSQLQuery(sqlQueryName);
		return sql.replace("mapped_statements", "iso_mapped_statements");
	}
	
	@Override
	public List<Statement> findPhenotypeStatements(String nextprotAccession) {

		String sql = getSQL("modified-statements-by-entry-accession");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("accession", nextprotAccession);

		return new NamedParameterJdbcTemplate(dsLocator.getStatementsDataSource()).query(sql, params, new StatementMapper());
	}

	@Override
	public List<Statement> findStatementsByAnnotEntryId(String annotHash) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("annot_hash", annotHash);

		return new NamedParameterJdbcTemplate(dsLocator.getStatementsDataSource()).query(sqlDictionary.getSQLQuery("statements-by-annot-entry-id"), params, new StatementMapper());
	}

	@Override
	public List<Statement> findStatementsByAnnotIsoIds(List<String> idList) {

		List<Statement> statements = new ArrayList<>();
		if(idList == null || idList.isEmpty()) return statements;
		
		int limit = 1000;

		//Make a distinct list, could use set as well?
		List<String> ids = idList.parallelStream().distinct().collect(Collectors.toList());

		for (int i = 0; i < ids.size(); i += limit) {

			int toLimit = (i + limit > ids.size()) ? ids.size() : i + limit;
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("ids", ids.subList(i, toLimit));

			String sql = getSQL("statements-by-annotation-id");
			
			List<Statement> statementsAux = new NamedParameterJdbcTemplate(dsLocator.getStatementsDataSource()).query(sql, params,
					new StatementMapper());
			statements.addAll(statementsAux);

		}
		return statements;
	}
	

	@Override
	public List<Statement> findNormalStatements(String nextprotAccession) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("accession", nextprotAccession);

		String sql = getSQL("statements-by-entry-accession");
		
		return new NamedParameterJdbcTemplate(dsLocator.getStatementsDataSource()).query(sql, params, new StatementMapper());
	}

}
