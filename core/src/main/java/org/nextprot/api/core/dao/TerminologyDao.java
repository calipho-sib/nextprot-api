package org.nextprot.api.core.dao;

import java.util.List;
import java.util.Set;

import org.nextprot.api.core.domain.CvTerm;

public interface TerminologyDao {

	CvTerm findTerminologyByAccession(String accession);

	List<CvTerm> findTerminologByTitle(String title); // Not implemented

	List<CvTerm> findTerminologyByName(String name); // Not implemented
	
	List<CvTerm> findTerminologyByOntology(String ontology);

	List<CvTerm> findAllTerminology();

	List<CvTerm> findTerminologyByAccessions(Set<String> accessions);

	List<CvTerm> findTermByAccessionAndTerminology(String accession, String terminology);

	List<String> findTerminologyNamesList();

	List<String> findEnzymeAcsByMaster(String entryName);

}
