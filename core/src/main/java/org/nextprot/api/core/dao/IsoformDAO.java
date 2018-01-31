package org.nextprot.api.core.dao;

import org.nextprot.api.core.domain.Isoform;

import java.util.List;
import java.util.Set;

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
	 * For a given master entry, all isoforms synonyms (non-main) are returned
	 * @param entryName the name of the master entry
	 * @return the list of synoyms
	 */
    List<EntityName> findIsoformsSynonymsByEntryName(String entryName); 

    /**
     * Retrieves a list of sets. 
     * Each set contains at least 2 accessions identifying isoforms having the same sequence 
     * @return a list of sets, each set contains isoform accessions
     */
    List<Set<String>> findSetsOfEquivalentIsoforms();
  
    /**
     * Retrieves a list of sets. 
     * Each set contains at least 2 accessions identifying entries having an identical isoform (same sequence = same md5) 
     * @return a list of sets, each set contains entry accessions
     */
    List<Set<String>> findSetsOfEntriesHavingAnEquivalentIsoform();
}
