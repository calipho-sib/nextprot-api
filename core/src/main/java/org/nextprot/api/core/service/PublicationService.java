package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.publication.EntryPublication;
import org.nextprot.api.core.domain.publication.GlobalPublicationStatistics;
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
     * Retrieves publication by MD5
     * @return
     */
    Publication findPublicationByMD5(String md5);
	
	/**
	 * Retrieves publications by master's unique name
	 * @param uniqueName
	 * @return
	 */
	List<Publication> findPublicationsByEntryName(@ValidEntry String uniqueName);

	/**
	 * Find publication ids by database and accession.
	 * For example to get a publication from PubMed given its id
	 * @param database PubMed
	 * @param accession 25923089
	 */
	Publication findPublicationByDatabaseAndAccession(String database, String accession);

    /**
     * Get publication statistics
     * @param publicationId the publication id
     */
    GlobalPublicationStatistics.PublicationStatistics getPublicationStatistics(long publicationId);

    /**
     * @return the list of associated EntryPublications
     */
    List<EntryPublication> getEntryPublications(long publicationId);

	/**
	 *
	 * @param start start of the list
	 * @param rows  Number of rows from the start of the list
	 * @param entryPublications EntryPublication list
	 * @return A sublist of the given EntryPublication list
	 */
    List<EntryPublication> getEntryPublicationsSublist(List<EntryPublication> entryPublications, int start, int rows);


	/**
	 * Moves the given entry in the top of the list
	 * @param entryPublications
	 * @param entry
	 */
	List<EntryPublication> prioritizeEntry(List<EntryPublication> entryPublications, String entry);

	/**
	 * Adds the generif back links to the publi
	 * @param entryPublications
	 * @param publicationId
	 */
	void addGenerXrefLinks(List<EntryPublication> entryPublications, long publicationId);
}
