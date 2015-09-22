package org.nextprot.api.core.dao;

import java.util.List;
import java.util.Set;

import org.nextprot.api.core.domain.Terminology;

public interface TerminologyDao {

	Terminology findTerminologyByAccession(String accession);

	List<Terminology> findTerminologByTitle(String title); // Not implemented

	List<Terminology> findTerminologyByName(String name); // Not implemented
	
	List<Terminology> findTerminologyByOntology(String ontology);

	List<Terminology> findAllTerminology();

	List<Terminology> findTerminologyByAccessions(Set<String> accessions);

	List<Terminology> findTermByAccessionAndTerminology(String accession, String terminology);

	List<String> findTerminologyNamesList();

}
