package org.nextprot.api.core.dao;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.nextprot.api.commons.dbunit.DBUnitBaseTest;
import org.nextprot.api.core.domain.Keyword;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@DatabaseSetup(value = "KeywordDaoTest.xml", type = DatabaseOperation.INSERT)
public class KeywordDaoTest extends DBUnitBaseTest {

	@Autowired private KeywordDao keywordDao;
	
	@Test
	public void testFindKeywordByMaster() {
		List<Keyword> keywords = this.keywordDao.findKeywordByMaster("NX_P12345");

		for (Keyword k: keywords) System.out.println(k.getAccession());
		
		assertEquals(3, keywords.size());
		assertEquals("KW-1", keywords.get(0).getAccession());
		assertEquals("keyword 1", keywords.get(0).getName());
		assertEquals("KW-2", keywords.get(1).getAccession());
		assertEquals("keyword 2", keywords.get(1).getName());
		assertEquals("KW-3", keywords.get(2).getAccession());
		assertEquals("keyword 3", keywords.get(2).getName());
	}
}
