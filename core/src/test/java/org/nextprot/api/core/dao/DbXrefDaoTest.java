package org.nextprot.api.core.dao;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@DatabaseSetup(value = "DbXrefDaoTest.xml", type = DatabaseOperation.INSERT)
public class DbXrefDaoTest extends CoreUnitBaseTest {

	@Autowired private DbXrefDao xrefDao;
	
	@Test
	public void findDbXRefsByPublicationId() {
		List<DbXref> xrefs = this.xrefDao.findDbXRefsByPublicationId(100L);
		assertEquals(1, xrefs.size());
		assertEquals("PubMed", xrefs.get(0).getDatabaseName());
		assertEquals("cat", xrefs.get(0).getDatabaseCategory());
	}
	
	@Test
	public void findDbXrefsByMaster() {
		List<DbXref> xrefs = this.xrefDao.findDbXrefsByMaster("NX_P12345");
		assertEquals(3, xrefs.size());
		
		assertEquals("Cosmic", xrefs.get(0).getDatabaseName());
		assertEquals(4000L, xrefs.get(0).getDbXrefId().longValue());
		assertEquals("333", xrefs.get(0).getAccession());
		assertEquals("cat", xrefs.get(0).getDatabaseCategory());
		assertEquals("http://cosmic.com", xrefs.get(0).getUrl());
		assertEquals("http://cosmic.com?id=5", xrefs.get(0).getLinkUrl());
		
		assertEquals("Ensembl", xrefs.get(1).getDatabaseName());
		assertEquals(130L, xrefs.get(1).getDbXrefId().longValue());
		assertEquals("3D", xrefs.get(1).getAccession());
		assertEquals("cat", xrefs.get(1).getDatabaseCategory());
		assertEquals("http://ensembl.com", xrefs.get(1).getUrl());
		assertEquals("http://ensembl.com?id=7", xrefs.get(1).getLinkUrl());
		
		assertEquals("PubMed", xrefs.get(2).getDatabaseName());
		assertEquals(120L, xrefs.get(2).getDbXrefId().longValue());
		assertEquals("789654", xrefs.get(2).getAccession());
		assertEquals("cat", xrefs.get(2).getDatabaseCategory());
		assertEquals("http://pubmed.com", xrefs.get(2).getUrl());
		assertEquals("http://pubmed.com?id=3", xrefs.get(2).getLinkUrl());
	}
}
