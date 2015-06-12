package org.nextprot.api.core.dao;

import org.nextprot.api.core.domain.Terminology;

import java.util.List;
import java.util.Map;

public interface TerminologyDao {

	Terminology findTerminologyByAccession(String accession);

	Map<String, Terminology> findTerminologyByAccessions(List<String> accessions);

	List<Terminology> findTerminologByTitle(String title); // Not implemented

	List<Terminology> findTerminologyByName(String name); // Not implemented
	
	List<Terminology> findTerminologyByOntology(String ontology);

	List<Terminology> findAllTerminology();

}
