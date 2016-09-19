package org.nextprot.api.core.service.impl;

import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.dao.EntityName;
import org.nextprot.api.core.dao.GeneIdentifierDao;
import org.nextprot.api.core.service.GeneIdentifierService;
import org.nextprot.api.core.service.OverviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Lazy
@Service
public class GeneIdentifierServiceImpl implements GeneIdentifierService {

	@Autowired
	private OverviewService overviewService;

	@Autowired
	private GeneIdentifierDao geneIdentifierDao;

	@Autowired
	private MasterIdentifierService masterIdentifierService;

	@Override
	@Cacheable("all-gene-names")
	public Set<String> findGeneNamesSlow() {

		Set<String> entryNames = masterIdentifierService.findUniqueNames();

		return entryNames.parallelStream()
				//.limit(100)
				.map(this::findGeneNamesByEntryAccession)
				.filter(genes -> !genes.isEmpty())
				.flatMap(Collection::stream)
				.collect(Collectors.toSet());
	}

	@Override
	public Set<String> findGeneNames() {

		return geneIdentifierDao.findGeneNames();
	}

	@Override
	@Cacheable("gene-names-by-accession")
	public Set<String> findGeneNamesByEntryAccession(String entryAccession) {

		List<EntityName> geneNames = overviewService.findOverviewByEntry(entryAccession).getGeneNames();

		if (geneNames == null)
			return new HashSet<>();

		return geneNames.stream().map(EntityName::getName).collect(Collectors.toSet());
	}
}
