package org.nextprot.api.core.service;

import java.util.List;
import java.util.Set;

import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.commons.utils.Tree;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.Terminology;

public interface TerminologyService {

	/**
	 * Gets terminology by accession
	 * 
	 * @param id
	 * @return
	 */
	public CvTerm findCvTermByAccession(String accession);


	/**
	 * Retrieves terms by ontology
	 * 
	 * @param the
	 *            name of ontology
	 * @return
	 */
	public List<CvTerm> findCvTermsByOntology(String ontology);

	/**
	 * Returns a tree for a given terminology
	 * 
	 * @param terminology
	 * @return
	 */
	Terminology findTerminology(TerminologyCv terminologyCv);
	
	/**
	 * Retrieves terms sorted by ontology
	 * 
	 * @return
	 */
	public List<CvTerm> findAllCVTerms();

	/**
	 * Gets enzyme terminologies
	 * 
	 * @param entryName
	 * @return
	 */
	public List<CvTerm> findEnzymeByMaster(String entryName);

	public List<CvTerm> findCvTermsByAccessions(Set<String> terminologyAccessions);



	public List<String> findTerminologyNamesList();


	//TODO TRY TO PLACE THIS ELSEWHERE, BUT PROBABLY SHOULD BE CACHED!
	public Set<String> getAncestorSets(List<Tree<CvTerm>> trees, String accession);
}
