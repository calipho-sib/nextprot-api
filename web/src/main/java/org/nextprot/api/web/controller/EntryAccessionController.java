package org.nextprot.api.web.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.service.ProteinExistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@Api(name = "Entry Accessions", description = "Retrieves nextProt entry accession numbers")
public class EntryAccessionController {

	@Autowired	private MasterIdentifierService masterIdentifierService;
	@Autowired	private ProteinExistenceService proteinExistenceService;

	@ApiMethod(path = "/entry-accessions", verb = ApiVerb.GET, description = "Retrieve all neXtProt entry accession numbers", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/entry-accessions", method = { RequestMethod.GET })
	@ResponseBody
	public List<String> masterIdentifiers() {
		return new ArrayList<>(masterIdentifierService.findUniqueNames());
	}
	
	
	@ApiMethod(path = "/entry-accessions/chromosome/{chromosome}", verb = ApiVerb.GET, description = "Retrieve all neXtProt entry accession numbers of the given chromosome", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/entry-accessions/chromosome/{chromosome}", method = { RequestMethod.GET })
	@ResponseBody
	public List<String> masterIdentifiersPerChromosome(
			@ApiPathParam(name = "chromosome", description = "The chromosome number or name (X,Y..)",  allowedvalues = { "Y"}) @PathVariable("chromosome")  String chromosome) {
		return new ArrayList<>(masterIdentifierService.findUniqueNamesOfChromosome(chromosome));
	}

	
	@ApiMethod(path = "/entry-accessions/gene/{geneName}", verb = ApiVerb.GET, description = "Retrieves the entry accession number(s) corresponding to the given gene name", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/entry-accessions/gene/{geneName}", method = { RequestMethod.GET })
	@ResponseBody
	public List<String> masterIdentifierByGeneName(
			@ApiPathParam(name = "geneName", description = "The gene name",  allowedvalues = { "INSR"}) @PathVariable("geneName")  String geneName, 
			@ApiQueryParam(name = "synonym", description = "Search gene name synonyms if set to true.",  allowedvalues = { "true", "false" })
			@RequestParam(value = "synonym", required = false) boolean withSynonyms) {
		return new ArrayList<>(masterIdentifierService.findEntryAccessionByGeneName(geneName, withSynonyms));
	}

	/*@ApiMethod(path = "/entry-accessions/protein-existence/{PE}", verb = ApiVerb.GET, description = "Retrieves the entry accession number(s) corresponding to the given gene name", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/entry-accessions/protein-existence/{PE}", method = { RequestMethod.GET })
	@ResponseBody
	public List<String> masterIdentifierByProteinExistence(
			@ApiPathParam(name = "proteinExistence", description = "The protein existence value type (ALL, PROTEIN_LEVEL, TRANSCRIPT_LEVEL, HOMOLOGY, PREDICTED, UNCERTAIN)",
					allowedvalues = { "PROTEIN_LEVEL"}) @PathVariable("proteinExistence")  String proteinExistence) {

		return masterIdentifierService.findEntryAccessionsByProteinExistence(ProteinExistence.valueOfKey(proteinExistence));
	}*/
}
