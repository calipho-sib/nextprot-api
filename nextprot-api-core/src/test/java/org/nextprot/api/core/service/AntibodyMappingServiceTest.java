package org.nextprot.api.core.service;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.nextprot.api.commons.dbunit.DBUnitBaseTest;
import org.nextprot.api.core.domain.AntibodyMapping;
import org.nextprot.api.core.domain.DbXref;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@DatabaseSetup(value = "AntibodyMappingServiceTest.xml", type = DatabaseOperation.INSERT)
public class AntibodyMappingServiceTest extends DBUnitBaseTest {

	@Autowired private AntibodyMappingService antibodyMappingService;
	
	@Test
	public void testFindAntibodyMappingByMasterId() {
		List<AntibodyMapping> mappings = this.antibodyMappingService.findAntibodyMappingByMasterId(596889L);
		assertEquals(1, mappings.size());
		assertEquals("NX_HPA004932", mappings.get(0).getAntibodyUniqueName());
	}
	
	@Test
	public void testFindAntibodyMappingByUniqueName() {
		List<AntibodyMapping> mappings = this.antibodyMappingService.findAntibodyMappingByMasterId(596889L);
		//System.out.println("mapping size:" + mappings.size());
		assertEquals(1, mappings.size());
		assertEquals("NX_HPA004932", mappings.get(0).getAntibodyUniqueName());
		List<DbXref> xrefs = mappings.get(0).getXrefs();
		//System.out.println("xrefs size:" + xrefs.size());
		assertEquals(1, xrefs.size());
		assertEquals("HPA", xrefs.get(0).getDatabaseName());
		
	}
}
