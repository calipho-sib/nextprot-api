package org.nextprot.api.web;

import java.util.HashSet;
import java.util.Set;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.exception.SearchQueryException;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.rdf.service.SparqlEndpoint;
import org.nextprot.api.rdf.service.SparqlService;
import org.nextprot.api.solr.Query;
import org.nextprot.api.solr.QueryRequest;
import org.nextprot.api.solr.SearchResult;
import org.nextprot.api.solr.SolrConfiguration;
import org.nextprot.api.solr.SolrService;
import org.nextprot.api.user.domain.UserProteinList;
import org.nextprot.api.user.service.UserProteinListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.base.Joiner;

@Lazy
@Controller
@Api(name="Search", description="Method to search")
public class SearchController {

//	private final Log Logger = LogFactory.getLog(SearchController.class);
	@Autowired private SolrService queryService;
	@Autowired private SparqlService sparqlService;
	@Autowired private SparqlEndpoint sparqlEndpoint;

	@Autowired private UserProteinListService proteinListService;
	@Autowired private SolrConfiguration configuration;

	@RequestMapping(value = "/search/{index}", method = { RequestMethod.POST })
	public String search(@PathVariable("index") String indexName, @RequestBody QueryRequest queryRequest, Model model) {
		
		model.addAttribute("StringUtils", StringUtils.class);
		
		if(this.queryService.checkAvailableIndex(indexName)) {
			
			Query query = null;

			if(queryRequest.hasAccs()) {
				Set<String> accessions = new HashSet<String>(queryRequest.getAccs());
				String queryString = "id:" + (accessions.size() > 1 ? "(" + Joiner.on(" ").join(accessions) + ")" : accessions.iterator().next());
				queryRequest.setQuery(queryString);

				query = this.queryService.buildQuery(indexName, "pl_search", queryRequest);
				
			}else if(queryRequest.hasList()) {
				
				UserProteinList proteinList = this.proteinListService.getUserProteinListByNameForUser(queryRequest.getListOwner(), queryRequest.getList());
				Set<String> accessions = proteinList.getAccessionNumbers();

				String queryString = "id:" + (accessions.size() > 1 ? "(" + Joiner.on(" ").join(accessions) + ")" : accessions.iterator().next());
				queryRequest.setQuery(queryString);

				query = this.queryService.buildQuery(indexName, "pl_search", queryRequest);
			}
			else if(queryRequest.hasSparql()) {
				
				Set<String> accessions = new HashSet<String>(sparqlService.findEntries(queryRequest.getSparql(), sparqlEndpoint.getUrl(), queryRequest.getSparqlTitle()));
				
				//In case there is no result
				if(accessions.isEmpty()){
					//There is no entry with NULL value, so the result will be empty, but the result structure will be maintaned (could be replace by SearchResult factory where you create an emptry result)
					accessions.add("NULL"); 
				}
				
				String queryString = "id:" + (accessions.size() > 1 ? "(" + Joiner.on(" ").join(accessions) + ")" : accessions.iterator().next());
				queryRequest.setQuery(queryString);

				query = this.queryService.buildQuery(indexName, "pl_search", queryRequest);

				
			} else {
				query = this.queryService.buildQuery(indexName, "simple", queryRequest);
			}
			
			Logger.info("queryRequest:\n"+queryRequest.toPrettyString());
			
			SearchResult result;
			try {
				result = this.queryService.executeQuery(query);
				model.addAttribute("result", result);
			} catch (SearchQueryException e) {
				e.printStackTrace();
				model.addAttribute("errormessage", e.getMessage());
				return "exception";
			}
			
			return "search";
		} else {
			model.addAttribute("errormessage", "index "+indexName+" not available");
			return "exception";
		}
	}
	
	@ApiMethod(path = "/autocomplete/{index}", verb = ApiVerb.GET, description = "")
	@RequestMapping(value="/autocomplete/{index}", method={RequestMethod.GET, RequestMethod.POST})
	public String autocomplete(
			@ApiQueryParam(name="index", allowedvalues={"entry", "terms", "publication"}, required=true) @PathVariable("index") String indexName, 
			@ApiQueryParam(name="query", description="Search query", required=true) @RequestParam(value="query", required=true) String queryString,
			@ApiQueryParam(name="quality", description="Quality GOLD/BRONZE") @RequestParam(value="quality", required=false) String quality, 
			@ApiQueryParam(name="sort") @RequestParam(value="sort", required=false) String sort,
			@ApiQueryParam(name="order") @RequestParam(value="order", required=false) String order,
			@ApiQueryParam(name="start") @RequestParam(value="start", required=false) String start, 
			@ApiQueryParam(name="rows") @RequestParam(value="rows", required=false) String rows,
			@RequestParam(value="filter", required=false) String filter,
			Model model) {
		
		if(this.queryService.checkAvailableIndex(indexName)) {

			Query q = this.queryService.buildQuery(indexName, "autocomplete", queryString, quality, sort, order, start, "0", filter);
			SearchResult result;
			try {
				result = this.queryService.executeQuery(q);
				model.addAttribute("result", result);
			} catch (SearchQueryException e) {
				e.printStackTrace();
				model.addAttribute("errormessage", e.getMessage());
				return "exception";
			}
			
		}
		return "autocomplete";
	}
	
	
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
					query = this.queryService.buildQuery(indexName, "pl_search", queryRequest);

				} else {
					query = this.queryService.buildQuery(indexName, "simple", queryRequest);
				}
				
				result = this.queryService.executeIdQuery(query);
				model.addAttribute("result", result);

//				result = executeQuery(index, "simple", queryString, quality, sort, order, start, rows, filter, "id");
//				model.addAttribute("result", result);
//				Query query = this.queryService.buildQuery(index, "simple", queryString, quality, null, null, start, rows, null);

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
		
		Query query = this.queryService.buildQuery("entry", "pl_search", queryString, "", sort, order, start, rows, filter);
		
		SearchResult result = this.queryService.executeQuery(query);
		
		model.addAttribute("result", result);
		return "search";
	}
}
