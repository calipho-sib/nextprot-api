package com.nextprot.api.annotation.builder.statement.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.nextprot.commons.statements.RawStatement;
import org.nextprot.commons.statements.StatementBuilder;
import org.nextprot.commons.statements.StatementField;
import org.springframework.jdbc.core.RowMapper;

public class RawStatementMapper implements RowMapper<RawStatement>
{
	public RawStatement mapRow(ResultSet rs, int rowNum) throws SQLException {
		StatementBuilder sfbuilder = StatementBuilder.createNew();
		for(StatementField key : StatementField.values()){
			String value = rs.getString(key.name());
			sfbuilder.addField(key, value);
		}
		return sfbuilder.build();
	}
	
}