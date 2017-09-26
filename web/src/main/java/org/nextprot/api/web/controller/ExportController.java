package org.nextprot.api.web.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.export.format.EntryBlock;
import org.nextprot.api.core.service.export.format.NextprotMediaType;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.utils.annot.export.EntryPartExporterImpl;
import org.nextprot.api.core.utils.annot.export.EntryPartWriter;
import org.nextprot.api.solr.QueryRequest;
import org.nextprot.api.user.domain.UserProteinList;
import org.nextprot.api.user.service.UserProteinListService;
import org.nextprot.api.web.service.StreamEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

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
    private StreamEntryService streamEntryService;

    @Autowired
    private UserProteinListService proteinListService;

    @Autowired
    private EntryBuilderService entryBuilderService;

    @RequestMapping(value = "/export/entries/all", method = {RequestMethod.GET})
    public void streamAllEntries(HttpServletRequest request, HttpServletResponse response) {

        streamEntryService.streamAllEntries(NextprotMediaType.valueOf(request), response);
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
                                     @RequestParam(value = "quality", required = false) String quality) {

        QueryRequest qr = getQueryRequest(query, listId, queryId, sparql, chromosome, filter, quality, sort, order);
        streamEntryService.streamQueriedEntries(qr, NextprotMediaType.valueOf(request), view, response);
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
                              @RequestParam(value = "quality", required = false) String quality) {

        QueryRequest qr = getQueryRequest(query, listId, queryId, sparql, chromosome, filter, quality, sort, order);

        streamEntryService.streamQueriedEntries(qr, NextprotMediaType.valueOf(request), "entry", response);
    }

    @RequestMapping(value = "/export/templates", method = {RequestMethod.GET})
    @ResponseBody
    public Map<String, Set<String>> getXMLTemplates() {
        return EntryBlock.getFormatViews();
    }

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

    @RequestMapping(value = "/export/entry/{entry}/{blockOrSubpart}", method = {RequestMethod.GET})
    public void streamEntrySubPart(HttpServletRequest request, HttpServletResponse response,
                                  @PathVariable("entry") String entryName,
                                  @PathVariable("blockOrSubpart") String blockOrSubpart) {

        Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryName).with(blockOrSubpart).withBed(true));

        try {
            EntryPartWriter writer = EntryPartWriter.valueOf(NextprotMediaType.valueOf(request),
                    EntryPartExporterImpl.fromSubPart(blockOrSubpart),
                    response.getOutputStream());

            writer.write(entry);
        } catch (IOException e) {
            throw new NextProtException("cannot export "+entryName+" "+blockOrSubpart+" in "+NextprotMediaType.valueOf(request)+ " format", e);
        }
    }

    @ApiMethod(path = "/export/chromosome/{chromosome}", verb = ApiVerb.GET, description = "Export all isoforms from neXtProt entries located on a given chromosome in PSI Extended Fasta Format", produces = { NextprotMediaType.PEFF_MEDIATYPE_VALUE } )
    @RequestMapping(value = "/export/chromosome/{chromosome}", method = {RequestMethod.GET}, produces = { NextprotMediaType.PEFF_MEDIATYPE_VALUE })
    public void exportEntriesAsPeffOnChromosome(
            @ApiPathParam(name = "chromosome", description = "The chromosome number or name (X,Y..)",  allowedvalues = { "Y"})
            @PathVariable("chromosome")  String chromosome, HttpServletResponse response) {

        streamEntryService.streamAllChromosomeEntries(chromosome, NextprotMediaType.PEFF, response);
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
