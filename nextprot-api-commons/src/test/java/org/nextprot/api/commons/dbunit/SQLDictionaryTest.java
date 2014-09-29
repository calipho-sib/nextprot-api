package org.nextprot.api.commons.dbunit;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.springframework.beans.factory.annotation.Autowired;

public class SQLDictionaryTest extends CommonsUnitBaseTest {

	@Autowired
	private DataSourceServiceLocator dsLocator = null;

	/**
	 * @param args
	 */
	@Test
	public void shouldGetTestQuery() {
		String sql = SQLDictionary.getSQLQuery("test-query-for-unit-testing");
		assertEquals("select * from nextprot.cv_databases\n", sql);
	}

	/**
	 * @param args
	 * @throws SQLException
	 */
	@Test
	public void shouldExecuteAQuery() throws SQLException {

		final int LIMIT = 10;
		Connection c = dsLocator.getDataSource().getConnection();
		PreparedStatement ps = c.prepareStatement(SQLDictionary.getSQLQuery("test-query-for-unit-testing"));
		ResultSet rs = ps.executeQuery();

		int j = 0;
		while (rs.next() && (j < LIMIT)) {
			j++;
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			if (j == 1) {

				for (int i = 0; i < columnCount; i++) {
					System.out.print(rsmd.getColumnLabel(i + 1));
					System.out.print("\t");
				}

				System.out.println();

			}

			for (int i = 0; i < columnCount; i++) {
				System.out.print(rs.getString(i + 1));
				System.out.print("\t");
			}
			System.out.println();

		}

		rs.close();
		ps.close();
		c.close();
	}

}
