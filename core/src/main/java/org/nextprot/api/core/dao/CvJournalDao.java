package org.nextprot.api.core.dao;

import java.util.List;

import org.nextprot.api.core.domain.CvJournal;
import org.nextprot.api.core.domain.PublicationCvJournal;

public interface CvJournalDao {

	CvJournal findById(Long journalId);
	
	List<CvJournal> findByPublicationId(Long publicationId);
	
	List<PublicationCvJournal> findCvJournalsByPublicationIds(List<Long> publicationIds);
}
