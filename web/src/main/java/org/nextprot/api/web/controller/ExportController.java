package org.nextprot.api.web.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.export.ExportService;
import org.nextprot.api.core.service.export.ExportUtils;
import org.nextprot.api.core.service.export.format.NPFileFormat;
import org.nextprot.api.core.service.export.format.NPViews;
import org.nextprot.api.core.service.fluent.FluentEntryService;
import org.nextprot.api.core.utils.NXVelocityUtils;
import org.nextprot.api.rdf.service.SparqlEndpoint;
import org.nextprot.api.rdf.service.SparqlService;
import org.nextprot.api.solr.QueryRequest;
import org.nextprot.api.user.domain.UserProteinList;
import org.nextprot.api.user.domain.UserQuery;
import org.nextprot.api.user.service.UserProteinListService;
import org.nextprot.api.user.service.UserQueryService;
import org.nextprot.api.user.utils.UserQueryUtils;
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
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

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

		NPFileFormat format = getRequestedFormat(request);
		response.setHeader("Content-Disposition", "attachment; filename=\"NXEntries." + format.getExtension() + "\"");

		List<Future<File>> futures = exportService.exportAllEntries(format);
		ExportUtils.printOutput(new LinkedList<Future<File>>(futures), response);

	}

	@ApiMethod(path = "/export/entries/chromosome/{chromosome}", verb = ApiVerb.GET, description = "Exports the whole chromosome", produces = { MediaType.APPLICATION_XML_VALUE, "text/turtle" })
	@RequestMapping("/export/entries/chromosome/{chromosome}")
	public void exportEntriesByChromosome(HttpServletResponse response, HttpServletRequest request,
			@ApiQueryParam(name = "chromosome", description = "The number of the chromosome. For example, the chromosome 21", allowedvalues = { "21" }) @PathVariable("chromosome") String chromosome) {

		NPFileFormat format = getRequestedFormat(request);
		response.setHeader("Content-Disposition", "attachment; filename=\"NXChromosome" + chromosome + "." + format.getExtension() + "\"");

		List<Future<File>> futures = exportService.exportEntriesOfChromossome(chromosome, format);
		ExportUtils.printOutput(new LinkedList<Future<File>>(futures), response);

	}

	@ApiMethod(path = "/export/list/{listId}", verb = ApiVerb.GET, description = "Exports entries from a list")
	@RequestMapping("/export/list/{listId}")
	public void exportList(Model model, HttpServletResponse response, HttpServletRequest request, @ApiQueryParam(name = "listname", description = "The list id") @PathVariable("listId") String listId) {

		UserProteinList pl = this.proteinListService.getUserProteinListById(Long.valueOf(listId));
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

	private QueryRequest getQueryRequest(String match) {

		ObjectMapper mapper = new ObjectMapper();
		QueryRequest queryRequest = null;
		try {
			return mapper.readValue(StringUtils.addQuotesToSimpleJson(match), QueryRequest.class);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	@Autowired
	private UserQueryService userQueryService;
	@Autowired
	private SparqlService sparqlService;
	@Autowired
	private SparqlEndpoint sparqlEndpoint;

	@RequestMapping(value = "/entries/{view}", method = { RequestMethod.GET })
	public void exportEntries(HttpServletRequest request, HttpServletResponse response, @PathVariable("view") String view, @RequestParam(value = "match", required = true) String match,
			@RequestParam(value = "limit", required = false) Integer limit, Model model) {
		QueryRequest qr = getQueryRequest(match);
		exportEntries(request, response, view, qr, limit, model);
	}
	

	@RequestMapping(value = "/entries", method = { RequestMethod.GET })
	public void exportAllEntries(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "match", required = true) String match,
			@RequestParam(value = "limit", required = false) Integer limit, Model model) {
		QueryRequest qr = getQueryRequest(match);
		exportEntries(request, response, "entry", qr, limit, model);
	}
	
	
	private void exportEntries(HttpServletRequest request, HttpServletResponse response, String viewName, QueryRequest queryRequest, Integer limit, Model model){
		
		
		NPFileFormat format = getRequestedFormat(request);
		String fileName = null;


		Set<String> accessions = new HashSet<String>();
		if (queryRequest.hasNextProtQuery()) {
			fileName = "nextprot-query-" + queryRequest.getQueryId() + "-" + viewName + "." + format.getExtension();
			UserQuery uq = userQueryService.getUserQueryById(UserQueryUtils.getUserQueryIdLongFromString(queryRequest.getQueryId()));
			accessions.addAll(sparqlService.findEntries(uq.getSparql(), sparqlEndpoint.getUrl(), uq.getSparql()));
		}else if (queryRequest.hasList()) {
			fileName = "nextprot-list-" + queryRequest.getListId() + "-" + viewName + "." + format.getExtension();
			accessions.addAll(proteinListService.getUserProteinListAccessionItemsById(queryRequest.getListId()));
		}else { // search and add filters ...
		}
		
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

		try {
			response.getWriter().write("\n" + format.getHeader() + "\n");
			int counter = 0;
			for (String acc : accessions) {
				counter++;
				if(limit != null){
					if(counter > limit){
						break;
					}
				}
				
				Entry entry = null;
				if(!viewName.equals("entry")){
					//TODO some incoherance with these 2 names withView and withEverything.getEntry... ???
					entry = fluentEntryService.getNewEntry(acc).withView(viewName);
				}else {
					entry = fluentEntryService.getNewEntry(acc).withEverything().getEntry();
				}
				
				model.addAttribute("entry", entry);
				model.addAttribute("NXUtils", NXVelocityUtils.class);
				model.addAttribute("StringUtils", StringUtils.class);

				View v = viewResolver.resolveViewName("entry", Locale.ENGLISH);
				v.render(model.asMap(), request, response);
			}
			response.getWriter().write(format.getFooter()+ "\n");
		} catch (Exception e) {
			e.printStackTrace();
			throw new NextProtException(e.getMessage());
		}

	}

	@RequestMapping(value = "/export/templates", method = { RequestMethod.GET })
	@ResponseBody
	public Map<String, Set<String>> getXMLTemplates() {
		return NPViews.getFormatViews();
	}

	private NPFileFormat getRequestedFormat(HttpServletRequest request) {
		NPFileFormat format = null;
		String uri = request.getRequestURI();
		if (uri.toLowerCase().endsWith(".ttl")) {
			format = NPFileFormat.TURTLE;
		} else if (uri.toLowerCase().endsWith(".xml")) {
			format = NPFileFormat.XML;
		} else if (uri.toLowerCase().endsWith(".json")) {
			format = NPFileFormat.JSON;
		} else if (uri.toLowerCase().endsWith(".txt")) {
			format = NPFileFormat.TXT;
		} else
			throw new NextProtException("Format not recognized");
		return format;
	}

}
