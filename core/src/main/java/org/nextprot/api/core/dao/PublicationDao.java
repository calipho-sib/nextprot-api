package org.nextprot.api.core.dao;

import org.nextprot.api.core.domain.Publication;

import java.util.List;

public interface PublicationDao {

	/**
	 * Returns the ids of publications belonging to an entry
	 * sorted using the 'default algorithm': publication date, type, etc
	 * @param masterId
	 * @return
	 */
	List<Long> findSortedPublicationIdsByMasterId(Long masterId);
	
	/**
	 * Returns publications belonging to an entry
	 * sorted using the 'default algorithm': publication date, type, etc
	 * @param masterId
	 * @return
	 */
	// TODO: this method should have a list of longs as parameters
	List<Publication> findSortedPublicationsByMasterId(Long masterId);

	/**
	 * Find publication id by database and accession.
	 * For example to get a publication from PubMed given its id
	 * @param database PubMed
	 * @param accession 25923089
	 * @return
	 */
	Publication findPublicationByDatabaseAndAccession(String database, String accession);
	
	/**
	 * Finds publication by id
	 * @param id
	 * @return
	 */
	Publication findPublicationById(long id);
	
	/**
	 * Retrieves publication by MD5
	 * @return
	 */
	Publication findPublicationByMD5(String md5);

	/**
	 * Retrieves all publications
	 * @return
	 */
	List<Long> findAllPublicationsIds();
	
}
