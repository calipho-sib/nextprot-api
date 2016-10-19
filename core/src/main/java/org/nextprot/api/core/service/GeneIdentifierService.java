package org.nextprot.api.core.service;

import java.util.Map;
import java.util.Set;

public interface GeneIdentifierService {

	/**
	 * @return all gene names found in neXtProt
	 */
	Set<String> findGeneNames();

	/**
	 * @return gene names coding the given protein
	 */
	Set<String> findGeneNamesByEntryAccession(String entryAccession);

	/**
	 * @return all neXtProt entries mapped to gene name(s)
	 */
	Map<String, Set<String>> findEntryGeneNames();
}
