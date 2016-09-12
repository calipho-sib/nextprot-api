package org.nextprot.api.core.service.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.nextprot.api.annotation.builder.statement.dao.StatementDao;
import org.apache.log4j.Logger;
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
import org.nextprot.commons.statements.StatementField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class PublicationServiceImpl implements PublicationService {

	private static final Logger LOGGER = Logger.getLogger(PublicationServiceImpl.class);

	@Autowired private MasterIdentifierDao masterIdentifierDao;
	@Autowired private PublicationDao publicationDao;
	@Autowired private StatementDao statementDao;
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

		publications.forEach(this::loadAuthorsAndXrefs);
		
		return publications;
	}

	@Override
	@Cacheable("publications")
	public List<Publication> findPublicationsByMasterUniqueName(String uniqueName) {

		List<Publication> publications = publicationDao.findSortedPublicationsByMasterId(masterIdentifierDao.findIdByUniqueName(uniqueName));
		Map<Long, List<PublicationDbXref>> npPublicationsXrefs = updateMissingPublicationFields(publications);

		// Getting publications from nx flat database
		List<String> nxFlatPubmedIds = this.statementDao.
				findAllDistinctValuesforFieldWhereFieldEqualsValues(StatementField.REFERENCE_ACCESSION,
						StatementField.ENTRY_ACCESSION, uniqueName, StatementField.REFERENCE_DATABASE, "PubMed");

		List<Publication> nxflatPublications = getPublicationsFromPubmedIds(nxFlatPubmedIds, npPublicationsXrefs);
		updateMissingPublicationFields(nxflatPublications);

		publications.addAll(nxflatPublications);

		// sort according to order with criteria defined in publication-sorted-for-master.sql
		Collections.sort(publications, (p1, p2) ->
				new PublicationYearComparatorDesc()
							.thenComparing((pub1, pub2) -> pub1.getPublicationType().compareTo(pub2.getPublicationType()))
							.thenComparing(new PublicationJournalNameComparatorAsc())
							.thenComparing(new PublicationVolumeComparatorAsc())
							.thenComparing(new PublicationFirstPageComparatorAsc())
							.compare(p1, p2)
		);

		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<Publication>().addAll(publications).build();
	}

	private Map<Long, List<PublicationDbXref>> updateMissingPublicationFields(List<Publication> publications) {

		List<Long> publicationIds = publications.stream().map(Publication::getPublicationId).collect(Collectors.toList());

		Map<Long, List<PublicationAuthor>> authorMap = authorDao.findAuthorsByPublicationIds(publicationIds).stream()
				.collect(Collectors.groupingBy(PublicationAuthor::getPublicationId));

		Map<Long, List<PublicationDbXref>> xrefMap = dbXrefService.findDbXRefByPublicationIds(publicationIds).stream()
				.collect(Collectors.groupingBy(PublicationDbXref::getPublicationId));

		for (Publication publication : publications) {

			setAuthorsAndEditors(publication, authorMap.get(publication.getPublicationId()));
			setXrefs(publication, xrefMap.get(publication.getPublicationId()));
		}

		return xrefMap;
	}

	/**
	 * Get all publications not found in npPublication from pubmedids
	 * @param nxflatPubmedIds entry name
	 * @param npPublicationXrefs needed to avoid loading pubmed id publication multiple times
	 * @return
	 */
	private List<Publication> getPublicationsFromPubmedIds(List<String> nxflatPubmedIds, Map<Long, List<PublicationDbXref>> npPublicationXrefs) {

		List<Publication> nxflatPublications = new ArrayList<>();

		// Filtering publications which pubmed was not already found in np publications
		List<Long> foundPublicationIds = npPublicationXrefs.keySet().stream()
				.filter(pubid -> npPublicationXrefs.get(pubid).stream().anyMatch(xref -> nxflatPubmedIds.contains(xref.getAccession())))
				.collect(Collectors.toList());

		nxflatPubmedIds.stream()
				.filter(pubmed -> pubmed != null)
				.forEach(pubmed -> {
					Publication pub = this.publicationDao.findPublicationByDatabaseAndAccession("PubMed", pubmed);
						if (pub == null) {
							LOGGER.warn("Pubmed " + pubmed + " cannot be found");
						} else if (!foundPublicationIds.contains(pub.getPublicationId())) {
							nxflatPublications.add(pub);
						}
					}
				);

		return nxflatPublications;
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

		setAuthorsAndEditors(publication, authorDao.findAuthorsByPublicationId(publicationId));
		setXrefs(publication, dbXrefDao.findDbXRefsByPublicationId(publicationId));
	}

	/**
	 * Extract editors from authors then set authors, editors
	 */
	private void setAuthorsAndEditors(Publication publication, Collection<PublicationAuthor> authorsAndEditors) {

		if (authorsAndEditors != null) {
			Set<PublicationAuthor> authorsAndEditorSet = new TreeSet<>(authorsAndEditors);

			publication.setAuthors(new TreeSet<>(Sets.filter(authorsAndEditorSet, pa -> pa != null && !pa.isEditor())));
			publication.setEditors(new TreeSet<>(Sets.filter(authorsAndEditorSet, pa -> pa != null && pa.isEditor())));
		}
	}

	private void setXrefs(Publication publication, Collection<? extends DbXref> xrefs){

		if (xrefs != null) publication.setDbXrefs(new HashSet<>(xrefs));
	}

	@Override
	@Cacheable("publications-by-id-and-accession")
	public Publication findPublicationByDatabaseAndAccession(String database, String accession) {
		return publicationDao.findPublicationByDatabaseAndAccession(database, accession);
	}

	// TODO: refactor those comparators (fred)
	private static class PublicationYearComparatorDesc implements Comparator<Publication> {

		@Override
		public int compare(Publication p1, Publication p2) {

			String year1 = p1.getPublicationYear();
			String year2 = p2.getPublicationYear();

			if (Objects.equals(year1, year2) ) {
				return 0;
			}

			if (year1 == null || year1.isEmpty()) {
				return 1;
			}

			if (year2 == null || year2.isEmpty()) {
				return -1;
			}

			return year2.compareTo(year1);
		}
	}

	private static class PublicationJournalNameComparatorAsc implements Comparator<Publication> {

		@Override
		public int compare(Publication p1, Publication p2) {

			String name1 = p1.getPublicationLocatorName();
			String name2 = p2.getPublicationLocatorName();

			if (Objects.equals(name1, name2) ) {
				return 0;
			}

			if (name1 == null || name1.isEmpty()) {
				return 1;
			}

			if (name2 == null || name2.isEmpty()) {
				return -1;
			}

			return name1.compareTo(name2);
		}
	}

	private static class PublicationVolumeComparatorAsc implements Comparator<Publication> {

		@Override
		public int compare(Publication p1, Publication p2) {

			String vol1 = p1.getVolume();
			String vol2 = p2.getVolume();

			if (Objects.equals(vol1, vol2) ) {
				return 0;
			}

			if (vol1 == null || vol1.isEmpty()) {
				return 1;
			}

			if (vol2 == null || vol2.isEmpty()) {
				return -1;
			}

			if (vol1.matches("\\d+") && vol2.matches("\\d+")) {

				return Integer.compare(Integer.parseInt(vol1), Integer.parseInt(vol2));
			}

			return vol1.compareTo(vol2);
		}
	}

	private static class PublicationFirstPageComparatorAsc implements Comparator<Publication> {

		@Override
		public int compare(Publication p1, Publication p2) {

			String firstPage1 = p1.getFirstPage();
			String firstPage2 = p2.getFirstPage();

			if (Objects.equals(firstPage1, firstPage2) ) {
				return 0;
			}

			if (firstPage1 == null || firstPage1.isEmpty()) {
				return 1;
			}

			if (firstPage2 == null || firstPage2.isEmpty()) {
				return -1;
			}

			if (firstPage1.matches("\\d+") && firstPage2.matches("\\d+")) {

				return Integer.compare(Integer.parseInt(firstPage1), Integer.parseInt(firstPage2));
			}

			return firstPage1.compareTo(firstPage2);
		}
	}
}
