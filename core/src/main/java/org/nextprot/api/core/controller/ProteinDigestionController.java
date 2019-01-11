package org.nextprot.api.core.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.core.service.DigestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Set;

// See also sources of mzjava-proteomics are available at https://bitbucket.org/sib-pig/mzjava-proteomics
@Controller
@Api(name = "Protein digestion", description = "Digest proteins with proteases", group = "Tools")
public class ProteinDigestionController {

	@Autowired
	private DigestionService digestionService;

	@ResponseBody
	@RequestMapping(value = "/digestion/available-protease-list", method = { RequestMethod.GET }, produces = { MediaType.APPLICATION_JSON_VALUE })
	@ApiMethod(path = "/digestion/available-protease-list", verb = ApiVerb.GET, description = "list all available proteases")
	public List<String> listAllProteases() {

		return digestionService.getProteaseNames();
	}

	@ResponseBody
	@RequestMapping(value = "/digestion/digest-all-proteins", method = { RequestMethod.GET }, produces = { MediaType.APPLICATION_JSON_VALUE })
	@ApiMethod(path = "/digestion/digest-all-proteins", verb = ApiVerb.GET, description = "digest all neXtProt mature proteins with TRYPSIN (with a maximum of 2 missed cleavages)")
	public Set<String> digestAllProteins() {

		return digestionService.digestAllWithTrypsin();
	}

	@ResponseBody
	@RequestMapping(value = "/digestion/{isoformOrEntryAccession}", method = { RequestMethod.GET }, produces = { MediaType.APPLICATION_JSON_VALUE })
	@ApiMethod(path = "/digestion/{isoformOrEntryAccession}", verb = ApiVerb.GET, description = "digest the mature protein with a specific protease")
	public Set<String> digestProtein(
			@ApiQueryParam(name = "isoformOrEntryAccession", description = "A neXtProt entry or isoform accession (i.e. NX_P01308 or NX_P01308-1).", allowedvalues = { "NX_P01308" })
			@RequestParam(value = "isoformOrEntryAccession") String isoformOrEntryAccession,
			@ApiQueryParam(name = "protease", description = "chose a protease to digest a protein", allowedvalues = { "TRYPSIN" })
			@RequestParam(value = "protease") String protease,
			@ApiQueryParam(name = "minpeplen", description = "minimum peptide length", allowedvalues = { "7" })
			@RequestParam(value = "minpeplen", required = false) Integer minPepLen,
			@ApiQueryParam(name = "maxpeplen", description = "maximum peptide length", allowedvalues = { "77" })
			@RequestParam(value = "maxpeplen", required = false) Integer maxPepLen,
			@ApiQueryParam(name = "maxmissedcleavages", description = "maximum number of missed cleavages (cannot be greater than 2)", allowedvalues = { "2" })
			@RequestParam(value = "maxmissedcleavages", required = false) Integer maxMissedCleavages) {

		return digestionService.digest(isoformOrEntryAccession, protease.toUpperCase(), minPepLen, maxPepLen, maxMissedCleavages);
	}
}
