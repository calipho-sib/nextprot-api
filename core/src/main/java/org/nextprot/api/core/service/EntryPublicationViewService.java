package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.publication.EntryPublicationView;
import org.nextprot.api.core.domain.publication.PublicationCategory;

import java.util.List;

public interface EntryPublicationViewService {

    List<EntryPublicationView> buildEntryPublicationView(String entryAccession, PublicationCategory publicationCategory);
}
