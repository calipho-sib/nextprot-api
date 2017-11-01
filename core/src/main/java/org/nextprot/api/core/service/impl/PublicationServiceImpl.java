package org.nextprot.api.core.service.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.nextprot.api.annotation.builder.statement.dao.SimpleWhereClauseQueryDSL;
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
import org.nextprot.api.core.domain.publication.EntryPublication;
import org.nextprot.api.core.domain.publication.GlobalPublicationStatistics;
import org.nextprot.api.core.domain.publication.PublicationStatistics;
import org.nextprot.api.core.service.DbXrefService;
import org.nextprot.api.core.service.OverallEntryPublicationService;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.core.utils.PublicationComparator;
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
	@Autowired private OverallEntryPublicationService overallEntryPublicationService;


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

	@Override
	@Cacheable("publications")
	public List<Publication> findPublicationsByEntryName(String uniqueName) {

		Long masterId = masterIdentifierDao.findIdByUniqueName(uniqueName);
		List<Publication> publications = publicationDao.findSortedPublicationsByMasterId(masterId);
		Map<Long, List<PublicationDbXref>> npPublicationsXrefs = updateMissingPublicationFields(publications);

		// Getting publications from nx flat database
		List<Publication> nxflatPublications = new ArrayList<>();
		Arrays.asList("PubMed", "DOI").forEach(db -> {

			List<String> referenceIds = this.statementDao.findAllDistinctValuesforFieldWhereFieldEqualsValues(
					StatementField.REFERENCE_ACCESSION,
					new SimpleWhereClauseQueryDSL(StatementField.ENTRY_ACCESSION, uniqueName),
					new SimpleWhereClauseQueryDSL(StatementField.REFERENCE_DATABASE, db));
				nxflatPublications.addAll(getPublicationsFromDBReferenceIds(referenceIds, db, npPublicationsXrefs));

		});


		updateMissingPublicationFields(nxflatPublications);
		publications.addAll(nxflatPublications);

		Comparator<Publication> comparator = PublicationComparator.StringComparator(Publication::getPublicationYear).reversed()
				.thenComparing(Comparator.comparing(Publication::getPublicationType))
				.thenComparing(PublicationComparator.StringComparator(Publication::getPublicationLocatorName))
				.thenComparing(PublicationComparator.FormattedNumberComparator(Publication::getVolume))
				.thenComparing(PublicationComparator.FormattedNumberComparator(Publication::getFirstPage));

		// sort according to order with criteria defined in publication-sorted-for-master.sql
		publications.sort(comparator);

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
	 * @param npPublicationXrefs needed to avoid loading pubmed id publication multiple times
	 * @return
	 */
	private List<Publication> getPublicationsFromDBReferenceIds(List<String> nxflatReferenceIds, String referenceDatabase, Map<Long, List<PublicationDbXref>> npPublicationXrefs) {

		List<Publication> nxflatPublications = new ArrayList<>();

		// Filtering publications which pubmed was not already found in np publications
		List<Long> foundPublicationIds = npPublicationXrefs.keySet().stream()
				.filter(pubid -> npPublicationXrefs.get(pubid).stream().anyMatch(xref -> nxflatReferenceIds.contains(xref.getAccession())))
				.collect(Collectors.toList());

		nxflatReferenceIds.stream()
				.filter(Objects::nonNull)
				.forEach(pubmed -> {
					Publication pub = this.publicationDao.findPublicationByDatabaseAndAccession(referenceDatabase, pubmed);
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

    @Override
    public PublicationStatistics calculatePublicationStatistics(long publicationId) {

	    return new PublicationStatisticsAnalyser(publicationId, overallEntryPublicationService.findAllEntryPublications()
                .getOrDefault(publicationId, new ArrayList<>())).analyse();
    }

    @Override
	public GlobalPublicationStatistics calculateGlobalStatistics() {

        GlobalPublicationStatistics globalPublicationStatistics = new GlobalPublicationStatistics();

        overallEntryPublicationService.findAllEntryPublications().forEach((key, value) -> {

            PublicationStatistics stats = calculatePublicationStatistics(key);

            if (stats.isCited()) {
                globalPublicationStatistics.incrementNumberOfCitedPublications();
            }
            if (stats.isComputed()) {
                globalPublicationStatistics.incrementNumberOfComputationallyMappedPublications();
            }
            if (stats.isLargeScale()) {
                globalPublicationStatistics.incrementNumberOfLargeScalePublications();
            }
            if (stats.isCurated()) {
                globalPublicationStatistics.incrementNumberOfCuratedPublications();
            }
        });

        return globalPublicationStatistics;
	}

	private static class PublicationStatisticsAnalyser {

        private final long publicationId;
        private final List<EntryPublication> entryPublications;

        private PublicationStatisticsAnalyser(long publicationId, List<EntryPublication> entryPublications) {

            this.publicationId = publicationId;
            this.entryPublications = entryPublications;
        }

        private PublicationStatistics analyse() {

            PublicationStatistics publicationStatistics = new PublicationStatistics();
            publicationStatistics.setPublicationId(publicationId);

            publicationStatistics.setCited(isCited());
            publicationStatistics.setComputed(isComputationallyMappedPublication());
            publicationStatistics.setCurated(isManuallyCuratedPublication());
            publicationStatistics.setLargeScale(isLargeScalePublication());

            return publicationStatistics;
        }

        private boolean isCited() {
            return entryPublications.stream()
                    .anyMatch(ep -> ep.isCited());
        }

        /**
         * Rule: Any kind of publication which is never referred in an entry annotation evidence but directly mapped to the entry by and only by PIR
         */
        private boolean isComputationallyMappedPublication() {
            return entryPublications.stream()
                    .allMatch(ep -> ep.isUncited());
        }

        /**
         * Rule: A large scale publication (pf_largescale)
         * Any kind of publication which Is linked to 15 entries or more by directly or by annotation evidences
         */
        private boolean isLargeScalePublication() {
            return entryPublications.size() > 14;
        }

        /**
         * Rule: An article, book, thesis or unpublished observation* that is referred in 1 or more entry annotation
         * evidence(s) or directly mapped to the entry by a NON PIR source
         */
        private boolean isManuallyCuratedPublication() {
            return entryPublications.stream()
                    .allMatch(ep -> ep.isCurated());
        }
    }
}
