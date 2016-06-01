package com.nextprot.api.annotation.builder.statement.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.commons.statements.RawStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nextprot.api.annotation.builder.statement.dao.RawStatementDao;

@Repository
public class RawStatementDaoImpl implements RawStatementDao {

	@Autowired private SQLDictionary sqlDictionary;
	@Autowired private DataSourceServiceLocator dsLocator;
	
	@Override
	public List<RawStatement> findRawStatements() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("source", "bioeditor");
		return new NamedParameterJdbcTemplate(dsLocator.getStatementsDataSource()).query("select annotation_category from mapped_statements where source = :source", params, new BeanPropertyRowMapper(RawStatement.class));
	}

}
