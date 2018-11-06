package org.nextprot.api.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.commons.exception.SearchQueryException;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.CvTermGraph;
import org.nextprot.api.core.service.CvTermGraphService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.solr.Query;
import org.nextprot.api.solr.SolrService;
import org.nextprot.api.solr.dto.QueryRequest;
import org.nextprot.api.solr.dto.SearchResult;
import org.nextprot.api.web.service.QueryBuilderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Api(name = "Terminology", description = "Method to retrieve a terminology")
public class TermController {

	private static final Log LOGGER = LogFactory.getLog(TermController.class);


	@Autowired private TerminologyService terminologyService;
	@Autowired private CvTermGraphService cvTermGraphService;
	@Autowired private SolrService solrService;
	@Autowired private QueryBuilderService queryBuilderService;

	@ApiMethod(path = "/terminology/{terminology}", verb = ApiVerb.GET, description = "Gets a terminology", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/terminology/{terminology}", method = { RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<CvTerm> getTerminology(
			@ApiPathParam(name = "terminology", description = "The name of the terminology. To get a list of possible terminologies, look at terminology-names method",  allowedvalues = { "nextprot-anatomy-cv"})
			@PathVariable("terminology") String terminology) {

		return terminologyService.findCvTermsByOntology(TerminologyCv.getTerminologyOf(terminology).name());
	}
	
	@ApiMethod(path = "/terminology-names", verb = ApiVerb.GET, description = "Gets a list of terminology names", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/terminology-names", method = { RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<String> getTerminologyNames() {
		return terminologyService.findTerminologyNamesList();
	}

	@RequestMapping(value = "/terminology-graph/build-all", produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, String> buildTerminologyCache() {

		Map<String, String> map = new HashMap<>();

		Instant totalTime = Instant.now();
		for (TerminologyCv terminologyCv : TerminologyCv.values()) {

			Instant t = Instant.now();
			cvTermGraphService.findCvTermGraph(terminologyCv);
			long ms = ChronoUnit.MILLIS.between(t, Instant.now());

			map.put(terminologyCv.name(), String.valueOf(ms)+" ms");
		}
		map.put("TOTAL BUILD", String.valueOf(ChronoUnit.SECONDS.between(totalTime, Instant.now()))+" s");

		return map;
	}

	@ApiMethod(path = "/terminology-graph/{terminology}", verb = ApiVerb.GET, description = "Get the graph of terminology", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/terminology-graph/{terminology}", method = { RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, CvTermGraph.View> getTerminologyGraph(
			@ApiPathParam(name = "terminology", description = "The name of the terminology. To get a list of possible terminologies, look at terminology-names method",  allowedvalues = { "nextprot-anatomy-cv"})
			@PathVariable("terminology") String terminology) {

		return Collections.singletonMap("terminology-graph", cvTermGraphService.findCvTermGraph(TerminologyCv.getTerminologyOf(terminology)).toView());
	}

    @ApiMethod(path = "/term/{term}/is-hierarchical-terminology", verb = ApiVerb.GET, description = "Tells if the terminology of the given term is hierarchical", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/term/{term}/is-hierarchical-terminology", method = { RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Boolean> isTermOntologyHierarchical(
            @ApiPathParam(name = "term", description = "The accession of the cv term",  allowedvalues = { "TS-0079"})
            @PathVariable("term") String term) {

        CvTerm cvTerm = terminologyService.findCvTermByAccessionOrThrowRuntimeException(term);

        return Collections.singletonMap("is-hierarchical-terminology", TerminologyCv.getTerminologyOf(cvTerm.getOntology()).isHierarchical());
    }

	@ApiMethod(path = "/term/{term}/ancestor-graph", verb = ApiVerb.GET, description = "Get the ancestor graph of the given term", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/term/{term}/ancestor-graph", method = { RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, CvTermGraph.View> getAncestorGraph(
		@ApiPathParam(name = "term", description = "The accession of the cv term",  allowedvalues = { "TS-0079"})
		@PathVariable("term") String term,
		@ApiQueryParam(name="includeRelevantFor") @RequestParam(value="includeRelevantFor", required=false) boolean includeRelevantFor,
		@ApiQueryParam(name="includeSilver") @RequestParam(value="includeSilver", required=false, defaultValue= "false") boolean includeSilver) {

		CvTerm cvTerm = terminologyService.findCvTermByAccessionOrThrowRuntimeException(term);

		CvTermGraph graph = cvTermGraphService.findCvTermGraph(TerminologyCv.getTerminologyOf(cvTerm.getOntology()));

		CvTermGraph.View subgraphView = graph.calcAncestorSubgraph(cvTerm.getId().intValue()).toView();

		if(includeRelevantFor){
			addRelevantFor(subgraphView, includeSilver);
		}

		return Collections.singletonMap("ancestor-graph", subgraphView);
	}

	private void addRelevantFor(CvTermGraph.View subgraphView, boolean includeSilver){

		subgraphView.getNodes().forEach(node -> {

			QueryRequest qr = new QueryRequest();
            String termAccession = node.getAccession();

            // Treatment specific to EC numbers
            if (termAccession.matches("^[1-7]\\.[1-9-][0-9]?\\.[1-9-][0-9]?\\.n?[1-9-][0-9]{0,2}$")) {

                termAccession = "\"EC "+termAccession+"\"";
            }

            qr.setQuery(termAccession);

			if(includeSilver){
				qr.setQuality("gold-and-silver");
			}else {
				qr.setQuality("gold");
			}

			qr.setRows("0");
			Query query = queryBuilderService.buildQueryForSearch(qr, "entry");
			try {
				SearchResult sr = solrService.executeQuery(query);
				long relevantForEntry = sr.getFound();
				node.setRelevantFor(relevantForEntry);
			} catch (SearchQueryException e) {
				e.printStackTrace();
				LOGGER.error(e.getLocalizedMessage());
			}
		});

	}


	//
	@ApiMethod(path = "/term/{term}", verb = ApiVerb.GET, description = "Get information for the given term", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/term/{term:.+}", method = { RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE)
	public CvTerm getTermInfo(
			@ApiPathParam(name = "term", description = "The accession of the cv term",  allowedvalues = { "TS-0079"})
			@PathVariable("term") String term) {

        //Mapping equals /term/{term:.+} because for some terms like (1.1.1.1), the last .1 was seen as the extension
        //The regex .+ allows to consume everything but JSON is included therefore we remove it if the user uses it
        term = term.replace(".json", "").replace(".JSON", "");
        return terminologyService.findCvTermByAccessionOrThrowRuntimeException(term);
    }

    @ApiMethod(path = "/term/{term}/descendant-graph", verb = ApiVerb.GET, description = "Get the descendant graph of the given term", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/term/{term}/descendant-graph", method = { RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, CvTermGraph.View> getDescendantGraph(
            @ApiPathParam(name = "term", description = "The accession of the cv term",  allowedvalues = { "TS-0079"})
            @PathVariable("term") String term,
			@ApiQueryParam(name="includeRelevantFor") @RequestParam(value="includeRelevantFor", required=false) boolean includeRelevantFor,
			@ApiQueryParam(name="includeSilver") @RequestParam(value="includeSilver", required=false, defaultValue= "false") boolean includeSilver,
			@ApiQueryParam(name="depth") @RequestParam(value="depth", required=false, defaultValue= "0") int depthMax) {

        CvTerm cvTerm = terminologyService.findCvTermByAccessionOrThrowRuntimeException(term);
        CvTermGraph graph = cvTermGraphService.findCvTermGraph(TerminologyCv.getTerminologyOf(cvTerm.getOntology()));

		CvTermGraph.View subgraphView = graph.calcDescendantSubgraph(cvTerm.getId().intValue(), depthMax).toView();

		if(includeRelevantFor){
			addRelevantFor(subgraphView, includeSilver);
		}

		return Collections.singletonMap("descendant-graph", subgraphView);
	}
}
