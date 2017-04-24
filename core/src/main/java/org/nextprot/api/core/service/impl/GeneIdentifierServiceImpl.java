package org.nextprot.api.core.service.impl;

import org.nextprot.api.core.dao.EntityName;
import org.nextprot.api.core.dao.GeneIdentifierDao;
import org.nextprot.api.core.service.GeneIdentifierService;
import org.nextprot.api.core.service.OverviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Lazy
@Service
public class GeneIdentifierServiceImpl implements GeneIdentifierService {

	@Autowired
	private GeneIdentifierDao geneIdentifierDao;

	@Autowired
	private OverviewService overviewService;

	@Override
	@Cacheable("all-gene-names")
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
	@Cacheable("gene-names-by-accession")
	public List<String> findGeneNamesByEntryAccession(String entryAccession) {

		List<EntityName> geneNames = overviewService.findOverviewByEntry(entryAccession).getGeneNames();

		if (geneNames == null)
			return new ArrayList<>();

		return geneNames.stream().map(EntityName::getName).collect(Collectors.toList());
	}

	@Override
	@Cacheable("all-entry-gene-names")
	public Map<String, List<String>> findEntryGeneNames() {

		return geneIdentifierDao.findEntryGeneNames();
	}
}
