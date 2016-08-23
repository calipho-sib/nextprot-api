package org.nextprot.api.tasks.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.tasks.service.SolrIndexingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

//@PreAuthorize("hasRole('ROLE_ADMIN')")
//@ApiAuthBasic(roles={"ROLE_ADMIN"})
@Lazy
@Controller
@Api(name = "Admin tasks controller", description = "Admin task operations", group="Admin")
public class TaskController {
	
	private static final Log LOGGER = LogFactory.getLog(TaskController.class);
	
	@Autowired private SolrIndexingService solrIndexerService; 
	
	@ResponseBody
	@RequestMapping(value = "/tasks/solr/reindex/terminologies", method = { RequestMethod.GET }, produces = {MediaType.TEXT_PLAIN_VALUE})
	@ApiMethod(path = "/tasks/solr/reindex/terminologies", verb = ApiVerb.GET, description = "Rebuilds the sol index for terminologies")
	public String indexTerminologies(HttpServletRequest request) {

		LOGGER.warn("Request to build solr index for terminologies " + request.getRemoteAddr());
		String result;
		try {
			result = solrIndexerService.indexTerminologies();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			result = e.getLocalizedMessage();
		}
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "/tasks/solr/reindex/publications", method = { RequestMethod.GET }, produces = {MediaType.TEXT_PLAIN_VALUE})
	@ApiMethod(path = "/tasks/solr/reindex/publications", verb = ApiVerb.GET, description = "Rebuilds the sol index for publications")
	public String indexPublicationss(HttpServletRequest request) {

		LOGGER.warn("Request to build solr index for terminologies " + request.getRemoteAddr());
		String result;
		try {
			result = solrIndexerService.indexPublications();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			result = e.getLocalizedMessage();
		}
		return result;
	}
	
}
