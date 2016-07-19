package com.nextprot.api.annotation.builder.statement.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.commons.statements.RawStatement;
import org.springframework.beans.factory.annotation.Autowired;
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
	public List<RawStatement> findPhenotypeRawStatements(String nextprotAccession) {

		String sql = sqlDictionary.getSQLQuery("modified-statements-by-entry-accession");
		if (nextprotAccession.contains("-")) {
			sql = sql.replace("ms.entry_accession", "ms.isoform_accession");
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("accession", nextprotAccession);

		return new NamedParameterJdbcTemplate(dsLocator.getStatementsDataSource()).query(sql, params, new RawStatementMapper());
	}

	@Override
	public List<RawStatement> findRawStatementsByAnnotEntryId(String annotHash) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("annot_hash", annotHash);

		return new NamedParameterJdbcTemplate(dsLocator.getStatementsDataSource()).query(sqlDictionary.getSQLQuery("statements-by-annot-entry-id"), params, new RawStatementMapper());
	}

	@Override
	public List<RawStatement> findRawStatementsByAnnotIsoIds(List<String> idList) {

		List<RawStatement> statements = new ArrayList<>();
		if(idList == null || idList.isEmpty()) return statements;
		
		int limit = 1000;

		//Make a distinct list, could use set as well?
		List<String> ids = idList.parallelStream().distinct().collect(Collectors.toList());

		for (int i = 0; i < ids.size(); i += limit) {

			int toLimit = (i + limit > ids.size()) ? ids.size() : i + limit;
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("ids", ids.subList(i, toLimit));

			List<RawStatement> statementsAux = new NamedParameterJdbcTemplate(dsLocator.getStatementsDataSource()).query(sqlDictionary.getSQLQuery("statements-by-annot-iso-id"), params,
					new RawStatementMapper());
			statements.addAll(statementsAux);

		}
		return statements;
	}
	

	@Override
	public List<RawStatement> findNormalRawStatements(String nextprotAccession) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("accession", nextprotAccession);

		String sql = sqlDictionary.getSQLQuery("entry-statements-by-entry-accession");
		if (nextprotAccession.contains("-")) {
			sql = sql.replace("ms.entry_accession", "ms.isoform_accession");
		}

		return new NamedParameterJdbcTemplate(dsLocator.getStatementsDataSource()).query(sql, params, new RawStatementMapper());
	}

}
