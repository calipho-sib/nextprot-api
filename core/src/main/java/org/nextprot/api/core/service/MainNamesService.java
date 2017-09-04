package org.nextprot.api.core.service;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.MainNames;

import java.util.Map;
import java.util.Optional;

/**
 * Extracts main names of proteins and isoforms based on isoform_identifier_view
 * 
 * @author pam
 */
public interface MainNamesService {

	Map<String,MainNames> findIsoformOrEntryMainName();

	/**
	 * Extract main names of a given protein or isoform
	 * @param accession isoform or entry accession
	 * @return a MainNames object
	 * @throws NextProtException if accession does not exist
	 */
	default Optional<MainNames> findIsoformOrEntryMainName(String accession) {

		Map<String, MainNames> mainNames = findIsoformOrEntryMainName();

		if (!mainNames.containsKey(accession)) {

			return Optional.empty();
		}

		return Optional.of(mainNames.get(accession));
	}
}
