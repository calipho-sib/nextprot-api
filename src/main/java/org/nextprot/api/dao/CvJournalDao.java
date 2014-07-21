package org.nextprot.api.dao;

import java.util.List;

import org.nextprot.api.domain.CvJournal;
import org.nextprot.api.domain.PublicationCvJournal;

public interface CvJournalDao {

	CvJournal findById(Long journalId);
	
	List<CvJournal> findByPublicationId(Long publicationId);
	
	List<PublicationCvJournal> findCvJournalsByPublicationIds(List<Long> publicationIds);
}
