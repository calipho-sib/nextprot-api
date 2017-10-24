package org.nextprot.api.core.dao;

import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.publication.PublicationDirectLink;
import org.nextprot.api.core.domain.publication.PublicationProperty;

import java.util.List;
import java.util.Map;

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
	List<Publication> findSortedPublicationsByMasterId(Long masterId);
	
	/**
	 * Returns a map with publication id as the key
	 * The value is a Map where the key is a property name, and value a list of string 
	 * @param masterId
	 * @return
	 */
	Map<Long, Map<PublicationProperty, List<PublicationDirectLink>>> findEntryPublicationPropertiesByMasterId(Long masterId);
	
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
	 * Finds publications by title
	 * @param title
	 * @return
	 */
	List<Publication> findPublicationByTitle(String title);
	
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
