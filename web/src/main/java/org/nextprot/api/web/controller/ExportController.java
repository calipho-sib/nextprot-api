package org.nextprot.api.web.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.service.export.ExportUtils;
import org.nextprot.api.core.service.export.format.NPFileFormat;
import org.nextprot.api.core.service.export.format.NPViews;
import org.nextprot.api.core.service.fluent.FluentEntryService;
import org.nextprot.api.rdf.service.SparqlEndpoint;
import org.nextprot.api.rdf.service.SparqlService;
import org.nextprot.api.solr.QueryRequest;
import org.nextprot.api.solr.SolrService;
import org.nextprot.api.user.domain.UserProteinList;
import org.nextprot.api.user.domain.UserQuery;
import org.nextprot.api.user.service.UserProteinListService;
import org.nextprot.api.user.service.UserQueryService;
import org.nextprot.api.web.service.ExportService;
import org.nextprot.api.web.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ViewResolver;

/**
 * Controller class responsible to extract in streaming
 * 
 * @author dteixeira
 */
@Lazy
@Controller
// @Api(name = "Export", description =
// "Export multiple entries based on a chromosome or a user list. A template can also be given in order to export only subparts of the entries.")
public class ExportController {

	@Autowired
	private ExportService exportService;

	@Autowired
	private SearchService searchService;

	@Autowired
	private UserProteinListService proteinListService;
	@Autowired
	private ViewResolver viewResolver;
	@Autowired
	private FluentEntryService fluentEntryService;
	@Autowired
	private EntryController entryController;

	@ApiMethod(path = "/export/entries/all", verb = ApiVerb.GET, description = "Exports all entries", produces = { MediaType.APPLICATION_XML_VALUE, "text/turtle" })
	@RequestMapping("/export/entries/all")
	public void exportAllEntries(HttpServletResponse response, HttpServletRequest request) {

		NPFileFormat format = NPFileFormat.valueOf(request);
		response.setHeader("Content-Disposition", "attachment; filename=\"NXEntries." + format.getExtension() + "\"");

		List<Future<File>> futures = exportService.exportAllEntries(format);
		ExportUtils.printOutput(new LinkedList<Future<File>>(futures), response);

	}

	@ApiMethod(path = "/export/entries/chromosome/{chromosome}", verb = ApiVerb.GET, description = "Exports the whole chromosome", produces = { MediaType.APPLICATION_XML_VALUE, "text/turtle" })
	@RequestMapping("/export/entries/chromosome/{chromosome}")
	public void exportEntriesByChromosome(HttpServletResponse response, HttpServletRequest request,
			@ApiQueryParam(name = "chromosome", description = "The number of the chromosome. For example, the chromosome 21", allowedvalues = { "21" }) @PathVariable("chromosome") String chromosome) {

		NPFileFormat format = NPFileFormat.valueOf(request);
		response.setHeader("Content-Disposition", "attachment; filename=\"NXChromosome" + chromosome + "." + format.getExtension() + "\"");

		List<Future<File>> futures = exportService.exportEntriesOfChromossome(chromosome, format);
		ExportUtils.printOutput(new LinkedList<Future<File>>(futures), response);

	}

	@ApiMethod(path = "/export/lists/{listId}", verb = ApiVerb.GET, description = "Exports entries from a list")
	@RequestMapping("/export/lists/{listId}")
	public void exportList(Model model, HttpServletResponse response, HttpServletRequest request, @ApiQueryParam(name = "listname", description = "The list id") @PathVariable("listId") String listId) {

		UserProteinList pl = this.proteinListService.getUserProteinListByPublicId(listId);
		String fileName = pl.getName() + ".txt";

		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

		// TODO should this be secured or not? for now let's say not...
		// 2 ways of doing it: either add the token in the header or generate a
		// secret value for each list that is created

		try {
			if (pl.getDescription() != null) {
				response.getWriter().write("#" + pl.getDescription() + "\n");
			}

			if (pl.getAccessionNumbers() != null) {
				Iterator<String> sIt = pl.getAccessionNumbers().iterator();
				while (sIt.hasNext()) {
					response.getWriter().write(sIt.next());
					if (sIt.hasNext()) {
						response.getWriter().write("\n");
					}

				}
			}

		} catch (Exception e) {
			throw new NextProtException(e.getMessage());
		}

	}

	/*
	 * @ApiMethod(path = "/export/list/{listId}/{view}", verb = ApiVerb.GET,
	 * description = "Exports subpart of entries from a list", produces = {
	 * MediaType.APPLICATION_XML_VALUE })
	 * 
	 * @RequestMapping("/export/list/{listId}/{view}") public void
	 * exportListSubPart(Model model, HttpServletResponse response,
	 * HttpServletRequest request,
	 * 
	 * @ApiQueryParam(name = "The view name", description = "The view name")
	 * @PathVariable("view") String view,
	 * 
	 * @ApiQueryParam(name = "listname", description = "The list id")
	 * @PathVariable("listId") String listId) {
	 * 
	 * NPFileFormat format = getRequestedFormat(request);
	 * 
	 * Set<String> accessions =
	 * this.proteinListService.getUserProteinListAccessionItemsById
	 * (Long.valueOf(listId)); String fileName = "nextprot-list-" + listId + "-"
	 * + view + "." + format.getExtension();
	 * response.setHeader("Content-Disposition", "attachment; filename=\"" +
	 * fileName + "\"");
	 * 
	 * try { response.getWriter().write(format.getHeader()); for (String acc :
	 * accessions) { Entry entry =
	 * fluentEntryService.getNewEntry(acc).withView(view);
	 * model.addAttribute("entry", entry); model.addAttribute("NXUtils", new
	 * NXVelocityUtils()); model.addAttribute("StringUtils", StringUtils.class);
	 * 
	 * View v = viewResolver.resolveViewName("entry", Locale.ENGLISH);
	 * v.render(model.asMap(), request, response); }
	 * response.getWriter().write(format.getFooter()); } catch (Exception e) {
	 * e.printStackTrace(); throw new NextProtException(e.getMessage()); }
	 * 
	 * }
	 */

	private QueryRequest getQueryRequest(String query, String listId, String queryId, String sparql, String filter, String quality, String sort, Integer limit) {
		QueryRequest qr = new QueryRequest();
		qr.setQuery(query);
		if(listId != null){
			qr.setListId(listId);
		}
		qr.setQueryId(queryId);
		qr.setRows(limit.toString());
		qr.setFilter(filter);
		qr.setSort(sort);
		qr.setQuality(quality);
		return qr;
	}

	@Autowired
	private UserQueryService userQueryService;
	@Autowired
	private SparqlService sparqlService;
	@Autowired
	private SparqlEndpoint sparqlEndpoint;
	@Autowired
	private SolrService solrService;

	@RequestMapping(value = "/export/entries/{view}", method = { RequestMethod.GET })
	public void streamEntriesInXMLSubPart(@PathVariable("view") String view, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "query", required = false) String query,
			@RequestParam(value = "listId", required = false) String listId,
			@RequestParam(value = "queryId", required = false) String queryId,
			@RequestParam(value = "sparql", required = false) String sparql,
			@RequestParam(value = "filter", required = false) String filter,
			@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "quality", required = false) String quality,
			@RequestParam(value = "limit", required = false) Integer limit, Model model) {
		QueryRequest qr = getQueryRequest(query, listId, queryId, sparql, filter, quality, sort, limit);
		
		NPFileFormat format = NPFileFormat.valueOf(request);
		if(format.equals(NPFileFormat.XML)){
			exportEntriesInXML(request, response, view, qr, limit, model);
		}else if(format.equals(NPFileFormat.JSON)){
			exportEntriesInJson(request, response, view, qr, limit, model);
		}
	}

	
	@RequestMapping(value = "/export/entries", method = { RequestMethod.GET })
	public void streamEntriesInXML(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "query", required = false) String query,
			@RequestParam(value = "listId", required = false) String listId,
			@RequestParam(value = "queryId", required = false) String queryId,
			@RequestParam(value = "sparql", required = false) String sparql,
			@RequestParam(value = "filter", required = false) String filter,
			@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "quality", required = false) String quality,
			@RequestParam(value = "limit", required = false) Integer limit, Model model) {
		QueryRequest qr = getQueryRequest(query, listId, queryId, sparql, filter, quality, sort, limit);

		NPFileFormat format = NPFileFormat.valueOf(request);
		if(format.equals(NPFileFormat.XML)){
			exportEntriesInXML(request, response, "entry", qr, limit, model);
		}else if(format.equals(NPFileFormat.JSON)){
			exportEntriesInJson(request, response,  "entry", qr, limit, model);
		}
	}

	
	
	private String getFileName(QueryRequest queryRequest, String viewName, NPFileFormat format){
		Set<String> accessions = new HashSet<String>();
		if (queryRequest.hasNextProtQuery()) {
			return "nextprot-query-" + queryRequest.getQueryId() + "-" + viewName + "." + format.getExtension();
		}else if (queryRequest.hasList()) {
			return "nextprot-list-" + queryRequest.getListId() + "-" + viewName + "." + format.getExtension();
		}else  if (queryRequest.getQuery() != null) { // search and add filters ...
			return "nextprot-search-" + queryRequest.getQuery() + "-" + viewName + "." + format.getExtension();
		}else {
			throw new NextProtException("Not implemented yet.");
		}
	}
	
	private Collection<String> getAccessionsFromRequest(QueryRequest queryRequest){

		List<String> accessions = new ArrayList<String>();
		if (queryRequest.hasNextProtQuery()) {
			UserQuery uq = userQueryService.getUserQueryByPublicId(queryRequest.getQueryId()); //For the export we only use public ids
			accessions.addAll(sparqlService.findEntries(uq.getSparql(), sparqlEndpoint.getUrl(), uq.getSparql()));
		}else if (queryRequest.hasList()) {
			accessions.addAll(proteinListService.getUserProteinListByPublicId(queryRequest.getListId()).getAccessionNumbers()); //For the export we only use public ids
		}else  if (queryRequest.getQuery() != null) { // search and add filters ...
			accessions.addAll(searchService.getAssessions(queryRequest));
		}else {
			throw new NextProtException("Not implemented yet.");
		}
		
		if(queryRequest.getRows() != null){
			int limit = Integer.valueOf(queryRequest.getRows());
			return accessions.subList(0, limit);
		}
		return accessions;
		
		/*String queryString = "id:" + (accessions.size() > 1 ? "(" + Joiner.on(" ").join(accessions) + ")" : accessions.iterator().next());
		queryRequest.setQuery(queryString);
		return queryService.buildQueryForSearchIndexes(indexName, "pl_search", queryRequest);*/

	}

	
	private void exportEntriesInXML(HttpServletRequest request, HttpServletResponse response, String viewName, QueryRequest queryRequest, Integer limit, Model model){

		NPFileFormat format = NPFileFormat.valueOf(request);

		String fileName = getFileName(queryRequest, viewName, format);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		Set<String> accessions = new HashSet<String>(getAccessionsFromRequest(queryRequest));
		
		try {
			this.exportService.streamResultsInXML(response.getOutputStream(), viewName, accessions); //should we close the writer or not???
		} catch (IOException e) {
			e.printStackTrace();
			throw new NextProtException("Failed to stream xml data");
		}

	}
	
	private void exportEntriesInJson(HttpServletRequest request, HttpServletResponse response, String viewName, QueryRequest queryRequest, Integer limit, Model model){

		Set<String> accessions = new HashSet<String>(getAccessionsFromRequest(queryRequest));
		try {
			this.exportService.streamResultsInJson(response.getOutputStream(), viewName, accessions); //should we close the writer or not???
		} catch (IOException e) {
			e.printStackTrace();
			throw new NextProtException("Failed to stream json data");
		}

	}

	@RequestMapping(value = "/export/templates", method = { RequestMethod.GET })
	@ResponseBody
	public Map<String, Set<String>> getXMLTemplates() {
		return NPViews.getFormatViews();
	}
}
