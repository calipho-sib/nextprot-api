package org.nextprot.api.core.dao;

import java.util.List;

import org.nextprot.api.core.domain.Isoform;

/**
 * Returns information about the isoforms and their synonyms
 * @author dteixeira
 *
 */
public interface IsoformDAO {

	/**
	 * For a given master entry all the isoforms are returned (without their synonyms filled)
	 * @param entryName the name of the master entry
	 * @return the list of all isoforms
	 */
	List<Isoform> findIsoformsByEntryName(String entryName);

	/**
	 * For a given master entry, all isoforms sysnomys (non-main) are returned
	 * @param entryName the name of the master entry
	 * @return the list of synoyms
	 */
    List<EntityName> findIsoformsSynonymsByEntryName(String entryName); 

}
