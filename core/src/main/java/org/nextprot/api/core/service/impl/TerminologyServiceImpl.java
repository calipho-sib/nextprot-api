package org.nextprot.api.core.service.impl;

import java.util.List;

import org.nextprot.api.core.dao.EnzymeDao;
import org.nextprot.api.core.dao.TerminologyDao;
import org.nextprot.api.core.domain.Terminology;
import org.nextprot.api.core.service.TerminologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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
		return terminologyDao.findTerminologyByOntology(ontology);
	}

	@Override
	@Cacheable("terminology-all")
	public List<Terminology> findAllTerminology() {
		return terminologyDao.findAllTerminology();
	}

	@Override
	@Cacheable("enzyme-terminology") //TODO there should be an utiliy method on entry to get the enzymes...
	public List<Terminology> findEnzymeByMaster(String entryName) {
		return enzymeDao.findEnzymeByMaster(entryName);
	}

}
