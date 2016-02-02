package org.nextprot.api.core.service.impl;

import com.google.common.base.Function;
import com.google.common.collect.*;
import org.nextprot.api.commons.dao.MasterIdentifierDao;
import org.nextprot.api.core.dao.AuthorDao;
import org.nextprot.api.core.dao.CvJournalDao;
import org.nextprot.api.core.dao.DbXrefDao;
import org.nextprot.api.core.dao.PublicationDao;
import org.nextprot.api.core.domain.*;
import org.nextprot.api.core.service.DbXrefService;
import org.nextprot.api.core.service.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class PublicationServiceImpl implements PublicationService {

	@Autowired private MasterIdentifierDao masterIdentifierDao;
	@Autowired private PublicationDao publicationDao;
	@Autowired private AuthorDao authorDao;
	@Autowired private DbXrefDao dbXrefDao;
	@Autowired private DbXrefService dbXrefService;
	@Autowired private CvJournalDao cvJournalDao;

	@Cacheable("publications-get-by-id")
	public Publication findPublicationById(long id) {
		Publication publication = this.publicationDao.findPublicationById(id); // Basic fields
		loadAuthorsXrefAndCvJournal(publication); // add non-basic fields to object
		return publication;
	}

	@Override
	public List<Publication> findPublicationByTitle(String title) {
		return publicationDao.findPublicationByTitle(title);
	}
	
	
	/**
	 * TO REMOVE
	 */
	@Override
	public List<Publication> findPublicationsByMasterId(Long masterId) {
		
		List<Publication> publications = this.publicationDao.findSortedPublicationsByMasterId(masterId);
		
		for(Publication publication : publications) {
			loadAuthorsXrefAndCvJournal(publication);			
		}
		
		return publications;
	}

	@Override
	@Cacheable("publications")
	public List<Publication> findPublicationsByMasterUniqueName(String uniqueName) {
		Long masterId = this.masterIdentifierDao.findIdByUniqueName(uniqueName);
		List<Publication> publications = this.publicationDao.findSortedPublicationsByMasterId(masterId);
		
		List<Long> publicationIds = Lists.transform(publications, new Function<Publication, Long>() {
			public Long apply(Publication publication) {
				return publication.getPublicationId();
			}
		});
		
		List<PublicationDbXref> xrefs = this.dbXrefService.findDbXRefByPublicationIds(publicationIds);
		List<PublicationAuthor> authors = this.authorDao.findAuthorsByPublicationIds(publicationIds);
		List<PublicationCvJournal> cvJournals = this.cvJournalDao.findCvJournalsByPublicationIds(publicationIds);
		
		Multimap<Long, PublicationDbXref> xrefMap = Multimaps.index(xrefs, new Function<PublicationDbXref, Long>() {
			@Override
			public Long apply(PublicationDbXref xref) {
				return xref.getPublicationId(); 
			}
		});
		
		Multimap<Long, PublicationAuthor> authorMap = Multimaps.index(authors, new Function<PublicationAuthor, Long>() {
			@Override
			public Long apply(PublicationAuthor author) {
				return author.getPublicationId();
			}
		});

		Map<Long, PublicationCvJournal> journalMap = Maps.uniqueIndex(cvJournals, new Function<PublicationCvJournal, Long>() {
			@Override
			public Long apply(PublicationCvJournal journal) {
				return journal.getPublicationId();
			}
		});

		for (Publication publication : publications) {
			long publicationId = publication.getPublicationId();
			SortedSet<PublicationAuthor> authorSet = new TreeSet<>(authorMap.get(publicationId));

			publication.setEditors(splitEditorsFromAuthors(authorSet));
			publication.setAuthors(authorSet);

			publication.setDbXrefs(new HashSet<DbXref>(xrefMap.get(publicationId)));
			PublicationCvJournal journal = journalMap.get(publicationId);
			if (journal != null)
				publication.setCvJournal(journal);
		}
		
		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<Publication>().addAll(publications).build();
	}


	private SortedSet<PublicationAuthor> splitEditorsFromAuthors(SortedSet<PublicationAuthor> publicationPersons) {

		SortedSet<PublicationAuthor> editors = new TreeSet<>();

		for (PublicationAuthor person : publicationPersons) {

			if (person.isEditor()) {

				editors.add(person);
			}
		}

		publicationPersons.removeAll(editors);

		return editors;
	}

	@Autowired
	public void setPublicationDao(PublicationDao publicationDao) {
		this.publicationDao = publicationDao;
	}

	@Override
	public Publication findPublicationByMD5(String md5) {
		Publication publication = this.publicationDao.findPublicationByMD5(md5);
		loadAuthorsXrefAndCvJournal(publication);
		return publication;
	}


	private void loadAuthorsXrefAndCvJournal(Publication p){
		long publicationId = p.getPublicationId();
		p.setAuthors(new TreeSet<>(this.authorDao.findAuthorsByPublicationId(publicationId)));
		p.setDbXrefs(new HashSet<>(this.dbXrefDao.findDbXRefsByPublicationId(publicationId)));

		List<CvJournal> res = this.cvJournalDao.findByPublicationId(publicationId);
		if(res.size() > 0) p.setCvJournal(res.get(0));		
	}

	@Override
	public List<Long> findAllPublicationIds() {		
		return publicationDao.findAllPublicationsIds();
	}


}
