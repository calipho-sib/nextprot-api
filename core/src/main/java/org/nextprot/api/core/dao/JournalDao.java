package org.nextprot.api.core.dao;

import org.nextprot.api.core.domain.publication.Journal;

import java.util.List;

public interface JournalDao {

	List<Journal> findJournalsByPublicationIds(List<Long> publicationIds);
}
