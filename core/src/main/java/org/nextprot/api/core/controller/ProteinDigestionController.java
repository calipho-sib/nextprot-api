package org.nextprot.api.core.controller;

import org.expasy.mzjava.proteomics.mol.digest.Protease;
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

import java.util.Set;

// See also sources of mzjava-proteomics are available at https://bitbucket.org/sib-pig/mzjava-proteomics
@Controller
@Api(name = "Protein Digestion", description = "Digest proteins with a protease", group = "Tools")
public class ProteinDigestionController {

	@Autowired
	private DigestionService digestionService;

	@ResponseBody
	@RequestMapping(value = "/protein/digest-all", method = { RequestMethod.GET }, produces = { MediaType.APPLICATION_JSON_VALUE })
	@ApiMethod(path = "/protein/digest-all", verb = ApiVerb.GET, description = "digest all neXtProt proteins")
	public Set<String> digestAllProteins(
			@ApiQueryParam(name = "protease", description = "chose a protease to digest proteins", allowedvalues = { "trypsin" })
			@RequestParam(value = "protease") String protease,
			@ApiQueryParam(name = "minpeplen", description = "minimum peptide length", allowedvalues = { "7" })
			@RequestParam(value = "minpeplen", required = false) Integer minPepLen,
			@ApiQueryParam(name = "maxpeplen", description = "maximum peptide length", allowedvalues = { "77" })
			@RequestParam(value = "maxpeplen", required = false) Integer maxPepLen) {

		return digestionService.digestAllWithTrypsin();
	}

	@ResponseBody
	@RequestMapping(value = "/protein/digest/{entryAccession}", method = { RequestMethod.GET }, produces = { MediaType.APPLICATION_JSON_VALUE })
	@ApiMethod(path = "/protein/digest/{entryAccession}", verb = ApiVerb.GET, description = "digest a protein")
	public Set<String> digestProtein(
			@ApiQueryParam(name = "entryAccession", description = "A neXtProt entry accession.", allowedvalues = { "NX_P01308" })
			@RequestParam(value = "entryAccession") String entryAccession,
			@ApiQueryParam(name = "protease", description = "chose a protease to digest proteins", allowedvalues = { "trypsin" })
			@RequestParam(value = "protease") String protease,
			@ApiQueryParam(name = "minpeplen", description = "minimum peptide length", allowedvalues = { "7" })
			@RequestParam(value = "minpeplen", required = false) Integer minPepLen,
			@ApiQueryParam(name = "maxpeplen", description = "maximum peptide length", allowedvalues = { "77" })
			@RequestParam(value = "maxpeplen", required = false) Integer maxPepLen) {

		return digestionService.digest(entryAccession, Protease.valueOf(protease.toUpperCase()), minPepLen, maxPepLen, 2);
	}
}
