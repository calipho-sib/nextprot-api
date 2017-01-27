package org.nextprot.api.web.controller;

import java.util.List;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.web.service.PepXService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Api(name = "Pepx", description = "Search for peptides on human isoforms", group = "Tools")
@RequestMapping(value = "/entries/search/")
public class EntriesSearchController {

	@Autowired
	private PepXService pepXService;

	@ResponseBody
	@RequestMapping(value = "peptide", method = { RequestMethod.GET }, produces = { MediaType.APPLICATION_JSON_VALUE })
	@ApiMethod(path = "peptide", verb = ApiVerb.GET, description = "Gets entries that match a peptide or a list of peptides. In the response, when the variants field is not null, the matched sequence includes a variant.")
	public List<Entry> pepx(
			@ApiQueryParam(name = "peptide(s)", description = "A peptide or a list of peptides separated with a comma", allowedvalues = { "LQELFLQEVR" }) @RequestParam(value = "peptide", required = true) String peptide)
			{
		NPreconditions.checkTrue(peptide.length() >= 6, "The minimum length of the peptide must be 6");
		Boolean modeIL = new Boolean(true);
		return pepXService.findEntriesWithPeptides(peptide, modeIL);
	}

	
}
