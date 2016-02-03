package org.nextprot.api.core.dao;

import org.nextprot.api.core.domain.publication.JournalLocator;

import java.util.List;

public interface JournalLocatorDao {

	List<JournalLocator> findScientificJournalsByPublicationIds(List<Long> publicationIds);
}
