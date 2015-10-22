package org.nextprot.api.core.service;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@DatabaseSetup(value = "AntibodyMappingServiceTest.xml", type = DatabaseOperation.INSERT)
public class AntibodyMappingServiceTest extends CoreUnitBaseTest {

	@Autowired private AntibodyMappingService antibodyMappingService;
	
	@Test
	public void testFindAntibodyMappingByMasterId() {
		List<Annotation> annots = this.antibodyMappingService.findAntibodyMappingAnnotationsByUniqueName("NX_P06213");
		Assert.assertEquals(2, annots.size());
		// TODO: see with pam
		//assertEquals("NX_HPA004932", mappings.get(0).getAntibodyUniqueName());
	}
	
	@Test
	public void testFindAntibodyMappingByUniqueName() {
		/*
		List<AntibodyMapping> mappings = this.antibodyMappingService.findAntibodyMappingByUniqueName("NX_P12345");
		//System.out.println("mapping size:" + mappings.size());
		assertEquals(1, mappings.size());
		assertEquals("NX_HPA004932", mappings.get(0).getAntibodyUniqueName());
		List<DbXref> xrefs = mappings.get(0).getXrefs();
		//System.out.println("xrefs size:" + xrefs.size());
		assertEquals(1, xrefs.size());
		assertEquals("HPA", xrefs.get(0).getDatabaseName());
		*/
		
	}
}
