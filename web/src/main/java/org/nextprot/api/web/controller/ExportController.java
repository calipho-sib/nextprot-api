package org.nextprot.api.web.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.service.export.format.EntryBlock;
import org.nextprot.api.core.service.export.format.NextprotMediaType;
import org.nextprot.api.solr.QueryRequest;
import org.nextprot.api.user.domain.UserProteinList;
import org.nextprot.api.user.service.UserProteinListService;
import org.nextprot.api.web.service.ExportService;
import org.nextprot.api.web.service.SearchService;
import org.nextprot.api.web.service.impl.writer.EntryStreamWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import static org.nextprot.api.web.service.impl.writer.EntryStreamWriter.newAutoCloseableWriter;

/**
 * Controller class responsible to extract in streaming
 *
 * @author dteixeira
 */
@Lazy
@Controller
@Api(name = "Export", description = "Export neXtProt entries ")
public class ExportController {

    @Autowired
    private ExportService exportService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private UserProteinListService proteinListService;

    @Autowired
    private MasterIdentifierService masterIdentifierService;

    @RequestMapping(value = "/export/entries/all", method = {RequestMethod.GET})
    public void streamAllEntries(HttpServletRequest request, HttpServletResponse response, Model model) {
        NextprotMediaType format = NextprotMediaType.valueOf(request);
        streamAllEntries(format, response, "entry");
    }

    @RequestMapping(value = "/export/entries/{view}", method = {RequestMethod.GET})
    public void streamEntriesSubPart(@PathVariable("view") String view, HttpServletRequest request, HttpServletResponse response,
                                     @RequestParam(value = "query", required = false) String query,
                                     @RequestParam(value = "listId", required = false) String listId,
                                     @RequestParam(value = "queryId", required = false) String queryId,
                                     @RequestParam(value = "sparql", required = false) String sparql, 
                                     @RequestParam(value = "chromosome", required = false) String chromosome, 
                                     @RequestParam(value = "filter", required = false) String filter,
                                     @RequestParam(value = "sort", required = false) String sort,
                                     @RequestParam(value = "order", required = false) String order,
                                     @RequestParam(value = "quality", required = false) String quality, Model model) {
        QueryRequest qr = getQueryRequest(query, listId, queryId, sparql, chromosome, filter, quality, sort, order);

        NextprotMediaType format = NextprotMediaType.valueOf(request);
        streamEntries(format, response, view, qr);
    }

    @RequestMapping(value = "/export/entries", method = {RequestMethod.GET})
    public void streamEntries(HttpServletRequest request, HttpServletResponse response,
                              @RequestParam(value = "query", required = false) String query,
    						  @RequestParam(value = "chromosome", required = false) String chromosome, 
    						  @RequestParam(value = "listId", required = false) String listId,
                              @RequestParam(value = "queryId", required = false) String queryId,
                              @RequestParam(value = "sparql", required = false) String sparql,
                              @RequestParam(value = "filter", required = false) String filter,
                              @RequestParam(value = "sort", required = false) String sort,
                              @RequestParam(value = "order", required = false) String order,
                              @RequestParam(value = "quality", required = false) String quality, Model model) {
        QueryRequest qr = getQueryRequest(query, listId, queryId, sparql, chromosome, filter, quality, sort, order);

        NextprotMediaType format = NextprotMediaType.valueOf(request);
        streamEntries(format, response, "entry", qr);
    }

    @RequestMapping(value = "/export/templates", method = {RequestMethod.GET})
    @ResponseBody
    public Map<String, Set<String>> getXMLTemplates() {
        return EntryBlock.getFormatViews();
    }

    //@ApiMethod(path = "/export/lists/{listId}", verb = ApiVerb.GET, description = "Exports entries accessions from a list")
    @RequestMapping("/export/lists/{listId}")
    public void exportList(HttpServletResponse response, HttpServletRequest request, @ApiQueryParam(name = "listname", description = "The list id") @PathVariable("listId") String listId) {

        UserProteinList pl = this.proteinListService.getUserProteinListByPublicId(listId);
        String fileName = pl.getName() + ".txt";

        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        // 2 ways of doing it: either add the token in the header or generate a
        // secret value for each list that is created

        // http://alpha-api.nextprot.org/export/lists/3C5KYA1M
        try {
            if (pl.getDescription() != null) {
                response.getWriter().write("#" + pl.getDescription() + StringUtils.CR_LF);
            }

            if (pl.getAccessionNumbers() != null) {
                for (String s : pl.getAccessionNumbers()) {
                    response.getWriter().write(s);
                    response.getWriter().write(StringUtils.CR_LF);
                }
            }

        } catch (Exception e) {
            throw new NextProtException(e.getMessage(), e);
        }
    }

    // TODO: To re-expose when ChromosomeReport is correctly built and tested !!
    //@ApiMethod(path = "/export/reports/chromosome/{chromosome}", verb = ApiVerb.GET, description = "Export informations of neXtProt entries coming from genes located on a given chromosome",
    //        produces = { MediaType.TEXT_PLAIN_VALUE, NextprotMediaType.TSV_MEDIATYPE_VALUE } )
    @RequestMapping(value = "/export/reports/chromosome/{chromosome}", method = {RequestMethod.GET})
    public void exportChromosomeEntriesReport(
            //@ApiPathParam(name = "chromosome", description = "The chromosome number or name (X,Y..)",  allowedvalues = { "Y"})
            @PathVariable("chromosome")  String chromosome, HttpServletRequest request, HttpServletResponse response) {

        NextprotMediaType mediaType = NextprotMediaType.valueOf(request);

        try (OutputStream os = response.getOutputStream()) {

            String filename = "nextprot_chromosome_" + chromosome + "." + mediaType.getExtension();
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            exportService.exportChromosomeEntryReport(chromosome, NextprotMediaType.valueOf(request), os);
        }
        catch (IOException e) {
            throw new NextProtException(e.getMessage()+": cannot export chromosome "+chromosome+" as "+ mediaType);
        }
    }

    // TODO: To re-expose when ChromosomeReport is correctly built and tested !!
    //@ApiMethod(path = "/export/reports/chromosome/hpp/{chromosome}", verb = ApiVerb.GET, description = "Export informations of neXtProt entries located on a given chromosome by accession",
    //        produces = { MediaType.TEXT_PLAIN_VALUE } )
    @RequestMapping(value = "/export/reports/chromosome/hpp/{chromosome}", method = {RequestMethod.GET})
    public void exportHPPChromosomeEntriesReport(
            //@ApiPathParam(name = "chromosome", description = "The chromosome number or name (X,Y..)",  allowedvalues = { "Y"})
            @PathVariable("chromosome")  String chromosome, HttpServletRequest request, HttpServletResponse response) {

        NextprotMediaType mediaType = NextprotMediaType.valueOf(request);

        try (OutputStream os = response.getOutputStream()) {

            String filename = "HPP_chromosome_" + chromosome + "." + mediaType.getExtension();
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            exportService.exportHPPChromosomeEntryReport(chromosome, NextprotMediaType.valueOf(request), os);
        }
        catch (IOException e) {
            throw new NextProtException(e.getMessage()+": cannot export HPP chromosome "+chromosome+" as "+ mediaType);
        }
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

    private void streamEntries(NextprotMediaType format, HttpServletResponse response, String viewName, QueryRequest queryRequest) {

        setResponseHeader(format, viewName, queryRequest, response);
        List<String> entries = getAccessions(queryRequest);

        try (EntryStreamWriter writer = newAutoCloseableWriter(format, viewName, response.getOutputStream())) {
            exportService.streamResults(writer, viewName, entries);
        }
        catch (IOException e) {
            throw new NextProtException(format.getExtension()+" streaming failed: cannot export "+entries.size()+" entries (query="+queryRequest.getQuery()+")", e);
        }
    }

    private void streamAllEntries(NextprotMediaType format, HttpServletResponse response, String viewName) {

        setResponseHeader(format, response);
        List<String> entries = new ArrayList<>(masterIdentifierService.findUniqueNames());

        try (EntryStreamWriter writer = newAutoCloseableWriter(format, viewName, response.getOutputStream())) {
            exportService.streamResults(writer, viewName, entries);
        }
        catch (IOException e) {
            throw new NextProtException(format.getExtension()+" streaming failed: cannot export "+entries.size()+" entries (all)", e);
        }
    }

    private void setResponseHeader(NextprotMediaType format, String viewName, QueryRequest queryRequest, HttpServletResponse response) {

        String filename = getFilename(queryRequest, viewName, format);

        if (!format.equals(NextprotMediaType.JSON)) {
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        }
    }

    private void setResponseHeader(NextprotMediaType format, HttpServletResponse response) {

        String filename = "nextprot-entries-all"  + "." + format.getExtension();

        if (!format.equals(NextprotMediaType.JSON)) {
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        }
    }

    private String getFilename(QueryRequest queryRequest, String viewName, NextprotMediaType format) {
        if (queryRequest.hasNextProtQuery()) {
            return "nextprot-query-" + queryRequest.getQueryId() + "-" + viewName + "." + format.getExtension();
        } else if (queryRequest.hasList()) {
            return "nextprot-list-" + queryRequest.getListId() + "-" + viewName + "." + format.getExtension();
        } else if (queryRequest.getQuery() != null) { // search and add filters
            return "nextprot-search-" + queryRequest.getQuery() + "-" + viewName + "." + format.getExtension();
        } else if (queryRequest.getSparql() != null) { // search and add filters
            return "nextprot-sparql-" + queryRequest.getSparql() + "-" + viewName + "." + format.getExtension();
        } else if (queryRequest.getChromosome() != null) { // search and add filters
            return "nextprot-chromosome-" + queryRequest.getChromosome() + "-" + viewName + "." + format.getExtension();
        } else {
            throw new NextProtException("Not implemented yet.");
        }
    }

    private static QueryRequest getQueryRequest(String query, String listId, String queryId, String sparql, String chromosome, String filter, String quality, String sort, String order) {

        QueryRequest qr = new QueryRequest();
        qr.setQuery(query);
        if (listId != null) {
            qr.setListId(listId);
        }

        if (sparql != null) {
            qr.setSparql(sparql);
        }
        
        if (chromosome != null) {
            qr.setChromosome(chromosome);
        }

        qr.setQueryId(queryId);
        qr.setRows("50");
        qr.setFilter(filter);
        qr.setSort(sort);
        qr.setOrder(order);
        qr.setQuality(quality);
        return qr;
    }
}
