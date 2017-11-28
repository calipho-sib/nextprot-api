package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.publication.EntryPublication;

import java.util.List;

public interface EntryPublicationListService {

    /**
     * @return the list of associated EntryPublications
     */
    List<EntryPublication> getEntryPublicationListByPubId(long pubId);
}
