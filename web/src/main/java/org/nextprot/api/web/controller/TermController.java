package org.nextprot.api.web.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.utils.graph.CvTermGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Api(name = "Terminology", description = "Method to retrieve a terminology")
public class TermController {

	@Autowired private TerminologyService terminologyService;

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
			terminologyService.findCvTermGraph(terminologyCv);
			long ms = ChronoUnit.MILLIS.between(t, Instant.now());

			map.put(terminologyCv.name(), String.valueOf(ms)+" ms");
		}
		map.put("TOTAL BUILD", String.valueOf(ChronoUnit.SECONDS.between(totalTime, Instant.now()))+" s");

		return map;
	}

	@ApiMethod(path = "/terminology-graph/{terminology}", verb = ApiVerb.GET, description = "Gets the graph of terminology", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/terminology-graph/{terminology}", method = { RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, CvTermGraph.View> getTerminologyGraph(
			@ApiPathParam(name = "terminology", description = "The name of the terminology. To get a list of possible terminologies, look at terminology-names method",  allowedvalues = { "nextprot-anatomy-cv"})
			@PathVariable("terminology") String terminology) {

		return Collections.singletonMap("terminology-graph", terminologyService.findCvTermGraph(TerminologyCv.getTerminologyOf(terminology)).toView());
	}

	@ApiMethod(path = "/term/{term}/ancestor-graph", verb = ApiVerb.GET, description = "Get the ancestor graph of the given term", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/term/{term}/ancestor-graph", method = { RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, CvTermGraph.View> getAncestorGraph(
			@ApiPathParam(name = "term", description = "The accession of the cv term",  allowedvalues = { "TS-0079"})
			@PathVariable("term") String term) {

		CvTerm cvTerm = terminologyService.findCvTermByAccession(term);

		CvTermGraph graph = terminologyService.findCvTermGraph(TerminologyCv.getTerminologyOf(cvTerm.getOntology()));

		return Collections.singletonMap("ancestor-graph", graph.calcAncestorSubgraph(cvTerm.getId().intValue()).toView());
	}
}
