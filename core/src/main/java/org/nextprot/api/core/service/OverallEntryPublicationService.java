package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.publication.EntryPublication;

import java.util.List;
import java.util.Map;

public interface OverallEntryPublicationService {

    Map<Long, List<EntryPublication>> findAllEntryPublications();
}
