package org.nextprot.api.core.service.impl;

import java.util.List;

import org.nextprot.api.core.dao.GeneDAO;
import org.nextprot.api.core.domain.exon.SimpleExonWithSequence;
import org.nextprot.api.core.service.GeneExonMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GeneExonMappingServiceImpl implements GeneExonMappingService {

	@Autowired
	GeneDAO geneDao;
	
	@Override
	public List<SimpleExonWithSequence> findGeneExons(String geneName) {
		return geneDao.findExonsOfGene(geneName);
	}


}
