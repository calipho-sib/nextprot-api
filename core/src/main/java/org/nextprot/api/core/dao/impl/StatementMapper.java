package org.nextprot.api.core.dao.impl;

import org.nextprot.commons.utils.StringUtils;
import org.nextprot.api.core.app.StatementSource;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementBuilder;
import org.nextprot.commons.statements.specs.CoreStatementField;
import org.nextprot.commons.statements.specs.Specifications;
import org.nextprot.commons.statements.specs.StatementField;
import org.nextprot.commons.statements.specs.StatementSpecifications;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class StatementMapper implements RowMapper<Statement> {

	public Statement mapRow(ResultSet rs, int rowNum) throws SQLException {

		String source = rs.getString(CoreStatementField.SOURCE.getName());
	
		// Temporary code for debugging, should not occur because source field value is always set by ETL
		if (! StatementSource.hasKey(source))  System.out.println("ERROR: will fail soonm cos no StatementSource found for " + source + ", at statement_id=" + rs.getString(CoreStatementField.STATEMENT_ID.getName()) );
		
		// Create a statement builder based on the statement source field
		StatementSource src = StatementSource.valueOfKey(source);
		StatementSpecifications specifications = src.getSpecifications();
		// The statement builder is given the specifications defined in the StatementSource for each field
		StatementBuilder stmtBuilder = new StatementBuilder().withSpecifications(specifications);

		// get possibly existing field values stored in extra_fields as json
		String extraValues = rs.getString(Specifications.EXTRA_FIELDS);
		Map<String,String> extraMap = new HashMap<>();
		if (extraValues != null) extraMap = StringUtils.deserializeAsMapOrNull(extraValues);

		// now add both core and custom fields to statement builder
		for (StatementField field : specifications.getFields()) {
			if (field instanceof CoreStatementField) {
				String value = rs.getString(field.getName());
				if (value != null) stmtBuilder.addField(field, value);
			} else {
				String value = extraMap.get(field.getName());
				if (value != null) stmtBuilder.addField(field, value);
			}
		}
		return stmtBuilder.build();
		// debug code
		// Statement stmt = stmtBuilder.build();
		//for (StatementField sf: stmt.keySet()) {
		//	System.out.println("=== statement has " + sf.getClass() + " - " + sf.getName() + " = " + stmt.get(sf));
		//}
		// return stmt;
	}


}