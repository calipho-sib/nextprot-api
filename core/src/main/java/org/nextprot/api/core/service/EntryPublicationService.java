package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.publication.EntryPublications;

public interface EntryPublicationService {

    EntryPublications findEntryPublications(String entryAccession);

    /**
     * Retrieves publications by master's unique name filtered by a view
     *
     * @param entryAccession the entry accession
     * @param publicationView the publication view
     * @return a list of Publication
     */
    //List<EntryPublication> findPublicationsByEntryName(String entryAccession, PublicationView publicationView);
}
