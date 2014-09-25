package org.nextprot.api.service;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.service.DbXrefService;
import org.nextprot.api.dbunit.DBUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@DatabaseSetup(value = "DbXrefServiceTest.xml", type = DatabaseOperation.INSERT)
public class DbXrefServiceTest extends DBUnitBaseTest {

	@Autowired private DbXrefService xrefService;
	
	
	@Test
	public void testFindDbXrefsByPublicationIds() {
		List<DbXref> xrefs = this.xrefService.findDbXRefByPublicationId(100L);
		assertEquals(1, xrefs.size());
		assertEquals("15923218", xrefs.get(0).getAccession());
	}
	
	@Test
	public void testFindDbXrefsByMaster() {
		List<DbXref> xrefs = this.xrefService.findDbXrefsByMaster("NX_P12345");
	
		assertEquals(1, xrefs.size());
		assertEquals(1, xrefs.get(0).getProperties().size());
		assertEquals("money", xrefs.get(0).getProperties().get(0).getName());
		
	}
}
