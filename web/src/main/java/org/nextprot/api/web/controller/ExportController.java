package org.nextprot.api.web.controller;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.nextprot.api.solr.Query;
import org.nextprot.api.solr.QueryRequest;
import org.nextprot.api.solr.SolrService;
import org.nextprot.api.user.domain.UserProteinList;
import org.nextprot.api.user.domain.UserQuery;
import org.nextprot.api.user.service.UserProteinListService;
import org.nextprot.api.user.service.UserQueryService;
import org.nextprot.api.web.service.ExportService;
import org.nextprot.api.web.service.QueryBuilderService;
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

	private static final Log Logger = LogFactory.getLog(ExportController.class);

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

	@Autowired
	private QueryBuilderService queryBuilderService;

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
	 * 
	 * @PathVariable("view") String view,
	 * 
	 * @ApiQueryParam(name = "listname", description = "The list id")
	 * 
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

	private QueryRequest getQueryRequest(String query, String listId, String queryId, String sparql, String filter, String quality, String sort, String order, Integer limit) {

		QueryRequest qr = new QueryRequest();
		qr.setQuery(query);
		if (listId != null) {
			qr.setListId(listId);
		}
		
		if (sparql != null) {
			qr.setSparql(sparql);
		}
		
		qr.setQueryId(queryId);
		qr.setRows("50");
		qr.setFilter(filter);
		qr.setSort(sort);
		qr.setOrder(order);
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
	public void streamEntriesInXMLSubPart(@PathVariable("view") String view, HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "query", required = false) String query,
			@RequestParam(value = "listId", required = false) String listId, @RequestParam(value = "queryId", required = false) String queryId,
			@RequestParam(value = "sparql", required = false) String sparql, @RequestParam(value = "filter", required = false) String filter,
			@RequestParam(value = "sort", required = false) String sort, @RequestParam(value = "order", required = false) String order,
			@RequestParam(value = "quality", required = false) String quality, @RequestParam(value = "limit", required = false) Integer limit, Model model) {
		QueryRequest qr = getQueryRequest(query, listId, queryId, sparql, filter, quality, sort, order, limit);

		NPFileFormat format = NPFileFormat.valueOf(request);
		exportEntries(format, request, response, view, qr, limit, model);

	}

	@RequestMapping(value = "/export/entries", method = { RequestMethod.GET })
	public void streamEntriesInXML(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "query", required = false) String query,
			@RequestParam(value = "listId", required = false) String listId, @RequestParam(value = "queryId", required = false) String queryId,
			@RequestParam(value = "sparql", required = false) String sparql, @RequestParam(value = "filter", required = false) String filter,
			@RequestParam(value = "sort", required = false) String sort, @RequestParam(value = "order", required = false) String order,
			@RequestParam(value = "quality", required = false) String quality, @RequestParam(value = "limit", required = false) Integer limit, Model model) {
		QueryRequest qr = getQueryRequest(query, listId, queryId, sparql, filter, quality, sort, order, limit);

		NPFileFormat format = NPFileFormat.valueOf(request);
		exportEntries(format, request, response, "entry", qr, limit, model);
	}

	private String getFileName(QueryRequest queryRequest, String viewName, NPFileFormat format) {
		if (queryRequest.hasNextProtQuery()) {
			return "nextprot-query-" + queryRequest.getQueryId() + "-" + viewName + "." + format.getExtension();
		} else if (queryRequest.hasList()) {
			return "nextprot-list-" + queryRequest.getListId() + "-" + viewName + "." + format.getExtension();
		} else if (queryRequest.getQuery() != null) { // search and add filters
			return "nextprot-search-" + queryRequest.getQuery() + "-" + viewName + "." + format.getExtension();
		} else if (queryRequest.getSparql() != null) { // search and add filters
			return "nextprot-sparql-" + queryRequest.getSparql() + "-" + viewName + "." + format.getExtension();
		} else {
			throw new NextProtException("Not implemented yet.");
		}
	}

	@Autowired
	private SolrService queryService;

	private void exportEntries(NPFileFormat format, HttpServletRequest request, HttpServletResponse response, String viewName, QueryRequest queryRequest, Integer limit, Model model) {

		// Gets the accessions
		String fileName = getFileName(queryRequest, viewName, format);
		if (format.equals(NPFileFormat.XML)) {
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		}


		Writer writer = null;
		try {

			writer = new OutputStreamWriter(response.getOutputStream(), "UTF-8");

			// write the header if xml only
			if (format.equals(NPFileFormat.XML)) {
				this.exportService.streamResultsInXML(writer, null, null, true, false);
			}
			
			Set<String> accessionsSet = searchService.getAccessions(queryRequest);
			List<String> accessions = null;
			
			if(queryRequest.getSort() != null || queryRequest.getOrder() != null){
				//TODO This is very slow and is highly memory intensive please review the way of sorting this using only the asking for ids. See the SearchServiceTest 
				accessions = searchService.sortAccessions(queryRequest, accessionsSet);
			}else {
				accessions = new ArrayList<String>(accessionsSet);
			}
			
			
			if (format.equals(NPFileFormat.XML)) {
				this.exportService.streamResultsInXML(writer, viewName, accessions, false, false);
			} else if (format.equals(NPFileFormat.JSON)) {
				this.exportService.streamResultsInJson(writer, viewName, accessions);
			} else {
				throw new NextProtException("Format not yet supported");
			}

			// write the footer
			if (format.equals(NPFileFormat.XML)) {
				this.exportService.streamResultsInXML(writer, null, null, false, true);
			}

		} catch (IOException e) {
			e.printStackTrace();
			throw new NextProtException("Failed to stream xml data");
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				throw new NextProtException("Failed to close writer for xml");
			}
		}

	}

	@RequestMapping(value = "/export/templates", method = { RequestMethod.GET })
	@ResponseBody
	public Map<String, Set<String>> getXMLTemplates() {
		return NPViews.getFormatViews();
	}
}
