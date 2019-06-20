package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.ChromosomalLocation;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface GeneIdentifierService {

	/**
	 * @return all gene names found in neXtProt
	 */
	Set<String> findGeneNames();

	/**
	 * @return gene names coding the given protein (the first gene name should be the recommended one)
	 */
	List<String> findGeneNamesByEntryAccession(String entryAccession);

	/**
	 * @return all neXtProt entries mapped to gene name(s) (the first gene name should be the recommended one)
	 */
	Map<String, List<String>> findEntryGeneNames();

	/**
	 * @return Genes along with the entries by chromosome ID
	 */
	String findEntryGeneNamesByChromosomeLocation(ChromosomalLocation chromosomalLocation);
}
