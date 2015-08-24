package org.nextprot.api.core.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.nextprot.api.commons.utils.Pair;
import org.nextprot.api.core.domain.PeptideMapping;
import org.nextprot.api.core.domain.PeptideMapping.PeptideEvidence;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@DatabaseSetup(value = "PeptideMappingDaoTest.xml", type = DatabaseOperation.INSERT)
public class PeptideMappingDaoTest extends CoreUnitBaseTest {
	
	@Autowired private PeptideMappingDao peptideMappingDao;
	
	@Test
	public void findPeptidesByMasterId() {
		List<PeptideMapping> mappings = this.peptideMappingDao.findNaturalPeptidesByMasterId(596889L);
		
		assertEquals(1, mappings.size());
		assertEquals("NX_PEPT12345678", mappings.get(0).getPeptideUniqueName());
		assertEquals(1, mappings.get(0).getIsoformSpecificity().size());
		List<Pair<Integer, Integer>> positions = mappings.get(0).getIsoformSpecificity().get("NX_P12345-1").getPositions();
		assertEquals(1, positions.size());
		assertTrue(2 == positions.get(0).getFirst());
		assertTrue(1000 == positions.get(0).getSecond());
	}
	
	@Test
	public void findPeptideEvidences() {
		List<String> peptideNames = new ArrayList<String>();
		peptideNames.add("NX_PEPT12345678");
		List<PeptideEvidence> evidences = this.peptideMappingDao.findNaturalPeptideEvidences(peptideNames);
		assertEquals(1, evidences.size());
		assertEquals("789654", evidences.get(0).getAccession());
		assertEquals("NX_PEPT12345678", evidences.get(0).getPeptideName());
		assertEquals("PubMed", evidences.get(0).getDatabaseName());
	}

}
