package org.nextprot.api.core.dao.impl;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.StatementDao;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.specs.StatementField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class StatementDaoImpl implements StatementDao {

	@Autowired
	private SQLDictionary sqlDictionary;
	@Autowired
	private DataSourceServiceLocator dsLocator;
	

	private String getSQL(String sqlQueryName){
		return sqlDictionary.getSQLQuery(sqlQueryName);
	}
	
	@Override
	public List<Statement> findProteoformStatements(String nextprotAccession) {

		String sql = getSQL("modified-statements-by-entry-accession");
		
		Map<String, Object> params = new HashMap<>();
		params.put("accession", nextprotAccession);

		return new NamedParameterJdbcTemplate(dsLocator.getStatementsDataSource()).query(sql, params, new StatementMapper());
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
		Map<String, Object> params = new HashMap<>();
		params.put("accession", nextprotAccession);

		String sql = getSQL("statements-by-entry-accession");
		return new NamedParameterJdbcTemplate(dsLocator.getStatementsDataSource()).query(sql, params, new StatementMapper());
	}

	@Override
	public List<String> findAllDistinctValuesforField(StatementField field) {
		String sql = "select distinct " + field.getName() + " from nxflat.entry_mapped_statements";
		return new JdbcTemplate(dsLocator.getStatementsDataSource()).queryForList(sql, String.class);
	}

	@Override
	public List<String> findDistinctExtraFieldsTerms(String termField) {
		String sql = "SELECT DISTINCT extra_fields FROM nxflat.entry_mapped_statements WHERE extra_fields LIKE '%" + termField + "%'";
		return new JdbcTemplate(dsLocator.getStatementsDataSource()).queryForList(sql, String.class);
	}
	
	@Override
	public List<String> findAllDistinctValuesforFieldWhereFieldEqualsValues(StatementField field, StatementSimpleWhereClauseQueryDSL... conditions) {

		String sql = "select distinct " + field.getName() + " from nxflat.entry_mapped_statements where ";
		for(int i=0; i<conditions.length; i++){
			String whereField = conditions[i].getWhereField().getName();
			Object value = conditions[i].getValue();
			if(value.getClass().equals(String.class)){
				sql += whereField + " = '" + value + "' ";
			}else {
				sql += whereField + " = " + value + " ";
			}
			if(i+1 < conditions.length){
				sql += "AND ";
			}
		}
		
		return new JdbcTemplate(dsLocator.getStatementsDataSource()).queryForList(sql, String.class);
	}

}
