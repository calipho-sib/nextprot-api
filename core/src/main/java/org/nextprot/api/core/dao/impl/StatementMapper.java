package org.nextprot.api.core.dao.impl;

import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementBuilder;
import org.nextprot.commons.statements.StatementField;
import org.nextprot.commons.statements.schema.NXFlatTableSchema;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StatementMapper implements RowMapper<Statement> {
	public Statement mapRow(ResultSet rs, int rowNum) throws SQLException {

		NXFlatTableSchema schema = NXFlatTableSchema.fromResultSetMetaData(rs.getMetaData());

		StatementBuilder sfbuilder = StatementBuilder.createNew()
				.withSchema(schema);

		for(StatementField key : schema.getFields()) {

			String value = rs.getString(key.getName());
			if (value != null) {
				sfbuilder.addField(key, value);
			}
		}
		return sfbuilder.generateHashAndBuild();
	}
	
}