package org.nextprot.api.core.service.impl;

import java.util.List;

import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.rdf.dao.TerminologyDao;
import org.nextprot.api.rdf.domain.Terminology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class TerminologyServiceImpl implements TerminologyService {

	@Autowired private TerminologyDao terminologyDao;
	
	@Override
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

}
