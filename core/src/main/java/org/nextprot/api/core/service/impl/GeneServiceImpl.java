package org.nextprot.api.core.service.impl;

import java.util.List;

import org.nextprot.api.core.dao.GeneDAO;
import org.nextprot.api.core.domain.ChromosomalLocation;
import org.nextprot.api.core.service.GeneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
class GeneServiceImpl implements GeneService {

	@Autowired private GeneDAO geneDAO;

	@Override
	@Cacheable("chromosomal-locations")
	public List<ChromosomalLocation> findChromosomalLocationsByEntry(String entryName) {
		return geneDAO.findChromosomalLocationsByEntryName(entryName);
	}

}
