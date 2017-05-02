package org.nextprot.api.core.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface GeneIdentifierDao {

	/**
	 * @return all gene names found in neXtProt
	 */
	Set<String> findGeneNames();

	/**
	 * @return all neXtProt entries mapped to gene name(s) (the first gene name should be the recommended one)
	 */
	Map<String, List<String>> findEntryGeneNames();
}
