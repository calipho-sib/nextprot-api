package org.nextprot.api.core.service.impl;

import java.util.List;

import org.nextprot.api.core.dao.GeneDAO;
import org.nextprot.api.core.domain.GeneRegion;
import org.nextprot.api.core.domain.exon.SimpleExonWithSequence;
import org.nextprot.api.core.service.GeneMasterIsoformMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GeneExonMappingServiceImpl implements GeneMasterIsoformMappingService {

	@Autowired
	GeneDAO geneDao;
	
	@Override
	public List<SimpleExonWithSequence> findGeneExons(String geneName) {
		return geneDao.findExonsOfGene(geneName);
	}

	@Override
	public List<GeneRegion> findEntryGeneRegions(String entryName) {
		return geneDao.findMasterGeneRegions(entryName);
	}


}
