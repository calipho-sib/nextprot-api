package org.nextprot.api.core.service.impl;

import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.dao.EntityName;
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
	private OverviewService overviewService;

	@Autowired
	private MasterIdentifierService masterIdentifierService;

	@Override
	//@Cacheable("gene-names")
	public Set<String> findGeneNames() {

		Set<String> entryNames = masterIdentifierService.findUniqueNames();

		return entryNames.parallelStream()
				//.limit(100)
				.map(this::findGeneNamesByEntryAccession)
				.filter(genes -> !genes.isEmpty())
				.flatMap(Collection::stream)
				.collect(Collectors.toSet());
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
