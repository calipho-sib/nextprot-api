package org.nextprot.api.commons.service;

import java.util.List;
import java.util.Set;

import org.nextprot.api.commons.dao.MasterIdentifierDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;

@Lazy
@Service
public class MasterIdentifierServiceImpl implements MasterIdentifierService {
	@Autowired
	private MasterIdentifierDao masterIdentifierDao;

	@Override
	@Cacheable("master-unique-names-chromossome")
	public List<String> findUniqueNamesOfChromosome(String chromosome) {
		return this.masterIdentifierDao.findUniqueNamesOfChromosome(chromosome);
	}

	@Override
	@Cacheable("master-unique-names")
	public Set<String> findUniqueNames() {
		return Sets.newHashSet(this.masterIdentifierDao.findUniqueNames());
	}

	@Override
	@Cacheable("master-unique-name")
	public Long findIdByUniqueName(String uniqueName) {
		return this.masterIdentifierDao.findIdByUniqueName(uniqueName);
	}

	@Override
	@Cacheable("entry-accession-by-gene-name")
	public Set<String> findEntryAccessionByGeneName(String geneName) {
		return Sets.newHashSet(this.masterIdentifierDao.findUniqueNamesByGeneName(geneName));
	}
}
