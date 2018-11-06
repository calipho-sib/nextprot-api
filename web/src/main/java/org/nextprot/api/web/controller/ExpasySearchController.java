package org.nextprot.api.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.solr.SolrService;
import org.nextprot.api.solr.dto.Query;
import org.nextprot.api.solr.dto.QueryRequest;
import org.nextprot.api.solr.dto.SearchResult;
import org.nextprot.api.web.service.QueryBuilderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

/**
 * Controller used to retrieve search results for www.expasy.org
 */
@Controller
public class ExpasySearchController {

	private final Log LOGGER = LogFactory.getLog(ExpasySearchController.class);

	@Autowired private SolrService queryService;
	@Autowired private QueryBuilderService queryBuilderService;


	@RequestMapping(value = "/expasy-search", method = { RequestMethod.POST, RequestMethod.GET })
	public String expasySearch(@RequestParam String query, @RequestParam (required = false) String type, Model model, HttpServletResponse response) {

        try {
            QueryRequest qr = new QueryRequest();
            qr.setQuality("gold-and-silver");
            qr.setQuery(query);
            Query bq = queryBuilderService.buildQueryForSearch(qr, "entry");
            SearchResult result = queryService.executeQuery(bq);
            model.addAttribute("count", result.getFound());
            model.addAttribute("url", "https://www.nextprot.org/proteins/search?quality=gold-and-silver&query=" + query);
            model.addAttribute("description", "Entries matching the query " + query + " in neXtProt");

        } catch (NextProtException e){

            LOGGER.error(e.getLocalizedMessage());
            e.printStackTrace();
            response.setStatus(500);
            model.addAttribute("count", -1);
            model.addAttribute("url", "error message " + e.getMessage());

        } catch (Exception e){

            LOGGER.error(e.getLocalizedMessage());
            e.printStackTrace();
            response.setStatus(500);
            model.addAttribute("count", -1);

        }

        return "expasy-search";
	}
}

