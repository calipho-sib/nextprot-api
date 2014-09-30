package org.nextprot.api.core.service;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.nextprot.api.core.domain.PeptideMapping;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@DatabaseSetup(value = "PeptideMappingServiceTest.xml", type = DatabaseOperation.INSERT)
public class PeptideMappingServiceTest extends CoreUnitBaseTest {

	@Autowired private PeptideMappingService peptideMappingService;
	
	@Test
	public void testFindPeptideMappingByMasterId() {
		List<PeptideMapping> mapping = this.peptideMappingService.findPeptideMappingByMasterId(596889L);
		assertEquals(1, mapping.size());
		assertEquals("NX_PEPT12345678", mapping.get(0).getPeptideUniqueName());
		assertEquals(1, mapping.get(0).getEvidences().size());
		assertEquals("789654", mapping.get(0).getEvidences().get(0).getAccession());
	}
	
	@Test
	public void testFindPeptideMappingByUniqueName() {
		List<PeptideMapping> mapping = this.peptideMappingService.findPeptideMappingByUniqueName("NX_P12345");
		assertEquals("NX_PEPT12345678", mapping.get(0).getPeptideUniqueName());
		assertEquals(1, mapping.get(0).getEvidences().size());
		assertEquals("789654", mapping.get(0).getEvidences().get(0).getAccession());
	}
}
