package org.nextprot.api.core.dao;

import org.nextprot.api.core.domain.publication.EntryPublication;

import java.util.List;

public interface EntryPublicationsDao {

    /**
     * Returns the ids of publications belonging to an entry
     * sorted using the 'default algorithm': publication date, type, etc
     *
     * @param entryAccession
     */
    List<Long> findSortedPublicationIds(String entryAccession);

    /**
     * Returns publications belonging to an entry
     * sorted using the 'default algorithm': publication date, type, etc
     *
     * @param entryAccession
     */
    List<EntryPublication> findSortedEntryPublications(String entryAccession);
}
