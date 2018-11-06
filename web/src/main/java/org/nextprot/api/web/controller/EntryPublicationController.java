package org.nextprot.api.web.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.exception.SearchQueryException;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.publication.EntryPublication;
import org.nextprot.api.core.domain.publication.EntryPublicationView;
import org.nextprot.api.core.domain.publication.GlobalPublicationStatistics;
import org.nextprot.api.core.domain.publication.PublicationCategory;
import org.nextprot.api.core.domain.publication.PublicationView;
import org.nextprot.api.core.service.EntryPublicationService;
import org.nextprot.api.core.service.EntryPublicationViewService;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.core.service.StatisticsService;
import org.nextprot.api.solr.query.Query;
import org.nextprot.api.solr.query.SolrService;
import org.nextprot.api.solr.query.dto.QueryRequest;
import org.nextprot.api.solr.query.dto.SearchResult;
import org.nextprot.api.web.service.QueryBuilderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Controller
@Api(name = "Entry Publications", description = "Method to retrieve a publications linked to a neXtProt entry")
public class EntryPublicationController {

	@Autowired
    private EntryPublicationService entryPublicationService;
    @Autowired
    private PublicationService publicationService;
    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private SolrService solrService;
    @Autowired
    private QueryBuilderService queryBuilderService;
    @Autowired
    private EntryPublicationViewService entryPublicationViewService;

	@ApiMethod(path = "/entry-publications/entry/{entry}/category/{category}", verb = ApiVerb.GET, description = "Exports publications associated with a neXtProt entry and a publication category",
			produces = { MediaType.APPLICATION_JSON_VALUE })
	@RequestMapping(value = "/entry-publications/entry/{entry}/category/{category}", method = { RequestMethod.GET })
	@ResponseBody
	public List<EntryPublicationView> getEntryPublicationList(
			@ApiPathParam(name = "entry", description = "The name of the neXtProt entry. For example, the insulin: NX_P01308",  allowedvalues = { "NX_P01308"})
			@PathVariable("entry") String entryName,
			@ApiPathParam(name = "category", description = "publication category (CURATED, SUBMISSION, ADDITIONAL, WEB_RESOURCE, PATENT or ALL)", allowedvalues = { "CURATED" })
			@PathVariable(value = "category") String publicationCategory) {

        String pubCategoryName = publicationCategory.toUpperCase();

		if (PublicationCategory.hasName(pubCategoryName)) {

            return entryPublicationViewService.buildEntryPublicationView(entryName, PublicationCategory.valueOfName(pubCategoryName));
		}

		throw new NextProtException(publicationCategory + ": Unknown publication view");
	}

	@ApiMethod(path = "/entry-publications/entry/{entry}/pubid/{pubid}", verb = ApiVerb.GET, description = "Exports identified publication associated with a neXtProt entry",
			produces = { MediaType.APPLICATION_JSON_VALUE })
	@RequestMapping(value = "/entry-publications/entry/{entry}/pubid/{pubid}", method = { RequestMethod.GET })
	@ResponseBody
	public EntryPublication getEntryPublication(
			@ApiPathParam(name = "entry", description = "The name of the neXtProt entry. For example, the insulin: NX_P01308",  allowedvalues = { "NX_P01308"})
			@PathVariable("entry") String entryName,
			@ApiPathParam(name = "pubid", description = "A publication id", allowedvalues = { "630194" })
			@PathVariable("pubid") long publicationId) {

		return entryPublicationService.findEntryPublications(entryName).getEntryPublication(publicationId);
	}

    @ApiMethod(path = "/entry-publications/entry/{entry}/count", verb = ApiVerb.GET, description = "Count entry publications associated with a neXtProt entry by publication category",
            produces = { MediaType.APPLICATION_JSON_VALUE })
    @RequestMapping(value = "/entry-publications/entry/{entry}/count", method = { RequestMethod.GET })
    @ResponseBody
    public Map<PublicationCategory, Integer> countEntryPublication(
            @ApiPathParam(name = "entry", description = "The name of the neXtProt entry. For example, the insulin: NX_P01308",  allowedvalues = { "NX_P01308"})
            @PathVariable("entry") String entryAccession) {

	    Map<PublicationCategory, Integer> count = new HashMap<>();

	    for (PublicationCategory view : PublicationCategory.values()) {

            count.put(view, entryPublicationService.findEntryPublications(entryAccession).getEntryPublicationList(view).size());
        }

        return count;
    }

    @ApiMethod(path = "/publications/stats", verb = ApiVerb.GET, description = "Get overall statistics over publications",
            produces = { MediaType.APPLICATION_JSON_VALUE })
    @RequestMapping(value = "/publications/stats", method = { RequestMethod.GET })
    @ResponseBody
    public GlobalPublicationStatistics calcGlobalPublicationStats() {

        return statisticsService.getGlobalPublicationStatistics();
    }

    @ApiMethod(path = "/publications/pubid/{pubid}/stats", verb = ApiVerb.GET, description = "Get statistics over all publications linked with a neXtProt entry",
            produces = { MediaType.APPLICATION_JSON_VALUE })
    @RequestMapping(value = "/publications/stats/pubid/{pubid}", method = { RequestMethod.GET })
    @ResponseBody
    public GlobalPublicationStatistics.PublicationStatistics calcPublicationStats(
            @ApiPathParam(name = "pubid", description = "A publication id", allowedvalues = { "630194" })
            @PathVariable("pubid") long publicationId) {

        return statisticsService.getGlobalPublicationStatistics().getPublicationStatistics(publicationId);
    }

    @ApiMethod(path = "/publications/pubids/{statstype}", verb = ApiVerb.GET, description = "Get all publication ids by statistics type",
            produces = { MediaType.APPLICATION_JSON_VALUE })
    @RequestMapping(value = "/publications/pubids/{statstype}", method = { RequestMethod.GET })
    @ResponseBody
    public Set<Long> getPublicationIds(
            @ApiPathParam(name = "statstype", description = "a publication statistics type (ALL, CITED, COMPUTED, CURATED, LARGE_SCALE)", allowedvalues = { "ALL" })
            @PathVariable(value = "statstype") String statisticsType) {

        StatisticsType type = StatisticsType.valueOf(statisticsType.toUpperCase());
        Map<Long, GlobalPublicationStatistics.PublicationStatistics> map = statisticsService.getGlobalPublicationStatistics().getPublicationStatisticsById();

        if (type == StatisticsType.ALL) {
            return map.keySet();
        }

        return map.values().stream()
                .filter(type.getPredicate())
                .map(ps -> ps.getPublicationId())
                .collect(Collectors.toSet());
    }

    @ApiMethod(path = "/entry-publications/pubid/{pubid}", verb = ApiVerb.GET, description = "Exports identified publication associated with neXtProt entries",
            produces = { MediaType.APPLICATION_JSON_VALUE })
    @RequestMapping(value = "/entry-publications/pubid/{pubid}", method = { RequestMethod.GET })
    @ResponseBody
    public PublicationView getEntryPublicationsByPubId(
            @ApiPathParam(name = "pubid", description = "A publication id", allowedvalues = { "630194" })
            @PathVariable("pubid") long publicationId,
            @ApiQueryParam(name = "limit", description = "The maximum number of returned results",  allowedvalues = { "500" })
            @RequestParam(value = "limit", required = false) String limit) {

        List<EntryPublication> eps = publicationService.getEntryPublications(publicationId);

        QueryRequest qr = new QueryRequest();
        qr.setQuality("gold");
        qr.setRows((limit != null) ? limit : "500");

        PublicationView view = new PublicationView();
        view.setPublication(publicationService.findPublicationById(publicationId));
        // return the n first results
        view.addEntryPublicationList(eps.stream()
                .limit(Integer.parseInt(qr.getRows()))
                .collect(Collectors.toList()));

        qr.setEntryAccessionSet(view.getEntryPublicationMap().keySet());

        Query q = queryBuilderService.buildQueryForSearch(qr, "entry");
        try {
            SearchResult searchResult = solrService.executeQuery(q);

            view.setRelatedEntryCount(eps.size());
            searchResult.getResults().forEach(result -> view.putEntrySolrResult(result));

        } catch (SearchQueryException e) {
            throw new NextProtException(e.getMessage());
        }

        return view;
    }

    @ApiMethod(path = "/publication/pubid/{pubid}", verb = ApiVerb.GET, description = "Get the publication",
            produces = { MediaType.APPLICATION_JSON_VALUE })
    @RequestMapping(value = "/publication/pubid/{pubid}", method = { RequestMethod.GET })
    @ResponseBody
    public Publication getPublication(@ApiPathParam(name = "pubid", description = "A publication id", allowedvalues = { "630194" })
                                          @PathVariable("pubid") long publicationId) {

        return publicationService.findPublicationById(publicationId);
    }

    @ApiMethod(path = "/publication/database/{database}/accession/{accession}", verb = ApiVerb.GET, description = "Get a PubMed or DOI publication",
            produces = { MediaType.APPLICATION_JSON_VALUE })
    @RequestMapping(value = "/publication/database/{database}/accession/{accession}", method = { RequestMethod.GET })
    @ResponseBody
    public Publication getPublicationFromAccessionAndDatabase(
            @ApiPathParam(name = "database", description = "A database for publications (PubMed or DOI)", allowedvalues = { "PubMed" })
            @PathVariable("database") String database,
            @ApiPathParam(name = "accession", description = "A publication accession", allowedvalues = { "26487540" })
            @PathVariable("accession") String accession) {

        return publicationService.findPublicationByDatabaseAndAccession(database, accession);
    }

    private enum StatisticsType {

        ALL(publicationStatistics -> true),
        CITED(publicationStatistics -> publicationStatistics.isCited()),
        COMPUTED(publicationStatistics -> publicationStatistics.isComputed()),
        CURATED(publicationStatistics -> publicationStatistics.isCurated()),
        LARGE_SCALE(publicationStatistics -> publicationStatistics.isLargeScale())
        ;

        private final Predicate<GlobalPublicationStatistics.PublicationStatistics> predicate;

        StatisticsType(Predicate<GlobalPublicationStatistics.PublicationStatistics> predicate) {
            this.predicate = predicate;
        }

        public Predicate<GlobalPublicationStatistics.PublicationStatistics> getPredicate() {
            return predicate;
        }
    }
}
