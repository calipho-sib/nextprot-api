package org.nextprot.api.blast.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.blast.domain.BlastPConfig;
import org.nextprot.api.blast.service.BlastPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Api(name = "BlastP", description = "Search protein sequence into neXtProt database.", group = "Tools")
public class BlastPController {

	@Autowired
	private BlastPService blastPService;

	@ApiMethod(path = "/blastp/seq/{sequence}", verb = ApiVerb.GET, description = "Search protein sequence", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/blastp/seq/{sequence}", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public String blastProteinSequence(
			@ApiPathParam(name = "sequence", description = "A protein sequence query.",  allowedvalues = { "GTTYVTDKSEEDNEIESEEEVQPKTQGSRR" })
			@PathVariable("sequence") String sequence,
			@ApiPathParam(name = "header", description = "A query header.",  allowedvalues = { "protein sequence query" })
			@RequestParam(value = "header", defaultValue = "protein sequence query") String header,
			@RequestParam(value = "debug", required = false) boolean debug) {

		// TODO: get the following paths from properties
		BlastPConfig config = new BlastPConfig("/Users/fnikitin/Applications/ncbi-blast-2.3.0+/bin", "/Users/fnikitin/data/blast/db");
		config.setDebugMode(debug);

		return blastPService.blastProteinSequence(config, header, sequence);
	}

    @ApiMethod(path = "/blastp/isoform/{isoform}", verb = ApiVerb.GET, description = "Search isoform sequence from neXtProt isoform accession", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/blastp/isoform/{isoform}", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public String blastIsoform(
            @ApiPathParam(name = "isoform", description = "The name of a neXtProt isoform.", format = "NX_...-{num}", allowedvalues = { "NX_P01308-1" })
            @PathVariable("isoform") String isoform,
			@ApiQueryParam(name = "begin", description = "The first sequence position", allowedvalues = { "1" })
			@RequestParam(value = "begin", required = false) Integer begin,
			@ApiQueryParam(name = "end", description = "The last sequence position")
			@RequestParam(value = "end", required = false) Integer end,
            @RequestParam(value = "debug", required = false) boolean debug) {

        // TODO: get the following paths from properties
        BlastPConfig config = new BlastPConfig("/Users/fnikitin/Applications/ncbi-blast-2.3.0+/bin", "/Users/fnikitin/data/blast/db");
        config.setDebugMode(debug);

        return blastPService.blastIsoform(config, isoform, begin, end);
    }
}