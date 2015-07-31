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
@Api(name = "Search Entries", description = "Search for peptides on human isoforms")
@RequestMapping(value = "/entries/search/")
public class EntriesSearchController {

	@Autowired
	private PepXService pepXService;

	@ResponseBody
	@RequestMapping(value = "peptide", method = { RequestMethod.GET }, produces = { MediaType.APPLICATION_JSON_VALUE })
	@ApiMethod(path = "peptide", verb = ApiVerb.GET, description = "Gets entries that match a peptide")
	public List<Entry> pepx(
			@ApiQueryParam(name = "peptide", description = "The peptide", allowedvalues = { "IRLNK" }) @RequestParam(value = "peptide", required = true) String peptide,
			@ApiQueryParam(name = "modeIL", description = "The mode isoleucine / leucine replaces the isoleucine by a leucine", allowedvalues = { "true" }) @RequestParam(value = "modeIL", required = false) Boolean modeIL) {

		NPreconditions.checkTrue(peptide.length() >= 5, "The minimum lenght of the peptide must be 5");
		if (modeIL == null) { modeIL = true;}
		return pepXService.findEntriesWithPeptides(peptide, modeIL);

	}

	@ResponseBody
	@RequestMapping(value = "blast", method = { RequestMethod.GET }, produces = { MediaType.APPLICATION_JSON_VALUE })
	@ApiMethod(path = "blast", verb = ApiVerb.GET, description = "Blast either a sequence or an isoform. If an entry is speficied the first isoform is taken into account")
	public List<Entry> blast(@ApiQueryParam(name = "sequence", description = "The sequence to blast", allowedvalues = { "IRLNK" }) @RequestParam(value = "sequence", required = false) String sequence,
			@ApiQueryParam(name = "isoform", description = "The name of the isoform ") @RequestParam(value = "isoform", required = false) String isoform) {

		throw new NextProtException("Blast not supported yet ");

	}

}
