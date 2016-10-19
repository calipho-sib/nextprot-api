package org.nextprot.api.core.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.core.service.GeneIdentifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.Set;

@Controller
@Api(name = "Gene Names", description = "Provides gene names")
public class GeneNameController {

	@Autowired
	private GeneIdentifierService geneIdentifierService;

	@ApiMethod(path = "/gene-names", verb = ApiVerb.GET, description = "Retrieves all gene names found in neXtProt", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/gene-names", method = { RequestMethod.GET })
	@ResponseBody
	public Set<String> geneIdentifiers() {
		return geneIdentifierService.findGeneNames();
	}

	//@ApiMethod(path = "/entry-gene-names", verb = ApiVerb.GET, description = "Retrieves all gene names found in neXtProt", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/entry-gene-names", method = { RequestMethod.GET })
	@ResponseBody
	public Map<String, Set<String>> entryGeneIdentifiers() {
		return geneIdentifierService.findEntryGeneNames();
	}

	@ApiMethod(path = "/gene-names/entry/{entryAccession}", verb = ApiVerb.GET, description = "Retrieves the gene names that code the given protein", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/gene-names/entry/{entryAccession}", method = { RequestMethod.GET })
	@ResponseBody
	public Set<String> geneIdentifiersByEntryAccession(
			@ApiPathParam(name = "entryAccession", description = "the neXtProt entry protein name",  allowedvalues = { "NX_P01308"}) @PathVariable("entryAccession")  String entryAccession) {
		return geneIdentifierService.findGeneNamesByEntryAccession(entryAccession);
	}

	@RequestMapping(value = "/entry-gene-names", method = { RequestMethod.GET })
	@ResponseBody
	public Map<String, Set<String>> entryGeneIdentifiers() {
		return geneIdentifierService.findEntryGeneNames();
	}
}
