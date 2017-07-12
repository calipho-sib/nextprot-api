package org.nextprot.api.core.service;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.commons.utils.Tree;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Terminology;
import org.nextprot.api.core.utils.graph.CvTermGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

	/**
	 * Get the list of xref(s) accession found for the specific database
	 * @param accession the cvterm accession
	 * @param databaseName the database name of the
	 * @return a xref accessions list
	 */
	default List<String> findCvTermXrefAccessionList(String accession, String databaseName) {

		Preconditions.checkNotNull(accession);

		CvTerm term = findCvTermByAccession(accession);

		List<String> accessions = new ArrayList<>();

		if (term != null) {

			return term.getXrefs().stream()
					.filter(xref -> xref.getDatabaseName().equals(databaseName))
					.map(DbXref::getAccession)
					.collect(Collectors.toList());
		}

		return accessions;
	}

	default String findPsiModAccession(String accession) {

		List<String> accessions = findCvTermXrefAccessionList(accession, "PSI-MOD");

		if (!accessions.isEmpty()) {

			if (accessions.size() > 1) {
				throw new IllegalStateException("accession mapped to ids " +accessions+ ": should not have more than one mapping to PSI-MOD");
			}

			return "MOD:" + accessions.get(0);
		}

		return null;
	}
}
