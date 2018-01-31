package org.nextprot.api.core.service;

import java.util.List;
import java.util.Set;

public interface MasterIdentifierService {

	Long findIdByUniqueName(String uniqueName);
	List<String> findUniqueNamesOfChromosome(String chromosome);
	Set<String> findUniqueNames();

	/**
	 * Should result a single accession most of the time
	 * @return
	 */
	Set<String> findEntryAccessionByGeneName(String geneName, boolean withSynonyms);

	//List<String> findEntryAccessionsByProteinExistence(ProteinExistence proteinExistence);
}
