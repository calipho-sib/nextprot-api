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
}
