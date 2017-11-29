package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.publication.EntryPublications;

public interface EntryPublicationService {

    EntryPublications findEntryPublications(String entryAccession);
}
