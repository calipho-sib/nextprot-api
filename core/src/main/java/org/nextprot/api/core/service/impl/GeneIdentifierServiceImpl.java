package org.nextprot.api.core.service.impl;

import org.nextprot.api.core.dao.GeneIdentifierDao;
import org.nextprot.api.core.domain.ChromosomalLocation;
import org.nextprot.api.core.domain.EntityName;
import org.nextprot.api.core.service.GeneIdentifierService;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.service.OverviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;


@Lazy
@Service
public class GeneIdentifierServiceImpl implements GeneIdentifierService {

	@Autowired
	private GeneIdentifierDao geneIdentifierDao;

	@Autowired
	private OverviewService overviewService;
	@Autowired
	private MasterIdentifierService masterService;

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
	@Cacheable(value = "entry-gene-names-and-alt-names", sync = true)
	public Map<String,List<String>> findEntryGeneNamesAndAltNames() {
		Map<String,List<String>> result = new TreeMap<String,List<String>>();
		Set<String> acs = masterService.findUniqueNames();
		int cpt = 0;
		for (String ac: acs) {
			cpt++;
			if (cpt % 500 == 0) System.out.println(new Date() + " - building entry gene names and alt names " + cpt + " / " + acs.size());
	        List<String> geneNames = new ArrayList<>();
			List<EntityName> entityNames = overviewService.findOverviewByEntry(ac).getGeneNames();
			if (entityNames != null) {
				for (EntityName entityName : entityNames) {
		            geneNames.add(entityName.getName());
		            geneNames.addAll(entityName.getSynonyms().stream().map(syn -> syn.getName()).collect(Collectors.toList()));
		        }
			}
			result.put(ac, geneNames);
		}
		System.out.println(new Date() + " - built entry gene names and alt names " + cpt + " / " + acs.size());
		return result;
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
