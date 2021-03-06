package org.nextprot.api.web.controller;

import com.google.common.base.Joiner;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.rdf.service.SparqlEndpoint;
import org.nextprot.api.rdf.service.SparqlService;
import org.nextprot.api.solr.core.Entity;
import org.nextprot.api.solr.query.Query;
import org.nextprot.api.solr.query.QueryConfiguration;
import org.nextprot.api.solr.query.QueryMode;
import org.nextprot.api.solr.query.dto.AutocompleteSearchResult;
import org.nextprot.api.solr.query.dto.QueryRequest;
import org.nextprot.api.solr.query.dto.SearchResult;
import org.nextprot.api.solr.service.SolrService;
import org.nextprot.api.user.domain.UserProteinList;
import org.nextprot.api.user.domain.UserQuery;
import org.nextprot.api.user.service.UserProteinListService;
import org.nextprot.api.user.service.UserQueryService;
import org.nextprot.api.web.service.QueryBuilderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Lazy
@Controller
//@Api(name="Search", description="Method to search")
public class SearchController {

//	private final Log Logger = LogFactory.getLog(SearchController.class);
	@Autowired private SolrService queryService;
	@Autowired private SparqlService sparqlService;
	@Autowired private SparqlEndpoint sparqlEndpoint;

	@Autowired private UserQueryService userQueryService;
	@Autowired private UserProteinListService proteinListService;
	@Autowired private QueryBuilderService queryBuilderService;

	/**
	 * Useful to build the cache for sparql queries on a target api server (typically build-api.nextprot.org)
	 * @param queryId a query public id
	 * @return either the number of entries returned by the queries (if query is a snorql only, the number returned will be 0) or an error message
	 */
	@RequestMapping(value = "/sparql/run", method = { RequestMethod.GET })
	@ResponseBody
	public Map<String,Object> runQuery(@RequestParam(value = "queryId", required = true) String queryId) {
		Map<String,Object> result = new HashMap<>();
		result.put("queryId", queryId);
		try {
			UserQuery uq = userQueryService.getUserQueryByPublicId(queryId);
			List<String> entries = sparqlService.findEntries(uq.getSparql(), sparqlEndpoint.getUrl(), uq.getTitle());
			result.put("entryCount", entries.size());
			
		} catch (Exception e) {
			result.put("error", e.getMessage());
		}
		return result;
	}

	
	@RequestMapping(value = "/search/{entity}", method = { RequestMethod.POST })
	@ResponseBody
	public SearchResult search(@PathVariable("entity") String entityName, @RequestBody QueryRequest queryRequest) {

		Entity entity = Entity.valueOfName(entityName);

		if (this.queryService.checkSolrCore(entity, queryRequest.getQuality())) {

			Query query = queryBuilderService.buildQueryForSearch(queryRequest, entity);

			try {
				return queryService.executeQuery(query);
			} catch (QueryConfiguration.MissingSortConfigException e) {

				throw new NextProtException(e);
			}
		} else {
			throw new NextProtException("error: entity " + entityName + " is not available");
		}
	}

	@ApiMethod(path = "/autocomplete/{entity}", verb = ApiVerb.GET, description = "")
	@RequestMapping(value="/autocomplete/{entity}", method={RequestMethod.GET, RequestMethod.POST})
	public AutocompleteSearchResult autocomplete(
			@ApiQueryParam(name="entity", allowedvalues={"entry", "term", "publication"}, required=true) @PathVariable("entity") String entityName,
			@ApiQueryParam(name="query", description="Search query", required=true) @RequestParam(value="query", required=true) String queryString,
			@ApiQueryParam(name="quality", description="Quality GOLD/BRONZE") @RequestParam(value="quality", required=false) String quality, 
			@ApiQueryParam(name="sort") @RequestParam(value="sort", required=false) String sort,
			@ApiQueryParam(name="order") @RequestParam(value="order", required=false) String order,
			@ApiQueryParam(name="start") @RequestParam(value="start", required=false) String start, 
			@RequestParam(value="filter", required=false) String filter) {

		Entity entity = Entity.valueOfName(entityName);

		if (this.queryService.checkSolrCore(entity, quality)) {

			Query q = this.queryBuilderService.buildQueryForAutocomplete(entity, queryString, quality, sort, order, start, "0", filter);

			try {
				return convert(queryService.executeQuery(q));
			} catch (QueryConfiguration.MissingSortConfigException e) {
				throw new NextProtException(e);
			}
		} else {
			throw new NextProtException("error: entity " + entityName + " is not available");
		}
	}

	private static AutocompleteSearchResult convert(SearchResult searchResult) {

		AutocompleteSearchResult autocompleteResult = new AutocompleteSearchResult();

		autocompleteResult.setElapsedTime(searchResult.getElapsedTime());
		autocompleteResult.setEntity(searchResult.getEntity());
		autocompleteResult.setIndex(searchResult.getIndex());

		Map<String, List<Map<String, Object>>> facets = searchResult.getFacets();

		for (List<Map<String, Object>> value : facets.values()) {

			for (Map<String, Object> map : value) {
				autocompleteResult.addResult((String) map.get("name"), ((Long) map.get("count")).intValue());
			}
		}

		return autocompleteResult;
	}

	@RequestMapping(value="/search-ids/{entity}", method={ RequestMethod.POST })
	public String searchIds(@PathVariable("entity") String entityName, @RequestBody QueryRequest queryRequest, Model model) {

		Entity entity = Entity.valueOfName(entityName);

		if (this.queryService.checkSolrCore(entity, queryRequest.getQuality())) {
			
			SearchResult result;

			Query query;

			if((queryRequest.getMode() != null) && queryRequest.getMode().equalsIgnoreCase("advanced")){

				Set<String> accessions = new HashSet<>(sparqlService.findEntries(queryRequest.getSparql(), sparqlEndpoint.getUrl(), queryRequest.getSparqlTitle()));

				String queryString = "id:" + (accessions.size() > 1 ? "(" + Joiner.on(" ").join(accessions) + ")" : accessions.iterator().next());
				queryRequest.setQuery(queryString);
				query = this.queryBuilderService.buildQueryForSearchIndexes(entity, QueryMode.PROTEIN_LIST_SEARCH, queryRequest);

			} else {
				query = this.queryBuilderService.buildQueryForSearchIndexes(entity, QueryMode.SIMPLE, queryRequest);

			}

			result = this.queryService.executeIdQuery(query);
			model.addAttribute("SearchResult", SearchResult.class);
			model.addAttribute("result", result);
		}
		
		return "search-ids";
	}
	
	@RequestMapping(value="/user/{username}/protein-list/{list}/results", method = RequestMethod.GET)
	public String test(@PathVariable("username") String username,
			@PathVariable("list") String listName, 
			@RequestParam(value="sort", required=false) String sort,
			@RequestParam(value="order", required=false) String order,
			@RequestParam(value="start", required=false) String start,
			@RequestParam(value="rows", required=false) String rows,
			@RequestParam(value="filter", required=false) String filter,
			Model model) throws QueryConfiguration.MissingSortConfigException {
		
		UserProteinList proteinList = this.proteinListService.getUserProteinListByNameForUser(username, listName);
		Set<String> accessions = proteinList.getAccessionNumbers();
		
		String queryString = "id:" + ( accessions.size() > 1 ? "(" + Joiner.on(" ").join(accessions) + ")" : accessions.iterator().next() );
		
//		SolrIndex index = this.configuration.getIndexByName("entry");
		
		Query query = this.queryBuilderService.buildQueryForProteinLists(Entity.Entry, queryString, "", sort, order, start, rows, filter);
		
		SearchResult result = this.queryService.executeQuery(query);
		
		model.addAttribute("result", result);
		return "search";
	}
}
