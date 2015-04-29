package org.nextprot.api.core.dao;

import java.util.List;

import org.nextprot.api.core.domain.Terminology;

public interface TerminologyDao {

	public Terminology findTerminologyByAccession(String accession);

	List<Terminology> findTerminologByTitle(String title); // Not implemented

	List<Terminology> findTerminologyByName(String name); // Not implemented
	
	List<Terminology> findTerminologyByOntology(String ontology);

	List<Terminology> findAllTerminology();

}
