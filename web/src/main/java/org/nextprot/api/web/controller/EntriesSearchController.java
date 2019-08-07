package org.nextprot.api.web.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.web.service.PepXService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@Api(name = "Peptide uniqueness checker", description = "Retrieve accession nos. of isoforms having a sequence matching the query peptide(s)", group = "Tools")
@RequestMapping(value = "/entries/search/")
public class EntriesSearchController {

	@Autowired
	private PepXService pepXService;

	@ResponseBody
	@RequestMapping(value = "peptide", method = { RequestMethod.GET, RequestMethod.POST }, consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = { MediaType.APPLICATION_JSON_VALUE })
	@ApiMethod(path = "peptide", verb = ApiVerb.GET, description = "Retrieve accession nos. of isoforms having a sequence matching the query peptide(s), taking into account variants. Leucine and isoleucine are considered to be equivalent.")
	public List<Entry> pepx(
			@ApiQueryParam(name = "peptide(s)", description = "A peptide or a list of peptides separated with a comma", allowedvalues = { "NDVVPTMAQGVLEYK" }) @RequestParam(value = "peptide", required = true) String peptide)
			{
		NPreconditions.checkTrue(peptide.length() >= 6, "The minimum length of the peptide must be 6");
		Boolean modeIL = new Boolean(true);
		return pepXService.findEntriesWithPeptides(peptide, modeIL);
	}

	
}
