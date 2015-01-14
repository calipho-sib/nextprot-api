package org.nextprot.api.web;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.export.ExportService;
import org.nextprot.api.core.service.export.ExportUtils;
import org.nextprot.api.core.service.export.format.*;
import org.nextprot.api.core.service.fluent.FluentEntryService;
import org.nextprot.api.core.utils.NXVelocityUtils;
import org.nextprot.api.user.domain.UserProteinList;
import org.nextprot.api.user.service.UserProteinListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.util.*;
import java.util.concurrent.Future;

/**
 * Controller class responsible to extract in streaming
 * 
 * @author dteixeira
 */
@Lazy
@Controller 
@Api(name = "Export", description = "Export multiple entries based on a chromosome or a user list. A template can also be given in order to export only subparts of the entries.", role = "ROLE_USER")
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
			@ApiParam(name = "chromosome", description = "The number of the chromosome. For example, the chromosome 21", allowedvalues = { "21" }) @PathVariable("chromosome") String chromosome) {

		NPFileFormat format = getRequestedFormat(request);
		response.setHeader("Content-Disposition", "attachment; filename=\"NXChromosome" + chromosome + "." + format.getExtension() + "\"");

		List<Future<File>> futures = exportService.exportEntriesOfChromossome(chromosome, format);
		ExportUtils.printOutput(new LinkedList<Future<File>>(futures), response);

	}

	@ApiMethod(path = "/export/entries/list/{listId}", verb = ApiVerb.GET, description = "Exports entries from a list", produces = { MediaType.APPLICATION_XML_VALUE })
	@RequestMapping("/export/entries/list/{listId}")
	public void exportByListId(HttpServletResponse response, HttpServletRequest request, @ApiParam(name = "listname", description = "The list id") @PathVariable("listId") String listId) {

		UserProteinList proteinList = this.proteinListService.getUserProteinListById(Long.valueOf(listId));
		NPFileFormat format = getRequestedFormat(request);
		response.setHeader("Content-Disposition", "attachment; filename=\"NXEntries." + format.getExtension() + "\"");

		List<Future<File>> futures = exportService.exportEntries(new ArrayList<String>(proteinList.getAccessionNumbers()), getRequestedFormat(request));
		ExportUtils.printOutput(new LinkedList<Future<File>>(futures), response);

	}

	@ApiMethod(path = "/export/list/{listId}", verb = ApiVerb.GET, description = "Exports entries from a list", produces = { MediaType.APPLICATION_XML_VALUE })
	@RequestMapping("/export/list/{listId}")
	public void exportList(Model model, HttpServletResponse response, HttpServletRequest request,
			@ApiParam(name = "listname", description = "The list id") @PathVariable("listId") String listId
	) {
		
	}
	
	@ApiMethod(path = "/export/list/{listId}/{view}", verb = ApiVerb.GET, description = "Exports subpart of entries from a list", produces = { MediaType.APPLICATION_XML_VALUE })
	@RequestMapping("/export/list/{listId}/{view}")
	public void exportListSubPart(Model model, HttpServletResponse response, HttpServletRequest request,
			@ApiParam(name = "The view name", description = "The view name") @PathVariable("view") String view,
			@ApiParam(name = "listname", description = "The list id") @PathVariable("listId") String listId
	) {

		NPFileFormat format = getRequestedFormat(request);
		/*switch (format) {
			case XML:template = ExportXMLTemplate.getTemplate(templateId); break;
			case TXT:template = ExportTXTTemplate.getTemplate(templateId); break;

			default:throw new NextProtException(format + " not supported for templates");
		}*/

		UserProteinList proteinList = this.proteinListService.getUserProteinListById(Long.valueOf(listId));
		String fileName = "nextprot-" + view + "-" + proteinList.getName() + "." + format.getExtension() ;
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

		NPViews npview = NPViews.valueOfViewName(view);
		
		Set<String> accessions = proteinList.getAccessionNumbers();
		try {
			//response.getWriter().write(template.getHeader());
			for (String acc : accessions) {
				Entry entry = fluentEntryService.getNewEntry(acc).withView(npview);
				model.addAttribute("entry", entry);
				model.addAttribute("NXUtils", new NXVelocityUtils());
				model.addAttribute("StringUtils", StringUtils.class);

				View v = viewResolver.resolveViewName(npview.getTemplateName(), Locale.ENGLISH);
				v.render(model.asMap(), request, response);
			}
			//response.getWriter().write(template.getFooter());
		} catch (Exception e) {
			e.printStackTrace();
			throw new NextProtException(e.getMessage());
		}
	}
	

	@RequestMapping(value="/export/templates", method = { RequestMethod.GET })
	@ResponseBody
	public Map<String, List<String>> getXMLTemplates() {
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
