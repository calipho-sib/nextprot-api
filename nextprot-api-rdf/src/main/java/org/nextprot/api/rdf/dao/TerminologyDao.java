package org.nextprot.api.rdf.dao;

import java.util.List;

import org.nextprot.api.rdf.domain.Terminology;

public interface TerminologyDao {

	public Terminology findTerminologyByAccession(String accession);

	List<Terminology> findTerminologByTitle(String title);

	List<Terminology> findTerminologyByName(String name) ;
	
	List<Terminology> findTerminologyByOntology(String ontology);

	List<Terminology> findAllTerminology();

}
