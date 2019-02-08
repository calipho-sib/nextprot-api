package org.nextprot.api.core.service.impl;

import com.google.common.collect.Sets;
import org.nextprot.api.core.dao.MasterIdentifierDao;
import org.nextprot.api.core.domain.ProteinExistence;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.service.OverviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Lazy
@Service
public class MasterIdentifierServiceImpl implements MasterIdentifierService {

	@Autowired
	private MasterIdentifierDao masterIdentifierDao;

	@Autowired
	private OverviewService overviewService;

	@Override
	@Cacheable(value = "master-unique-names-chromossome", sync = true)
	public List<String> findUniqueNamesOfChromosome(String chromosome) {
		return this.masterIdentifierDao.findUniqueNamesOfChromosome(chromosome);
	}

	@Override
	public Set<String> findUniqueNames() {
		return Sets.newTreeSet(this.masterIdentifierDao.findUniqueNames());
	}

	@Override
	@Cacheable(value = "master-unique-name", sync = true)
	public Long findIdByUniqueName(String uniqueName) {
		return this.masterIdentifierDao.findIdByUniqueName(uniqueName);
	}

	@Override
	@Cacheable(value="entry-accession-by-gene-name",key="{  #geneName, #withSynonyms }", sync = true)
	public Set<String> findEntryAccessionByGeneName(String geneName, boolean withSynonyms) {
		return Sets.newTreeSet(this.masterIdentifierDao.findUniqueNamesByGeneName(geneName, withSynonyms));
	}

	@Override
	@Cacheable(value="entry-accession-by-protein-existence", sync = true)
	public List<String> findEntryAccessionsByProteinExistence(ProteinExistence proteinExistence) {

		List<String> entries = new ArrayList<>();

		for (String entryAccession : masterIdentifierDao.findUniqueNames()) {

			ProteinExistence pe = overviewService.findOverviewByEntry(entryAccession).getProteinExistence();

			if (pe == proteinExistence) {
				entries.add(entryAccession);
			}
		}

		return entries;
	}
}
