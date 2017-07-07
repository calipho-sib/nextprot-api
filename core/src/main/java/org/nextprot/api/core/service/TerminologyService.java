package org.nextprot.api.core.service;

import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.commons.utils.Tree;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.Terminology;
import org.nextprot.api.core.utils.graph.CvTermGraph;

import java.util.List;
import java.util.Set;

public interface TerminologyService {

	/**
	 * @return a {@code CvTerm} by accession
	 */
	CvTerm findCvTermByAccession(String accession);

	/**
	 * @return a list of all {@code CvTerm}s of a given ontology
	 */
	List<CvTerm> findCvTermsByOntology(String ontology);

	/**
	 * @deprecated use {@link #findCvTermGraph(TerminologyCv)} instead
	 */
	@Deprecated
	Terminology findTerminology(TerminologyCv terminologyCv);

	/**
	 * @return a graph of {@code CvTerm}s of a given terminology
	 */
	CvTermGraph findCvTermGraph(TerminologyCv terminologyCv);

	/**
	 * Retrieves terms sorted by ontology
	 * 
	 * @return
	 */
	List<CvTerm> findAllCVTerms();

	/**
	 * Gets enzyme terminologies
	 * 
	 * @param entryName
	 * @return
	 */
	List<CvTerm> findEnzymeByMaster(String entryName);

	List<CvTerm> findCvTermsByAccessions(Set<String> terminologyAccessions);

	List<String> findTerminologyNamesList();

	//TODO TRY TO PLACE THIS ELSEWHERE, BUT PROBABLY SHOULD BE CACHED!
	Set<String> getAncestorSets(List<Tree<CvTerm>> trees, String accession);
}
