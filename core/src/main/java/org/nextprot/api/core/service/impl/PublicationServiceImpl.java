package org.nextprot.api.core.service.impl;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.*;
import org.nextprot.api.commons.dao.MasterIdentifierDao;
import org.nextprot.api.core.dao.AuthorDao;
import org.nextprot.api.core.dao.DbXrefDao;
import org.nextprot.api.core.dao.PublicationDao;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.PublicationAuthor;
import org.nextprot.api.core.domain.PublicationDbXref;
import org.nextprot.api.core.service.DbXrefService;
import org.nextprot.api.core.service.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class PublicationServiceImpl implements PublicationService {

	private final static Predicate<PublicationAuthor> EDITOR_PREDICATE = new Predicate<PublicationAuthor>() {
		@Override
		public boolean apply(PublicationAuthor contributor) {
			return contributor.isPerson() && contributor.isEditor();
		}
	};

	private final static Predicate<PublicationAuthor> AUTHOR_PREDICATE = new Predicate<PublicationAuthor>() {
		@Override
		public boolean apply(PublicationAuthor contributor) {
			return contributor.isPerson() && !contributor.isEditor();
		}
	};

	@Autowired private MasterIdentifierDao masterIdentifierDao;
	@Autowired private PublicationDao publicationDao;
	@Autowired private AuthorDao authorDao;
	@Autowired private DbXrefDao dbXrefDao;
	@Autowired private DbXrefService dbXrefService;

	@Cacheable("publications-get-by-id")
	public Publication findPublicationById(long id) {
		Publication publication = this.publicationDao.findPublicationById(id); // Basic fields
		loadAuthorsAndXrefs(publication); // add non-basic fields to object
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
			loadAuthorsAndXrefs(publication);
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

		for (Publication publication : publications) {
			long publicationId = publication.getPublicationId();

			setAuthorsEditorsAndXrefs(publication, authorMap.get(publicationId), xrefMap.get(publicationId));
		}
		
		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<Publication>().addAll(publications).build();
	}

	@Autowired
	public void setPublicationDao(PublicationDao publicationDao) {
		this.publicationDao = publicationDao;
	}

	@Override
	public Publication findPublicationByMD5(String md5) {
		Publication publication = this.publicationDao.findPublicationByMD5(md5);
		loadAuthorsAndXrefs(publication);
		return publication;
	}

	@Override
	public List<Long> findAllPublicationIds() {		
		return publicationDao.findAllPublicationsIds();
	}

	private void loadAuthorsAndXrefs(Publication publication){
		long publicationId = publication.getPublicationId();

		setAuthorsEditorsAndXrefs(publication, authorDao.findAuthorsByPublicationId(publicationId), dbXrefDao.findDbXRefsByPublicationId(publicationId));
	}

	/**
	 * Extract editors from authors, set authors, editors and xrefs to publication
	 */
	private void setAuthorsEditorsAndXrefs(Publication publication, Collection<PublicationAuthor> authorsAndEditors, Collection<? extends DbXref> xrefs){

		SortedSet<PublicationAuthor> authorsAndEditorSet = new TreeSet<>(authorsAndEditors);

		publication.setAuthors(Sets.filter(authorsAndEditorSet, AUTHOR_PREDICATE));
		publication.setEditors(Sets.filter(authorsAndEditorSet, EDITOR_PREDICATE));
		publication.setDbXrefs(new HashSet<>(xrefs));
	}
}
