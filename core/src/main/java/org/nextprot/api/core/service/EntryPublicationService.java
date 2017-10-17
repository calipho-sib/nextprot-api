package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.publication.EntryPublicationReport;

public interface EntryPublicationService {

	EntryPublicationReport reportEntryPublication(String entryAccession);
}
