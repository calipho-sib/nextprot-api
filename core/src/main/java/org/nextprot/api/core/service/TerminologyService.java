package org.nextprot.api.core.service;

import java.util.List;
import java.util.Set;

import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.commons.utils.Tree;
import org.nextprot.api.core.domain.CvTerm;


public interface TerminologyService {
	
	/**
	 * Gets terminology by accession
	 * @param id
	 * @return
	 */
	public CvTerm findTerminologyByAccession(String accession);
	
	/**
	 * Gets terms by title case insensitive
	 * @param title
	 * @return
	 */
	public List<CvTerm> findTerminologByTitle(String title);
	
	/**
	 * Retrieves terms by name
	 * @param name
	 * @return
	 */
	public List<CvTerm> findTerminologyByName(String name);	

	/**
	 * Retrieves terms by ontology
	 * @param the name of ontology
	 * @return
	 */
	public List<CvTerm> findTerminologyByOntology(String ontology);	
	
	
	/**
	 * Retrieves terms sorted by ontology
	 * @return
	 */
	public List<CvTerm> findAllTerminology();

	/**
	 * Gets enzyme terminologies
	 * @param entryName
	 * @return
	 */
	public List<CvTerm> findEnzymeByMaster(String entryName);

	public List<CvTerm> findTerminologyByAccessions(Set<String> terminologyAccessions);

	/**
	 * Returns a tree for a given terminology
	 * @param terminology
	 * @return
	 */
	List<Tree<CvTerm>> findTerminologyTreeList(TerminologyCv terminologyCv);

	public List<String> findTerminologyNamesList();

	public Set<String> getAncestorSets(List<Tree<CvTerm>> trees, String accession);
}
