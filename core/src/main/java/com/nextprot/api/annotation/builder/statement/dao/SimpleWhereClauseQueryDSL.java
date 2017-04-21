package com.nextprot.api.annotation.builder.statement.dao;

import org.nextprot.commons.statements.StatementField;

public class SimpleWhereClauseQueryDSL {

	private StatementField whereField;
	private Object value;
	
	public SimpleWhereClauseQueryDSL(StatementField whereField, Object value) {
		this.whereField = whereField;
		this.value = value;
	}

	public StatementField getWhereField() {
		return whereField;
	}

	public Object getValue() {
		return value;
	}


}
