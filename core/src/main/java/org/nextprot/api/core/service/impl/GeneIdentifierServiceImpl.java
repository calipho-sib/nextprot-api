package org.nextprot.api.core.service.impl;

import org.nextprot.api.core.dao.GeneIdentifierDao;
import org.nextprot.api.core.domain.ChromosomalLocation;
import org.nextprot.api.core.domain.EntityName;
import org.nextprot.api.core.service.GeneIdentifierService;
import org.nextprot.api.core.service.OverviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Lazy
@Service
public class GeneIdentifierServiceImpl implements GeneIdentifierService {

	@Autowired
	private GeneIdentifierDao geneIdentifierDao;

	@Autowired
	private OverviewService overviewService;

	@Override
	@Cacheable(value = "all-gene-names", sync = true)
	public Set<String> findGeneNames() {

		/*Set<String> entryNames = masterIdentifierService.findUniqueNames();
		return entryNames.parallelStream()
				.map(this::findGeneNamesByEntryAccession)
				.filter(genes -> !genes.isEmpty())
				.flatMap(Collection::stream)
				.collect(Collectors.toSet());*/

		return geneIdentifierDao.findGeneNames();
	}

	@Override
	@Cacheable(value = "gene-names-by-accession", sync = true)
	public List<String> findGeneNamesByEntryAccession(String entryAccession) {

        List<String> geneNames = new ArrayList<>();

		List<EntityName> entityNames = overviewService.findOverviewByEntry(entryAccession).getGeneNames();

		if (entityNames == null)
			return geneNames;


		for (EntityName entityName : entityNames) {

            geneNames.add(entityName.getName());
            geneNames.addAll(entityName.getSynonyms().stream().map(syn -> syn.getName()).collect(Collectors.toList()));
        }

		return geneNames;
	}

	@Override
	@Cacheable(value = "all-entry-gene-names", sync = true)
	public Map<String, List<String>> findEntryGeneNames() {

		return geneIdentifierDao.findEntryGeneNames();
	}

	@Override
	public String findEntryGeneNamesByChromosomeLocation(ChromosomalLocation chromosomalLocation) {
		String geneName = geneIdentifierDao.findGeneNameByChromosomalLocation(chromosomalLocation);
		return geneName;
	}
}
