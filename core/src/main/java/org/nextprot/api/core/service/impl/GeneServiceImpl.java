package org.nextprot.api.core.service.impl;

import com.google.common.collect.ImmutableList;
import org.nextprot.api.core.dao.GeneDAO;
import org.nextprot.api.core.domain.ChromosomalLocation;
import org.nextprot.api.core.service.GeneIdentifierService;
import org.nextprot.api.core.service.GeneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class GeneServiceImpl implements GeneService {

	@Autowired private GeneDAO geneDAO;
	@Autowired
    private GeneIdentifierService geneIdentifierService;

	@Override
	@Cacheable(value = "chromosomal-locations", sync = true)
	public List<ChromosomalLocation> findChromosomalLocationsByEntry(String entryName) {
		List<ChromosomalLocation> chroms = geneDAO.findChromosomalLocationsByEntryName(entryName);
		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<ChromosomalLocation>().addAll(chroms).build();
	}

    @Override
    public boolean isValidGeneName(String accession, String geneName) {

        return geneIdentifierService.findGeneNamesByEntryAccession(accession).contains(geneName);
    }
}
