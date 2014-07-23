package org.nextprot.api.utils;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.dbunit.MVCBaseIntegrationTest;
import org.nextprot.auth.core.service.DataSourceServiceLocator;
import org.springframework.beans.factory.annotation.Autowired;

public class SQLDictionaryTest extends MVCBaseIntegrationTest {

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

		final String QUERY = "interactions_by_entry";
		final int LIMIT = 10;
		Connection c = dsLocator.getDataSource().getConnection();

		Map<String, String> map = new HashMap<String, String>();
		map.put(":entryName", "'NX_Q9Y6V7'");
		String sql = SQLDictionary.getSQLQuery(QUERY, map);
		PreparedStatement ps = c.prepareStatement(sql);
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
