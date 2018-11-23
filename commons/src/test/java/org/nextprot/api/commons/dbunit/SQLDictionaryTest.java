package org.nextprot.api.commons.dbunit;

import org.junit.Test;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class SQLDictionaryTest extends CommonsUnitBaseTest {

	@Autowired
	private DataSourceServiceLocator dsLocator = null;

	@Autowired
	private SQLDictionary sqlDictionary;

	
	@Test
	public void shouldGetTestQuery() {
		String sql = sqlDictionary.getSQLQuery("test-query-for-unit-testing");
		assertEquals("select * from nextprot.cv_databases\n", sql);
	}
}
