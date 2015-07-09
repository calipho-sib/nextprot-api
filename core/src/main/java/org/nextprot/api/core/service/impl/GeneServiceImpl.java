package org.nextprot.api.core.service.impl;

import java.util.List;

import org.nextprot.api.core.dao.GeneDAO;
import org.nextprot.api.core.domain.ChromosomalLocation;
import org.nextprot.api.core.service.GeneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableList;

@Service
class GeneServiceImpl implements GeneService {

	@Autowired private GeneDAO geneDAO;

	@Override
	@Cacheable("chromosomal-locations")
	public List<ChromosomalLocation> findChromosomalLocationsByEntry(String entryName) {
		List<ChromosomalLocation> chroms = geneDAO.findChromosomalLocationsByEntryName(entryName);
		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<ChromosomalLocation>().addAll(chroms).build();
	}

}
