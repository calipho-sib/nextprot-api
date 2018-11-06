package org.nextprot.api.web.controller;

import com.google.common.base.Joiner;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.exception.SearchQueryException;
import org.nextprot.api.rdf.service.SparqlEndpoint;
import org.nextprot.api.rdf.service.SparqlService;
import org.nextprot.api.solr.AutocompleteSearchResult;
import org.nextprot.api.solr.Query;
import org.nextprot.api.solr.QueryRequest;
import org.nextprot.api.solr.SearchResult;
import org.nextprot.api.solr.SolrCoreRepository;
import org.nextprot.api.solr.SolrService;
import org.nextprot.api.user.domain.UserProteinList;
import org.nextprot.api.user.domain.UserQuery;
import org.nextprot.api.user.service.UserProteinListService;
import org.nextprot.api.user.service.UserQueryService;
import org.nextprot.api.user.service.impl.SparqlQueryDictionary;
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
	@Autowired private SolrCoreRepository solrCoreRepository;
	@Autowired private QueryBuilderService queryBuilderService;

	@Autowired
	private SparqlQueryDictionary sparqlQueryDictionary;
	

	/**
	 * Useful to build the cache for sparql queries on a target api server (typically build-api.nextprot.org)
	 * @param queryId a query public id
	 * @return either the number of entries returned by the queries (if query is a snorql only, the number returned will be 0) or an error message
	 */
	@RequestMapping(value = "/sparql/run", method = { RequestMethod.GET })
	@ResponseBody
	public Map<String,Object> runQuery(@RequestParam(value = "queryId", required = true) String queryId) {
		Map<String,Object> result = new HashMap<String, Object>();
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

	
	@RequestMapping(value = "/search/{index}", method = { RequestMethod.POST })
	@ResponseBody
	public SearchResult search(@PathVariable("index") String indexName, @RequestBody QueryRequest queryRequest) {

		if (this.queryService.checkAvailableIndex(indexName)) {

			Query query = queryBuilderService.buildQueryForSearch(queryRequest, indexName);

			try {
				return queryService.executeQuery(query);
			} catch (SearchQueryException e) {

				throw new NextProtException(e);
			}
		} else {
			throw new NextProtException("error: index " + indexName + " is not available");
		}
	}

	@ApiMethod(path = "/autocomplete/{index}", verb = ApiVerb.GET, description = "")
	@RequestMapping(value="/autocomplete/{index}", method={RequestMethod.GET, RequestMethod.POST})
	public AutocompleteSearchResult autocomplete(
			@ApiQueryParam(name="index", allowedvalues={"entry", "term", "publication"}, required=true) @PathVariable("index") String indexName,
			@ApiQueryParam(name="query", description="Search query", required=true) @RequestParam(value="query", required=true) String queryString,
			@ApiQueryParam(name="quality", description="Quality GOLD/BRONZE") @RequestParam(value="quality", required=false) String quality, 
			@ApiQueryParam(name="sort") @RequestParam(value="sort", required=false) String sort,
			@ApiQueryParam(name="order") @RequestParam(value="order", required=false) String order,
			@ApiQueryParam(name="start") @RequestParam(value="start", required=false) String start, 
			@RequestParam(value="filter", required=false) String filter) {
		
		if (this.queryService.checkAvailableIndex(indexName)) {

			Query q = this.queryBuilderService.buildQueryForAutocomplete(indexName, queryString, quality, sort, order, start, "0", filter);

			try {
				return convert(queryService.executeQuery(q));
			} catch (SearchQueryException e) {
				throw new NextProtException(e);
			}
		} else {
			throw new NextProtException("error: index " + indexName + " is not available");
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

	/**
	 * @param indexName
	 * @param queryRequest
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/search-ids/{index}", method={ RequestMethod.POST })
	public String searchIds(@PathVariable("index") String indexName, @RequestBody QueryRequest queryRequest, Model model) {
		
		if(this.queryService.checkAvailableIndex(indexName)) {
			
			SearchResult result;
			try {
				
				Query query = null;
				
				if((queryRequest.getMode() != null) && queryRequest.getMode().equalsIgnoreCase("advanced")){
					
					Set<String> accessions = new HashSet<String>(sparqlService.findEntries(queryRequest.getSparql(), sparqlEndpoint.getUrl(), queryRequest.getSparqlTitle()));

					String queryString = "id:" + (accessions.size() > 1 ? "(" + Joiner.on(" ").join(accessions) + ")" : accessions.iterator().next());
					queryRequest.setQuery(queryString);
					query = this.queryBuilderService.buildQueryForSearchIndexes(indexName, "pl_search", queryRequest);

				} else {
					query = this.queryBuilderService.buildQueryForSearchIndexes(indexName, "simple", queryRequest);

				}
				
				result = this.queryService.executeIdQuery(query);
				model.addAttribute("SearchResult", SearchResult.class);
				model.addAttribute("result", result);

			} catch (SearchQueryException e) {
				e.printStackTrace();
				model.addAttribute("errormessage", e.getMessage());
				return "exception";
			}
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
			Model model) throws SearchQueryException {
		
		UserProteinList proteinList = this.proteinListService.getUserProteinListByNameForUser(username, listName);
		Set<String> accessions = proteinList.getAccessionNumbers();
		
		String queryString = "id:" + ( accessions.size() > 1 ? "(" + Joiner.on(" ").join(accessions) + ")" : accessions.iterator().next() );
		
//		SolrIndex index = this.configuration.getIndexByName("entry");
		
		Query query = this.queryBuilderService.buildQueryForProteinLists("entry", queryString, "", sort, order, start, rows, filter);
		
		SearchResult result = this.queryService.executeQuery(query);
		
		model.addAttribute("result", result);
		return "search";
	}
}
