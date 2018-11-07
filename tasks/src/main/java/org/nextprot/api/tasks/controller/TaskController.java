package org.nextprot.api.tasks.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.solr.service.SolrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Lazy
@Controller
@Api(name = "Solr indexing tasks", description = "Solr indexing operations", group="Task")
public class TaskController {
	
	private static final Log LOGGER = LogFactory.getLog(TaskController.class);
	
	@Autowired private SolrService solrService;
	
	@ResponseBody
	@RequestMapping(value = "/tasks/solr/{indexname}/index/chromosome/{chrname}", method = { RequestMethod.GET }, produces = {MediaType.TEXT_PLAIN_VALUE})
	@ApiMethod(path = "/tasks/solr/{indexname}/index/chromosome/{chrname}", verb = ApiVerb.GET, description = "Add entries of a chromosome to the  entries or gold-entries solr index")
	public String addChromosomeEntryToEntryIndex(
			@ApiPathParam(name = "indexname", description = "The name of of an entry index: entries or gold-entries",  allowedvalues = { "gold-entries"})
			@PathVariable("indexname") String indexName, 
			@ApiPathParam(name = "chrname", description = "The name of of chromosome, example: MT, Y, unknown, 12...",  allowedvalues = { "MT"})
			@PathVariable("chrname") String chrName, HttpServletRequest request) {

		LOGGER.warn("Request to add entries of chromosome " + chrName + " to index " + indexName + " from " + request.getRemoteAddr());
		String result;
		try {
			if ("entries".equals(indexName)) {
				result = solrService.indexEntriesChromosome(false, chrName);
			} else if ("gold-entries".equals(indexName)) {
				result = solrService.indexEntriesChromosome(true, chrName);
			} else {
				result = "Error: invalid index name, should be either entries or gold-entries";
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			result = e.getLocalizedMessage();
		}
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "/tasks/solr/{indexname}/init", method = { RequestMethod.GET }, produces = {MediaType.TEXT_PLAIN_VALUE})
	@ApiMethod(path = "/tasks/solr/{indexname}/init", verb = ApiVerb.GET, description = "Clears the entries or gold-entries solr index")
	public String initEntryIndex(
			@ApiPathParam(name = "indexname", description = "The name of of an entry index: entries or gold-entries",  allowedvalues = { "gold-entries"})
			@PathVariable("indexname") String indexName, HttpServletRequest request) {

		LOGGER.warn("Request to clear solr index for  " + indexName + " from " + request.getRemoteAddr());
		String result;
		try {
			if ("entries".equals(indexName)) {
				result = solrService.initIndexEntries(false);
			} else if ("gold-entries".equals(indexName)) {
				result = solrService.initIndexEntries(true);
			} else {
				result = "Error: invalid index name, should be either entries or gold-entries";
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			result = e.getLocalizedMessage();
		}
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "/tasks/solr/terminologies/reindex", method = { RequestMethod.GET }, produces = {MediaType.TEXT_PLAIN_VALUE})
	@ApiMethod(path = "/tasks/solr/terminologies/reindex", verb = ApiVerb.GET, description = "Rebuilds the solr index for terminologies")
	public String indexTerminologies(HttpServletRequest request) {

		LOGGER.warn("Request to build solr index for terminologies " + request.getRemoteAddr());
		String result;
		try {
			result = solrService.indexTerminologies();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			result = e.getLocalizedMessage();
		}
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "/tasks/solr/publications/reindex", method = { RequestMethod.GET }, produces = {MediaType.TEXT_PLAIN_VALUE})
	@ApiMethod(path = "/tasks/solr/publications/reindex", verb = ApiVerb.GET, description = "Rebuilds the solr index for publications")
	public String indexPublicationss(HttpServletRequest request) {

		LOGGER.warn("Request to build solr index for terminologies " + request.getRemoteAddr());
		String result;
		try {
			result = solrService.indexPublications();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			result = e.getLocalizedMessage();
		}
		return result;
	}
	
}
