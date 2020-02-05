package org.nextprot.api.web.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiBodyObject;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.web.service.PepXService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Controller
@Api(name = "Peptide uniqueness checker", description = "Retrieve accession nos. of isoforms having a sequence matching the query peptide(s)", group = "Tools")
@RequestMapping(value = "/entries/search/")
public class EntriesSearchController {

	@Autowired
	private PepXService pepXService;

	@ResponseBody
	@RequestMapping(value = "peptide", method = { RequestMethod.GET }, produces = { MediaType.APPLICATION_JSON_VALUE })
	@ApiMethod(path = "peptide", verb = ApiVerb.GET, description = "Retrieve accession nos. of isoforms having a sequence matching the query peptide(s), taking into account variants. Leucine and isoleucine are considered to be equivalent.")
	public List<Entry> pepx(
			@ApiQueryParam(name = "peptide(s)", description = "A peptide or a list of peptides separated with a comma", allowedvalues = { "NDVVPTMAQGVLEYK" }) 
			@RequestParam(value = "peptide", required = true) String peptide,
			@ApiQueryParam(name = "no variant match", description = "Tells if the variants should be taken into account for match", allowedvalues = { "true" })
			@RequestParam(value = "no-variant-match", required = false) boolean noVariantMatch) {
		checkPeptideLength(Arrays.stream(peptide.split(",")));
		return getEntriesWithPeptides(peptide, "GET", noVariantMatch);
	}

	@ResponseBody
	@RequestMapping(value = "peptide-post", method = { RequestMethod.POST }, consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = { MediaType.APPLICATION_JSON_VALUE })
	@ApiMethod(path = "peptide-post", verb = ApiVerb.POST, description = "Retrieve accession nos. of isoforms having a sequence matching the query peptides, taking into account variants. Leucine and isoleucine are considered to be equivalent.")
	// TODO: add description of body
	public List<Entry> pepx(@ApiBodyObject @RequestBody List<String> peptideList,
			@ApiQueryParam(name = "no variant match", description = "Tells if the variants should be taken into account for match", allowedvalues = { "true" })
			@RequestParam(value = "no-variant-match", required = false) boolean noVariantMatch) {
		NPreconditions.checkNotNull(peptideList, "The peptide list must be not null");
		NPreconditions.checkNotEmpty(peptideList, "The peptide list must be not empty");
		checkPeptideLength(peptideList.stream());
		return getEntriesWithPeptides(String.join(",", peptideList), "POST", noVariantMatch);
	}

	private void checkPeptideLength(Stream<String> peptideList) {
		NPreconditions.checkTrue(peptideList.allMatch(p -> p.length() >= 6), "The minimum length of all peptides must be 6");
	}

	private List<Entry> getEntriesWithPeptides(String peptide, String method, boolean ignoreVariantMatches) {
		Boolean modeIL = new Boolean(true);
		return pepXService.findEntriesWithPeptides(peptide, modeIL, method, ignoreVariantMatches);
	}
}
