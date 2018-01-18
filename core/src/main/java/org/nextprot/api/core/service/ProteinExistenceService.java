package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.ProteinExistence;

public interface ProteinExistenceService {

	ProteinExistence getProteinExistence(String entryAccession);

	/**
	 * @return the protein existence according to sources
	 */
	ProteinExistence getProteinExistence(String entryAccession, ProteinExistence.Source source);
}
