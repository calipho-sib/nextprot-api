package org.nextprot.api.core.service;

import java.util.List;
import java.util.Set;

import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.commons.utils.Tree;
import org.nextprot.api.core.domain.Terminology;


public interface TerminologyService {
	
	/**
	 * Gets terminology by accession
	 * @param id
	 * @return
	 */
	public Terminology findTerminologyByAccession(String accession);
	
	/**
	 * Gets terms by title case insensitive
	 * @param title
	 * @return
	 */
	public List<Terminology> findTerminologByTitle(String title);
	
	/**
	 * Retrieves terms by name
	 * @param name
	 * @return
	 */
	public List<Terminology> findTerminologyByName(String name);	

	/**
	 * Retrieves terms by ontology
	 * @param the name of ontology
	 * @return
	 */
	public List<Terminology> findTerminologyByOntology(String ontology);	
	
	
	/**
	 * Retrieves terms sorted by ontology
	 * @return
	 */
	public List<Terminology> findAllTerminology();

	/**
	 * Gets enzyme terminologies
	 * @param entryName
	 * @return
	 */
	public List<Terminology> findEnzymeByMaster(String entryName);

	public List<Terminology> findTerminologyByAccessions(Set<String> terminologyAccessions);

	/**
	 * Returns a tree for a given terminology
	 * @param terminology
	 * @return
	 */
	List<Tree<Terminology>> findTerminologyTreeList(TerminologyCv terminologyCv);

	public List<String> findTerminologyNamesList();

	public Set<String> getAncestorSets(List<Tree<Terminology>> trees, String accession);
}
