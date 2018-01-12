package org.nextprot.api.core.service;

public interface ProteinExistenceService {

	/**
	 * @return true if the current entry can upgrade to PE 1 (ProteinExistence.PROTEIN_LEVEL)
	 */
	boolean upgrade(String entryAccession);
}
