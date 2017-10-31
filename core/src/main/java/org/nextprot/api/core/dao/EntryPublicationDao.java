package org.nextprot.api.core.dao;

import org.nextprot.api.core.domain.publication.EntryPublication;
import org.nextprot.api.core.domain.publication.PublicationDirectLink;

import java.util.List;

public interface EntryPublicationDao {

    EntryPublication buildEntryPublication(String entryAccession, long publicationId);

    List<PublicationDirectLink> findPublicationDirectLinkList(String entryAccession, long pubId);
}
