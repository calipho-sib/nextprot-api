package org.nextprot.api.core.dao;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.Family;
import org.nextprot.api.core.domain.Mdata;
import org.nextprot.api.core.service.FamilyService;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@ActiveProfiles({ "dev" })
public class MdataDaoIntegrationTest extends CoreUnitBaseTest {

	@Autowired
	private MdataDao mdataDao;
	
	@Test
	public void should_Find_10_Mdata_For_10_Nextprot_Ptm_Evidences() {
		
		int sampleSet = 10;
		
		List<Long> evidenceIds = mdataDao.findExamplesOfEvidencesHavingMdataForNextprotPTMs(sampleSet);
		Set<Long> eviSet = new HashSet<>();
		evidenceIds.forEach(eviSet::add);

		// we should find sampleSet=10 such evidences in the db
		Assert.assertEquals(sampleSet, eviSet.size()); 
		
		List<Mdata> mdata = mdataDao.findMdataForNextprotPTMs(evidenceIds);
		Set<Long> mdataEviSet = new HashSet<>();
		mdata.forEach(md -> mdataEviSet.add(md.getEvidenceId()));
		
		// we should retrieve one mdata for each evidence id
		Assert.assertEquals(sampleSet, mdataEviSet.size());
		Assert.assertEquals(eviSet, mdataEviSet);
		
	}

	@Test
	public void should_Find_10_Mdata_For_10_Non_Nextprot_Ptm_Evidences() {
		
		int sampleSet = 10;
		
		List<Long> evidenceIds = mdataDao.findExamplesOfEvidencesHavingMdataForNonNextprotPTMs(sampleSet);
		Set<Long> eviSet = new HashSet<>();
		evidenceIds.forEach(eviSet::add);
		evidenceIds.forEach(e -> System.out.println(e.toString()));

		// we should find sampleSet=10 such evidences in the db
		Assert.assertEquals(sampleSet, eviSet.size()); 
		
		List<Mdata> mdata = mdataDao.findMdataForNonNextprotPTMs(evidenceIds);
		Set<Long> mdataEviSet = new HashSet<>();
		mdata.forEach(md -> mdataEviSet.add(md.getEvidenceId()));
		
		// we should retrieve one mdata for each evidence id
		Assert.assertEquals(sampleSet, mdataEviSet.size());
		Assert.assertEquals(eviSet, mdataEviSet);
		
	}

	
	
}
