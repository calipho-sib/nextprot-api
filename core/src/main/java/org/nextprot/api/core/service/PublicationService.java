package org.nextprot.api.core.service;

import java.util.List;

import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.service.annotation.ValidEntry;


public interface PublicationService {
	
	/**
	 * Gets publication by id
	 * @param id
	 * @return
	 */
	Publication findPublicationById(long id);
	
	/**
	 * Gets publication by title case insensitive
	 * @param title
	 * @return
	 */
	List<Publication> findPublicationByTitle(String title);
	
	/**
	 * Retrieves publications by master identifier
	 * @param masterId
	 * @return
	 */
	List<Publication> findPublicationsByMasterId(Long masterId);
	
	/**
	 * Retrieves publications by master's unique name
	 * @param uniqueName
	 * @return
	 */
	List<Publication> findPublicationsByMasterUniqueName(@ValidEntry String uniqueName);

	/**
	 * Retrieves publication by MD5
	 * @return
	 */
	Publication findPublicationByMD5(String md5);
	
	
	/**
	 * Retrieves all publications ids
	 * @return
	 */	
	List<Long> findAllPublicationIds();
}
