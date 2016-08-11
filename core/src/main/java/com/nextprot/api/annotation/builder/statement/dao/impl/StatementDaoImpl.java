package com.nextprot.api.annotation.builder.statement.dao.impl;

import com.nextprot.api.annotation.builder.statement.dao.StatementDao;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.constants.AnnotationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class StatementDaoImpl implements StatementDao {

	@Autowired
	private SQLDictionary sqlDictionary;
	@Autowired
	private DataSourceServiceLocator dsLocator;

	
	private String getSQL(AnnotationType type, String sqlQueryName){

		String sql = sqlDictionary.getSQLQuery(sqlQueryName);
		if(type.equals(AnnotationType.ENTRY)){
			sql = sql.replace("mapped_statements", "entry_mapped_statements");

		}else if(type.equals(AnnotationType.ISOFORM)){
			sql = sql.replace("mapped_statements", "iso_mapped_statements");
			sql = sql.replace("entry_accession", "isoform_accession");

		}else {
			throw new NextProtException(type + " not supported currently");
		}
		return sql;
	}
	
	@Override
	public List<Statement> findProteoformStatements(AnnotationType type, String nextprotAccession) {

		String sql = getSQL(type, "modified-statements-by-entry-accession");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("accession", nextprotAccession);

		return new NamedParameterJdbcTemplate(dsLocator.getStatementsDataSource()).query(sql, params, new StatementMapper());
	}

	@Override
	public List<Statement> findStatementsByAnnotEntryId(AnnotationType type, String annotHash) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("annot_hash", annotHash);

		return new NamedParameterJdbcTemplate(dsLocator.getStatementsDataSource()).query(sqlDictionary.getSQLQuery("statements-by-annot-entry-id"), params, new StatementMapper());
	}

	@Override
	public List<String> findUniqueGeneNames() {

		return new JdbcTemplate(dsLocator.getStatementsDataSource()).queryForList(sqlDictionary.getSQLQuery("all-gene-names"), String.class);
	}

	@Override
	public List<Statement> findStatementsByAnnotIsoIds(AnnotationType type, List<String> idList) {

		List<Statement> statements = new ArrayList<>();
		if(idList == null || idList.isEmpty()) return statements;
		
		int limit = 1000;

		//Make a distinct list, could use set as well?
		List<String> ids = idList.parallelStream().distinct().collect(Collectors.toList());

		for (int i = 0; i < ids.size(); i += limit) {

			int toLimit = (i + limit > ids.size()) ? ids.size() : i + limit;
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("ids", ids.subList(i, toLimit));

			String sql = getSQL(type, "statements-by-annotation-id");
			
			List<Statement> statementsAux = new NamedParameterJdbcTemplate(dsLocator.getStatementsDataSource()).query(sql, params,
					new StatementMapper());
			statements.addAll(statementsAux);

		}
		return statements;
	}
	

	@Override
	public List<Statement> findNormalStatements(AnnotationType type, String nextprotAccession) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("accession", nextprotAccession);

		String sql = getSQL(type, "statements-by-entry-accession");
		
		return new NamedParameterJdbcTemplate(dsLocator.getStatementsDataSource()).query(sql, params, new StatementMapper());
	}

}
