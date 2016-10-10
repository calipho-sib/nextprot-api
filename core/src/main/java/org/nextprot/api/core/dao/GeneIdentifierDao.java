package org.nextprot.api.core.dao;

import java.util.Set;

public interface GeneIdentifierDao {

	/**
	 * @return all gene names found in neXtProt
	 */
	Set<String> findGeneNames();
}
