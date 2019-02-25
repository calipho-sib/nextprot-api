package org.nextprot.api.core.service;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.exception.ResourceNotFoundException;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.DbXref;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public interface TerminologyService {

	/**
	 * @return a {@code CvTerm} given an accession else return null
	 */
	CvTerm findCvTermByAccession(String accession);

    /**
     * @return a {@code CvTerm} given an accession else throw a runtime exception
     */
	default CvTerm findCvTermByAccessionOrThrowRuntimeException(String term) {

        return Optional.ofNullable(findCvTermByAccession(term))
                .orElseThrow(() -> new ResourceNotFoundException("Term '" +  term + "' not found."));
    }

	/**
	 * @return a list of all {@code CvTerm}s of a given ontology
	 */
	List<CvTerm> findCvTermsByOntology(String ontology);

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

	/**
	 * Get the list of xref(s) accession found for the specific database
	 *
	 * @param cvTermAccession the cvterm accession
	 * @param databaseName the database name of the
	 * @return a xref accessions list
	 */
	default List<String> findCvTermXrefAccessionList(String cvTermAccession, String databaseName) {

		Preconditions.checkNotNull(cvTermAccession);

		CvTerm term = findCvTermByAccessionOrThrowRuntimeException(cvTermAccession);

		List<String> accessions = new ArrayList<>();

		if (term != null && term.getXrefs() != null) {

			return term.getXrefs().stream()
					.filter(xref -> xref.getDatabaseName().equals(databaseName))
					.map(DbXref::getAccession)
					.collect(Collectors.toList());
		}

		return accessions;
	}

	/**
	 * @return the PSI-MOD name of the given cv term or empty if not find
	 */
	Optional<String> findPsiModName(String cvTermAccession);

	/**
	 * @return the PSI-MOD accession of the given cv term or null if not find
	 */
	default Optional<String> findPsiModAccession(String cvTermAccession) {

		List<String> accessions = findCvTermXrefAccessionList(cvTermAccession, "PSI-MOD");

		if (!accessions.isEmpty()) {

			if (accessions.size() > 1) {
				throw new IllegalStateException("accession mapped to ids " +accessions+ ": should not have more than one mapping to PSI-MOD");
			}

			return Optional.of((!accessions.get(0).startsWith("MOD:")) ? "MOD:" + accessions.get(0) : accessions.get(0));
		}

		return Optional.empty();
	}

	List<CvTerm> getAllAncestorTerms(String cvTermAccession);

	/**
	 * Get all ancestors of the given cvterm
	 *
	 * @param cvTermAccession the cvterm accession
	 * @return a list of cvterm ancestor accessions
	 */
	default List<String> getAllAncestorsAccession(String cvTermAccession) {

		return getAllAncestorTerms(cvTermAccession).stream()
				.map(CvTerm::getAccession)
				.collect(Collectors.toList());
	}

	/**
	 * Returns an ordered list of terms.
	 * The first term is the Term identified with the parameter cvTermAccession
	 * The next term is a parent of the previous term until we reach the root term
	 * A known limitation is that only the first parent is retrieved for each term !
	 *
	 * @param cvTermAccession
	 */
	List<CvTerm> getOnePathToRootTerm(String cvTermAccession);
}
