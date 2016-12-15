package org.nextprot.api.commons.service;

import java.util.List;
import java.util.Set;

public interface MasterIdentifierService {

	Long findIdByUniqueName(String uniqueName);
	List<String> findUniqueNamesOfChromosome(String chromosome);
	Set<String> findUniqueNames();

	/**
	 * Should result a single accession most of the time
	 * @param geneNam
	 * @return
	 */
	Set<String> findEntryAccessionByGeneName(String geneName,String synonym);
}
