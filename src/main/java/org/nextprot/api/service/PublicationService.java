package org.nextprot.api.service;

import java.util.List;

import org.nextprot.api.aop.annotation.ValidEntry;
import org.nextprot.api.domain.Publication;


public interface PublicationService {
	
	/**
	 * Gets publication by id
	 * @param id
	 * @return
	 */
	public Publication findPublicationById(long id);
	
	/**
	 * Gets publication by title case insensitive
	 * @param title
	 * @return
	 */
	public List<Publication> findPublicationByTitle(String title);
	
	/**
	 * Retrieves publications by master identifier
	 * @param masterId
	 * @return
	 */
	public List<Publication> findPublicationsByMasterId(Long masterId);
	
	/**
	 * Retrieves publications by master's unique name
	 * @param uniqueName
	 * @return
	 */
	public List<Publication> findPublicationsByMasterUniqueName(@ValidEntry String uniqueName);

	/**
	 * Retrieves publication by MD5
	 * @param uniqueName
	 * @return
	 */
	public Publication findPublicationByMD5(String md5);
	
	
	/**
	 * Retrieves all publications ids
	 * @param uniqueName
	 * @return
	 */	
	public List<Long> findAllPublicationIds();
}
