package org.nextprot.api.core.dao;

import java.util.Map;
import java.util.Set;

public interface GeneIdentifierDao {

	/**
	 * @return all gene names found in neXtProt
	 */
	Set<String> findGeneNames();

	/**
	 * @return all neXtProt entries mapped to gene name(s)
	 */
	Map<String, Set<String>> findEntryGeneNames();
}
