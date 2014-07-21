package org.nextprot.api.dao;

import java.util.List;

import org.nextprot.api.domain.Publication;

public interface PublicationDao {

	/**
	 * Returns the ids of publications belonging to an entry
	 * sorted using the 'default algorithm': publication date, type, etc
	 * @param masterId
	 * @return
	 */
	public List<Long> findSortedPublicationIdsByMasterId(Long masterId);
	
	/**
	 * Returns publications belonging to an entry
	 * sorted using the 'default algorithm': publication date, type, etc
	 * @param masterId
	 * @return
	 */
	public List<Publication> findSortedPublicationsByMasterId(Long masterId);
	
	/**
	 * Finds publication by id
	 * @param id
	 * @return
	 */
	public Publication findPublicationById(long id);

	/**
	 * Finds publications by title
	 * @param title
	 * @return
	 */
	public List<Publication> findPublicationByTitle(String title);
	
	/**
	 * Retrieves publication by MD5
	 * @param uniqueName
	 * @return
	 */
	public Publication findPublicationByMD5(String md5);

	/**
	 * Retrieves all publications
	 * @param uniqueName
	 * @return
	 */	
	public List<Publication> findAllPublications();	

	public List<Long> findAllPublicationsIds();	
	
}
