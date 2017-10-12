package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.publication.GlobalPublicationStatistics;
import org.nextprot.api.core.domain.publication.PublicationView;
import org.nextprot.api.core.service.annotation.ValidEntry;

import java.util.List;


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
	List<Publication> findPublicationsByEntryName(@ValidEntry String uniqueName);

	/**
	 * Retrieves publications by master's unique name filtered by a view
	 *
	 * @param entryAccession the entry accession
	 * @param publicationView the publication view
	 * @return a list of Publication
	 */
	List<Publication> findPublicationsByEntryName(String entryAccession, PublicationView publicationView);

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


	/**
	 * Find publication ids by database and accession.
	 * For example to get a publication from PubMed given its id
	 * @param database PubMed
	 * @param accession 25923089
	 * @return
	 */
	Publication findPublicationByDatabaseAndAccession(String database, String accession);

	GlobalPublicationStatistics countGlobalStatistics();

	boolean isCitedPublication(long publicationId);

	boolean isComputationallyMappedPublication(long publicationId);

	boolean isLargeScalePublication(long publicationId);

	boolean isCuratedPublication(long publicationId);

	/**
	 * Count the number of publication linked to this entry for the given view
	 * @param entryAccession the entry accession
	 * @param publicationView the publication view
	 */
	default int countPublicationsByEntryName(String entryAccession, PublicationView publicationView) {

		return findPublicationsByEntryName(entryAccession, publicationView).size();
	}
}
