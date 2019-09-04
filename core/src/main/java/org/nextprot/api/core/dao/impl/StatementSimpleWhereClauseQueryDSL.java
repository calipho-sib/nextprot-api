package org.nextprot.api.core.dao.impl;

import org.nextprot.commons.statements.specs.StatementField;

public class StatementSimpleWhereClauseQueryDSL {

	private StatementField whereField;
	private Object value;
	
	public StatementSimpleWhereClauseQueryDSL(StatementField whereField, Object value) {
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
