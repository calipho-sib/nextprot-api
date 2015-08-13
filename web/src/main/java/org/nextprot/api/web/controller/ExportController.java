package org.nextprot.api.web.controller;

import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.service.export.ExportUtils;
import org.nextprot.api.core.service.export.format.EntryBlock;
import org.nextprot.api.core.service.export.format.FileFormat;
import org.nextprot.api.solr.QueryRequest;
import org.nextprot.api.user.domain.UserProteinList;
import org.nextprot.api.user.service.UserProteinListService;
import org.nextprot.api.web.service.ExportService;
import org.nextprot.api.web.service.SearchService;
import org.nextprot.api.web.service.impl.writer.NPEntryWriter;
import org.nextprot.api.web.service.impl.writer.NPEntryWriterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Future;

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

    @ApiMethod(path = "/export/entries/all", verb = ApiVerb.GET, description = "Exports all entries", produces = {MediaType.APPLICATION_XML_VALUE, "text/turtle"})
    @RequestMapping("/export/entries/all")
    public void exportAllEntries(HttpServletResponse response, HttpServletRequest request) {

        FileFormat format = FileFormat.valueOf(request);
        response.setHeader("Content-Disposition", "attachment; filename=\"NXEntries." + format.getExtension() + "\"");

        List<Future<File>> futures = exportService.exportAllEntries(format);
        ExportUtils.printOutput(new LinkedList<>(futures), response);
    }

    @ApiMethod(path = "/export/entries/chromosome/{chromosome}", verb = ApiVerb.GET, description = "Exports the whole chromosome", produces = {MediaType.APPLICATION_XML_VALUE, "text/turtle"})
    @RequestMapping("/export/entries/chromosome/{chromosome}")
    public void exportEntriesByChromosome(HttpServletResponse response, HttpServletRequest request,
                                          @ApiQueryParam(name = "chromosome", description = "The number of the chromosome. For example, the chromosome 21", allowedvalues = {"21"}) @PathVariable("chromosome") String chromosome) {

        FileFormat format = FileFormat.valueOf(request);
        response.setHeader("Content-Disposition", "attachment; filename=\"NXChromosome" + chromosome + "." + format.getExtension() + "\"");

        List<Future<File>> futures = exportService.exportEntriesOfChromosome(chromosome, format);
        ExportUtils.printOutput(new LinkedList<>(futures), response);
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

    @RequestMapping(value = "/export/entries/{view}", method = {RequestMethod.GET})
    public void streamEntriesSubPart(@PathVariable("view") String view, HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "query", required = false) String query,
                                     @RequestParam(value = "listId", required = false) String listId, @RequestParam(value = "queryId", required = false) String queryId,
                                     @RequestParam(value = "sparql", required = false) String sparql, @RequestParam(value = "filter", required = false) String filter,
                                     @RequestParam(value = "sort", required = false) String sort, @RequestParam(value = "order", required = false) String order,
                                     @RequestParam(value = "quality", required = false) String quality, @RequestParam(value = "limit", required = false) Integer limit, Model model) {
        QueryRequest qr = getQueryRequest(query, listId, queryId, sparql, filter, quality, sort, order, limit);

        FileFormat format = FileFormat.valueOf(request);
        streamEntries(format, response, view, qr);
    }

    @RequestMapping(value = "/export/entries", method = {RequestMethod.GET})
    public void streamEntries(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "query", required = false) String query,
                              @RequestParam(value = "listId", required = false) String listId, @RequestParam(value = "queryId", required = false) String queryId,
                              @RequestParam(value = "sparql", required = false) String sparql, @RequestParam(value = "filter", required = false) String filter,
                              @RequestParam(value = "sort", required = false) String sort, @RequestParam(value = "order", required = false) String order,
                              @RequestParam(value = "quality", required = false) String quality, @RequestParam(value = "limit", required = false) Integer limit, Model model) {
        QueryRequest qr = getQueryRequest(query, listId, queryId, sparql, filter, quality, sort, order, limit);

        FileFormat format = FileFormat.valueOf(request);
        streamEntries(format, response, "entry", qr);
    }

    private List<String> getAccessions(QueryRequest queryRequest) {

        Set<String> accessionsSet = searchService.getAccessions(queryRequest);
        List<String> accessions;

        if (queryRequest.getSort() != null || queryRequest.getOrder() != null) {
            //TODO This is very slow and is highly memory intensive please review the way of sorting this using only the asking for ids. See the SearchServiceTest
            accessions = searchService.sortAccessions(queryRequest, accessionsSet);
        } else {
            accessions = new ArrayList<>(accessionsSet);
            Collections.sort(accessions);
        }

        return accessions;
    }

    private void streamEntries(FileFormat format, HttpServletResponse response, String viewName, QueryRequest queryRequest) {

        setResponseHeader(format, viewName, queryRequest, response);
        List<String> entries = getAccessions(queryRequest);

        NPEntryWriter writer = null;

        try {
            writer = NPEntryWriterFactory.newNPEntryStreamWriter(format, response.getOutputStream());

            exportService.streamResults(writer, viewName, entries);
        } catch (IOException e) {
            e.printStackTrace();
            throw new NextProtException(format.getExtension()+" streaming failed: cannot export "+entries.size()+" entries (query="+queryRequest.getQuery()+")");
        } finally {
            try {
                if (writer != null) writer.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new NextProtException("closing "+format.getExtension()+" stream failed: cannot export "+entries.size()+" entries (query="+queryRequest.getQuery()+")");
            }
        }
    }

    private void setResponseHeader(FileFormat format, String viewName, QueryRequest queryRequest, HttpServletResponse response) {

        String filename = getFilename(queryRequest, viewName, format);

        if (!format.equals(FileFormat.JSON)) {
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        }
    }

    private String getFilename(QueryRequest queryRequest, String viewName, FileFormat format) {
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

    private static QueryRequest getQueryRequest(String query, String listId, String queryId, String sparql, String filter, String quality, String sort, String order, Integer limit) {

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

    @RequestMapping(value = "/export/templates", method = {RequestMethod.GET})
    @ResponseBody
    public Map<String, Set<String>> getXMLTemplates() {
        return EntryBlock.getFormatViews();
    }
}
