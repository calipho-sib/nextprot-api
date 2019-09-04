package org.nextprot.api.core.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.core.domain.ChromosomalLocation;
import org.nextprot.api.core.service.GeneIdentifierService;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@Api(name = "Gene Names", description = "Provides gene names")
public class GeneNameController {

	@Autowired
	private GeneIdentifierService geneIdentifierService;

	@Autowired
	private MasterIdentifierService masterIdentifierService;

	@ApiMethod(path = "/gene-names", verb = ApiVerb.GET, description = "Retrieves all gene names found in neXtProt", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/gene-names", method = { RequestMethod.GET })
	@ResponseBody
	public Set<String> geneIdentifiers() {
		return geneIdentifierService.findGeneNames();
	}

	@ApiMethod(path = "/gene-names/entry/{entryAccession}", verb = ApiVerb.GET, description = "Retrieves the gene names that code the given protein (the first gene name is the recommended one)", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/gene-names/entry/{entryAccession}", method = { RequestMethod.GET })
	@ResponseBody
	public List<String> geneIdentifiersByEntryAccession(
			@ApiPathParam(name = "entryAccession", description = "the neXtProt entry protein name",  allowedvalues = { "NX_P01308"}) @PathVariable("entryAccession")  String entryAccession) {
		return geneIdentifierService.findGeneNamesByEntryAccession(entryAccession);
	}

	@ApiMethod(path = "/gene-names/chromosome", verb = ApiVerb.GET, description = "Retrieves the gene name and the associated entry accessions", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/gene-names/chromosome", method = {RequestMethod.GET})
	@ResponseBody
	public Map<String, Object> entryGeneByChromosomalLocation(
			@ApiQueryParam(name = "chromosome", description = "Chromosome", allowedvalues = {"1"})
			@RequestParam(value = "chromosome") String chromosome,
			@ApiQueryParam(name = "firstPosition", description = "Starting position on the chromosome", allowedvalues = {"147954635"})
			@RequestParam(value = "firstPosition") int firstPosition,
			@ApiQueryParam(name = "lastPosition", description = "Ending position on the chromosome", allowedvalues = {"147955419"})
			@RequestParam(value = "lastPosition") int lastPosition
	) {
		// Finds the gene name given the chromosomal location
		ChromosomalLocation chromosomalLocation = new ChromosomalLocation();
		chromosomalLocation.setChromosome(chromosome);
		chromosomalLocation.setFirstPosition(firstPosition);
		chromosomalLocation.setLastPosition(lastPosition);
		String geneName = geneIdentifierService.findEntryGeneNamesByChromosomeLocation(chromosomalLocation);

		// Find the entries associated to the gene
		Set<String> entryAccessions = masterIdentifierService.findEntryAccessionByGeneName(geneName, false);
		Map<String, Object> geneEntries = new HashMap<>();
		geneEntries.put("geneName", geneName);
		geneEntries.put("entries", entryAccessions);
		return geneEntries;
	}

	@RequestMapping(value = "/entry-gene-names", method = { RequestMethod.GET })
	@ResponseBody
	public Map<String, List<String>> entryGeneIdentifiers() {
		return geneIdentifierService.findEntryGeneNames();
	}
}
