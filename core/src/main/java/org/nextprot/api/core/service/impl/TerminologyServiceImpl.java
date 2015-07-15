package org.nextprot.api.core.service.impl;

import java.util.List;
import java.util.Set;

import org.nextprot.api.core.dao.EnzymeDao;
import org.nextprot.api.core.dao.TerminologyDao;
import org.nextprot.api.core.domain.Terminology;
import org.nextprot.api.core.service.TerminologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableList;

@Service
class TerminologyServiceImpl implements TerminologyService {

	@Autowired private TerminologyDao terminologyDao;
	@Autowired private EnzymeDao enzymeDao;
	
	@Override
	@Cacheable("terminology-by-accession")
	public Terminology findTerminologyByAccession(String accession) {
		return terminologyDao.findTerminologyByAccession(accession);
	}

	@Override
	public List<Terminology> findTerminologByTitle(String title) {
		return terminologyDao.findTerminologByTitle(title);
	}

	@Override
	public List<Terminology> findTerminologyByName(String name) {
		return terminologyDao.findTerminologyByName(name);
	}

	@Override
	@Cacheable("terminology-by-ontology")
	public List<Terminology> findTerminologyByOntology(String ontology) {
		List<Terminology> terms = terminologyDao.findTerminologyByOntology(ontology);
		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<Terminology>().addAll(terms).build();

	}

	@Override
	@Cacheable("terminology-all")
	public List<Terminology> findAllTerminology() {
		List<Terminology> terms =  terminologyDao.findAllTerminology();
		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<Terminology>().addAll(terms).build();

	}

	@Override
	@Cacheable("enzyme-terminology") //TODO there should be an utiliy method on entry to get the enzymes...
	public List<Terminology> findEnzymeByMaster(String entryName) {
		List<Terminology> terms =  enzymeDao.findEnzymeByMaster(entryName);
		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<Terminology>().addAll(terms).build();

	}

	@Override
	public List<Terminology> findTerminologyByAccessions(Set<String> terminologyAccessions) {
		List<Terminology> terms =  terminologyDao.findTerminologyByAccessions(terminologyAccessions);
		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<Terminology>().addAll(terms).build();

	}

}
