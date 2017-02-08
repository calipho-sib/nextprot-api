package org.nextprot.api.blast.dao;

import java.util.Map;

public interface BlastDAO {

	/**
	 * @return all nextprot sequences mapped by isoform accession
	 */
	Map<String, String> getAllIsoformSequences();
}
