package org.nextprot.api.core.service.impl;

import com.google.common.base.Preconditions;
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
import java.util.function.Function;
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

		final Comparator<Publication> comparator = new PublicationComparatorAsc(Publication::getPublicationYear).reversed()
				.thenComparing((pub1, pub2) -> pub1.getPublicationType().compareTo(pub2.getPublicationType()))
				.thenComparing(new PublicationComparatorAsc(Publication::getPublicationLocatorName))
				.thenComparing(new PublicationComparatorAsc(Publication::getVolume, PublicationComparatorAsc.FieldType.NUMBER_TYPE))
				.thenComparing(new PublicationComparatorAsc(Publication::getFirstPage, PublicationComparatorAsc.FieldType.NUMBER_TYPE));

		// sort according to order with criteria defined in publication-sorted-for-master.sql
		Collections.sort(publications, comparator);

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
		} else {
			publication.setAuthors(new TreeSet<>());
			publication.setEditors(new TreeSet<>());
		}
	}

	private void setXrefs(Publication publication, Collection<? extends DbXref> xrefs){
		if (xrefs == null) {
			publication.setDbXrefs(new HashSet<>());						
		} else {
			publication.setDbXrefs(new HashSet<>(xrefs));			
		}
	}

	@Override
	@Cacheable("publications-by-id-and-accession")
	public Publication findPublicationByDatabaseAndAccession(String database, String accession) {
		return publicationDao.findPublicationByDatabaseAndAccession(database, accession);
	}

	/**
	 * Base class for comparing Publications in ascending order of String field
	 */
	private static class PublicationComparatorAsc implements Comparator<Publication> {

		private enum FieldType {

			STRING_TYPE, NUMBER_TYPE
		}

		private final Function<Publication, String> toFieldStringFunc;
		private final FieldType fieldType;

		PublicationComparatorAsc(Function<Publication, String> toFieldStringFunc) {

			this(toFieldStringFunc, FieldType.STRING_TYPE);
		}

		/**
		 * @param toFieldStringFunc accept a Publication and produce a String to be compare
		 * @param fieldType the field type
		 */
		PublicationComparatorAsc(Function<Publication, String> toFieldStringFunc, FieldType fieldType) {

			Preconditions.checkNotNull(toFieldStringFunc);
			Preconditions.checkNotNull(fieldType);
			this.toFieldStringFunc = toFieldStringFunc;
			this.fieldType = fieldType;
		}

		@Override
		public int compare(Publication p1, Publication p2) {

			String stringField1 = toFieldStringFunc.apply(p1);
			String stringField2 = toFieldStringFunc.apply(p2);

			if (Objects.equals(stringField1, stringField2) ) {
				return 0;
			}

			if (stringField1 == null || stringField1.isEmpty()) {
				return 1;
			}

			if (stringField2 == null || stringField2.isEmpty()) {
				return -1;
			}

			return  (fieldType == FieldType.STRING_TYPE) ? doStringComparison(stringField1, stringField2) : doIntComparison(stringField1, stringField2);
		}

		private int doStringComparison(String string1, String string2) {

			return string1.compareTo(string2);
		}

		private int doIntComparison(String string1, String string2) {

			boolean isInt1 = string1.matches("\\d+");
			boolean isInt2 = string2.matches("\\d+");

			// compare ints
			if (isInt1 && isInt2) {

				return Integer.compare(Integer.parseInt(string1), Integer.parseInt(string2));
			}

			else if (isInt1) {

				return -1;
			}

			else if (isInt2) {

				return 1;
			}

			// compare strings
			return doStringComparison(string1, string2);
		}
	}
}
