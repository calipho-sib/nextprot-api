package org.nextprot.api.core.dao.impl;

import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementBuilder;
import org.nextprot.commons.statements.specs.Specifications;
import org.nextprot.commons.statements.specs.StatementField;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class StatementMapper implements RowMapper<Statement> {

	public Statement mapRow(ResultSet rs, int rowNum) throws SQLException {

		Specifications.Builder builder = new Specifications.Builder();

		int index = findColumnIndex(rs.getMetaData(), Specifications.EXTRA_FIELDS);

		if (index > 0) {
			builder.withExtraFieldsValue(rs.getString(index));
		}

		Specifications specifications = builder.build();
		StatementBuilder sfbuilder = new StatementBuilder()
				.withSpecifications(specifications);

		for (StatementField field : specifications.getFields()) {

			String value = rs.getString(field.getName());
			if (value != null) {
				sfbuilder.addField(field, value);
			}
		}

		return sfbuilder.build();
	}

	private int findColumnIndex(ResultSetMetaData metaData, String columnName) throws SQLException {

		for (int i=1 ; i<=metaData.getColumnCount() ; i++) {

			if (metaData.getColumnName(i).equals(columnName)) {
				return i;
			}
		}

		return -1;
	}

}