package org.nextprot.api.core.dao;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.Mdata;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ActiveProfiles({ "dev"})
public class MdataDaoIntegrationTest extends CoreUnitBaseTest {

	@Autowired
	private MdataDao mdataDao;

	@Test
	public void should_Find_Non_Empty_Evidence_Mdata_Map_And_Corresponding_Mdata_Records_For_PeptideMappings_Of_NX_Q99622() {

		Map<Long,Long> map = mdataDao.findEvidenceIdMdataIdMapForPeptideMappingsByEntryName("NX_Q99622");
		Assert.assertTrue(map.size()>0);
		map.keySet().forEach(k -> Assert.assertTrue(k.intValue()>0));
		map.values().forEach(v -> Assert.assertTrue(v.intValue()>0));

		//map.entrySet().forEach(e -> System.out.println("evidence:"+ e.getKey() + " - mdata:" + e.getValue()));

		
		// now retrieve the mdata objects from the set of mdata ids retrieved above
		Set<Long> valueSet = new HashSet<>(map.values());
		List<Long> uniqueIds = new ArrayList<Long>(valueSet);
		List<Mdata> mdatas = mdataDao.findMdataByIds(uniqueIds);
		Assert.assertEquals(valueSet.size(), mdatas.size());
		
		//mdatas.forEach(v -> System.out.println(v));
	}
	
	@Test
	public void should_Find_Non_Empty_Evidence_Mdata_Map_And_Corresponding_Mdata_Records_For_PTMs_Of_NX_Q99622() {

		// we should get a list of map entries with evidence id as key and mdata id as value
		// no id should be null
		Map<Long,Long> map = mdataDao.findEvidenceIdMdataIdMapForPTMsByEntryName("NX_Q99622");
		Assert.assertTrue(map.size()>0);
		map.keySet().forEach(k -> Assert.assertTrue(k.intValue()>0));
		map.values().forEach(v -> Assert.assertTrue(v.intValue()>0));

		// now retrieve the mdata objects from the set of mdata ids retrieved above
		Set<Long> valueSet = new HashSet<>(map.values());
		List<Long> uniqueIds = new ArrayList<Long>(valueSet);
		List<Mdata> mdatas = mdataDao.findMdataByIds(uniqueIds);
		Assert.assertEquals(valueSet.size(), mdatas.size());

		//mdatas.forEach(v -> System.out.println(v));
	}
	
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 	
	// tests related to queries that are useful to get sample evidences
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 	
	
	@Test
	public void should_Find_10_Nextprot_Ptm_Evidences() {
		
		int sampleSet = 10;
		
		List<Long> evidenceIds = mdataDao.findExamplesOfEvidencesHavingMdataForNextprotPTMs(sampleSet);
		Set<Long> eviSet = new HashSet<>();
		evidenceIds.forEach(eviSet::add);

		// we should find sampleSet=10 such evidences in the db
		Assert.assertEquals(sampleSet, eviSet.size()); 
		
	}

	@Test
	public void should_Find_10_Non_Nextprot_Ptm_Evidences() {
		
		int sampleSet = 10;
		
		List<Long> evidenceIds = mdataDao.findExamplesOfEvidencesHavingMdataForNonNextprotPTMs(sampleSet);
		Set<Long> eviSet = new HashSet<>();
		evidenceIds.forEach(eviSet::add);

		// we should find sampleSet=10 such evidences in the db
		Assert.assertEquals(sampleSet, eviSet.size()); 
				
	}

	
	
}
