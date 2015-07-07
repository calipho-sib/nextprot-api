package org.nextprot.api.core.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.nextprot.api.commons.dao.MasterIdentifierDao;
import org.nextprot.api.core.dao.AuthorDao;
import org.nextprot.api.core.dao.CvJournalDao;
import org.nextprot.api.core.dao.DbXrefDao;
import org.nextprot.api.core.dao.PublicationDao;
import org.nextprot.api.core.domain.CvJournal;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.PublicationAuthor;
import org.nextprot.api.core.domain.PublicationCvJournal;
import org.nextprot.api.core.domain.PublicationDbXref;
import org.nextprot.api.core.service.DbXrefService;
import org.nextprot.api.core.service.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;


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

		long publicationId = -1;
		for(Publication publication : publications) {
			publicationId = publication.getPublicationId();
			SortedSet<PublicationAuthor> authorSet = new TreeSet<>(authorMap.get(publicationId));
			publication.setAuthors(authorSet);
			publication.setDbXrefs(new HashSet<DbXref>(xrefMap.get(publicationId)));
			publication.setCvJournal(journalMap.get(publicationId));
		}
		
		return publications;
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
