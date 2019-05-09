package org.nextprot.api.core.service.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import org.apache.log4j.Logger;
import org.nextprot.api.core.dao.PublicationDao;
import org.nextprot.api.core.dao.StatementDao;
import org.nextprot.api.core.dao.impl.StatementSimpleWhereClauseQueryDSL;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.PublicationAuthor;
import org.nextprot.api.core.domain.PublicationDbXref;
import org.nextprot.api.core.domain.publication.EntryPublication;
import org.nextprot.api.core.domain.publication.GlobalPublicationStatistics;
import org.nextprot.api.core.service.AuthorService;
import org.nextprot.api.core.service.DbXrefService;
import org.nextprot.api.core.service.EntryPublicationService;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.core.service.StatisticsService;
import org.nextprot.api.core.service.dbxref.XrefDatabase;
import org.nextprot.api.core.utils.PublicationComparator;
import org.nextprot.commons.statements.StatementField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;


@Service
public class PublicationServiceImpl implements PublicationService {

	private static final Logger LOGGER = Logger.getLogger(PublicationServiceImpl.class);

	@Autowired private MasterIdentifierService masterIdentifierService;
	@Autowired private PublicationDao publicationDao;
	@Autowired private StatementDao statementDao;
	@Autowired private AuthorService authorService;
	@Autowired private DbXrefService dbXrefService;
	@Autowired private StatisticsService statisticsService;
    @Autowired private EntryPublicationService entryPublicationService;

    private Map<Long, List<EntryPublication>> entryPublicationsById;

	@Cacheable(value = "publications-get-by-id", sync = true)
	public Publication findPublicationById(long id) {
		Publication publication = this.publicationDao.findPublicationById(id); // Basic fields
		loadAuthorsAndXrefs(publication); // add non-basic fields to object
		return publication;
	}

    @Override
    public Publication findPublicationByMD5(String md5) {
        Publication publication = this.publicationDao.findPublicationByMD5(md5);
        loadAuthorsAndXrefs(publication);
        return publication;
    }

    // TODO: Publications are already cached in publications-get-by-id - even worse, some publication are linked to more than 10000 entries!!!)
    // almost 5GB of cache here !!!
    @Override
	@Cacheable(value = "publications", sync = true)
	public List<Publication> findPublicationsByEntryName(String uniqueName) {

		Long masterId = masterIdentifierService.findIdByUniqueName(uniqueName);
		List<Publication> publications = publicationDao.findSortedPublicationsByMasterId(masterId);
		Map<Long, List<PublicationDbXref>> npPublicationsXrefs = updateMissingPublicationFields(publications);

		// Getting publications from nx flat database
		List<Publication> nxflatPublications = new ArrayList<>();
		Arrays.asList(XrefDatabase.DOI, XrefDatabase.PUB_MED).forEach(db -> {

			List<String> referenceIds = this.statementDao.findAllDistinctValuesforFieldWhereFieldEqualsValues(
					StatementField.REFERENCE_ACCESSION,
					new StatementSimpleWhereClauseQueryDSL(StatementField.ENTRY_ACCESSION, uniqueName),
					new StatementSimpleWhereClauseQueryDSL(StatementField.REFERENCE_DATABASE, db.getName()));
				nxflatPublications.addAll(getPublicationsFromDBReferenceIds(referenceIds, db.getName(), npPublicationsXrefs));
		});

		updateMissingPublicationFields(nxflatPublications);
		publications.addAll(nxflatPublications);

		Comparator<Publication> comparator = PublicationComparator.StringComparator(Publication::getPublicationYear).reversed()
				.thenComparing(Publication::getPublicationType)
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

		Map<Long, List<PublicationAuthor>> authorMap = authorService.findAuthorsByPublicationIds(publicationIds).stream()
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

	private void loadAuthorsAndXrefs(Publication publication){
		long publicationId = publication.getPublicationId();

		setAuthorsAndEditors(publication, authorService.findAuthorsByPublicationId(publicationId));
		setXrefs(publication, dbXrefService.findDbXRefByPublicationId(publicationId));
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

	private void setXrefs(Publication publication, List<PublicationDbXref> xrefs){
		if (xrefs == null) {
			publication.setDbXrefs(new ArrayList<>());
		} else {
			publication.setDbXrefs(xrefs);
		}
	}

	@Override
	@Cacheable(value = "publications-by-id-and-accession", sync = true)
	public Publication findPublicationByDatabaseAndAccession(String database, String accession) {
		return publicationDao.findPublicationByDatabaseAndAccession(database, accession);
	}

    @Override
    public GlobalPublicationStatistics.PublicationStatistics getPublicationStatistics(long publicationId) {

        return statisticsService.getGlobalPublicationStatistics().getPublicationStatistics(publicationId);
    }

    @Cacheable(value = "entry-publications-by-pubid", sync = true)
    @Override
    public List<EntryPublication> getEntryPublications(long pubId) {

        if (entryPublicationsById == null) {
            entryPublicationsById = buildEntryPublicationsMap();
        }

        return entryPublicationsById.getOrDefault(pubId, new ArrayList<>());
    }

    // Memoized function that returns EntryPublications by publication id
    private Map<Long, List<EntryPublication>> buildEntryPublicationsMap() {

        Map<Long, List<EntryPublication>> map = new HashMap<>();

        for (String entryAccession : masterIdentifierService.findUniqueNames()) {

            Map<Long, EntryPublication> publicationsById = entryPublicationService.findEntryPublications(entryAccession).getEntryPublicationsById();

            for (Map.Entry<Long, EntryPublication> kv : publicationsById.entrySet()) {

                map.computeIfAbsent(kv.getKey(), k -> new ArrayList<>())
                        .add(kv.getValue());
            }
        }

        return map;
    }

    public List<EntryPublication> getEntryPublicationsSublist(List<EntryPublication> eps, int start, int rows ) {
		// Does the paging on entry publications
		if((start > 0 && start < eps.size()) && (start + rows < eps.size())) {
			int endIndex = start + rows;
			return eps.subList(start, endIndex);
		}
		return null;
	}

	/**
	 * Moves up the given entry if exists in the list
	 * @param eps : EntryPublication list
	 */
	public List<EntryPublication> prioritizeEntry(List<EntryPublication> eps, String entry) {
		if(entry != null) {
			// Extracts the selected entry publication
			EntryPublication selectedEntryPublication = eps.stream()
					.filter((entryPublication) -> entryPublication.getEntryAccession().equals(entry))
					.findFirst()
					.orElse(null);

			// Inserts the selected on the top of the list
			if(selectedEntryPublication != null) {
				eps.remove(selectedEntryPublication);
				eps.add(0, selectedEntryPublication);
			}
		}
		return eps;
	}
}
