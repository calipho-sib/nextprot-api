package org.nextprot.api.core.dao;

import org.nextprot.api.core.domain.Terminology;

import java.util.List;

public interface TerminologyDao {

	Terminology findTerminologyByAccession(String accession);

	List<Terminology> findTerminologByTitle(String title);

	List<Terminology> findTerminologyByName(String name) ;
	
	List<Terminology> findTerminologyByOntology(String ontology);

	List<Terminology> findAllTerminology();

}
