package org.nextprot.api.blast.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.blast.domain.BlastConfig;
import org.nextprot.api.blast.domain.gen.BlastResult;
import org.nextprot.api.blast.service.BlastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Api(name = "Blast", description = "Search protein sequence into neXtProt database.", group = "Tools")
public class BlastController {

	@Autowired
	private BlastService blastService;

	@ApiMethod(path = "/blastp/seq/{sequence}", verb = ApiVerb.GET, description = "Search protein sequence", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/blastp/seq/{sequence}", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public BlastResult blastProteinSequence(
			@ApiPathParam(name = "sequence", description = "A protein sequence query.",  allowedvalues = { "GTTYVTDKSEEDNEIESEEEVQPKTQGSRR" })
			@PathVariable("sequence") String sequence,
			@ApiPathParam(name = "header", description = "A query header.",  allowedvalues = { "protein sequence query" })
			@RequestParam(value = "header", defaultValue = "protein sequence query") String header,

			@ApiQueryParam(name = "matrix", description = "Scoring matrix name", allowedvalues = { "BLOSUM62" })
			@RequestParam(value = "matrix", required = false) String matrix,
			@ApiQueryParam(name = "evalue", description = "Expected value (E) threshold for saving hits", allowedvalues = { "10" })
			@RequestParam(value = "evalue", required = false) Double evalue,
			@ApiQueryParam(name = "gapopen", description = "Cost to open a gap", allowedvalues = { "11" })
			@RequestParam(value = "gapopen", required = false) Integer gapOpen,
			@ApiQueryParam(name = "gapextend", description = "Cost to extend a gap", allowedvalues = { "1" })
			@RequestParam(value = "gapextend", required = false) Integer gapExtend,

			@RequestParam(value = "debug", required = false) boolean debug) {

		BlastConfig config = newConfig(matrix, evalue, gapOpen, gapExtend, debug);

		return blastService.blastProteinSequence(config, header, sequence);
	}

    @ApiMethod(path = "/blastp/isoform/{isoform}", verb = ApiVerb.GET, description = "Search isoform sequence from neXtProt isoform accession", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/blastp/isoform/{isoform}", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public BlastResult blastIsoform(
            @ApiPathParam(name = "isoform", description = "An neXtProt isoform accession.", allowedvalues = { "NX_P01308-1" })
            @PathVariable("isoform") String isoform,
			@ApiQueryParam(name = "begin", description = "The first sequence position (should be strictly positive)")
			@RequestParam(value = "begin", required = false) Integer begin,
			@ApiQueryParam(name = "end", description = "The last sequence position (should be <= sequence length)")
			@RequestParam(value = "end", required = false) Integer end,

			@ApiQueryParam(name = "matrix", description = "Scoring matrix name", allowedvalues = { "BLOSUM62" })
			@RequestParam(value = "matrix", required = false) String matrix,
			@ApiQueryParam(name = "evalue", description = "Expected value (E) threshold for saving hits", allowedvalues = { "10" })
			@RequestParam(value = "evalue", required = false) Double eValue,
			@ApiQueryParam(name = "gapopen", description = "Cost to open a gap", allowedvalues = { "11" })
			@RequestParam(value = "gapopen", required = false) Integer gapOpen,
			@ApiQueryParam(name = "gapextend", description = "Cost to extend a gap", allowedvalues = { "1" })
			@RequestParam(value = "gapextend", required = false) Integer gapExtend,

            @RequestParam(value = "debug", required = false) boolean debug) {

		BlastConfig config = newConfig(matrix, eValue, gapOpen, gapExtend, debug);

        return blastService.blastIsoformSequence(config, isoform, begin, end);
    }

    private BlastConfig newConfig(String matrix, Double eValue, Integer gapOpen, Integer gapExtend, boolean debug) {

		// TODO: get the following paths from properties
		BlastConfig config = new BlastConfig("/Users/fnikitin/Applications/ncbi-blast-2.3.0+/bin", "/Users/fnikitin/data/blast/db");
		config.setDebugMode(debug);

		if (matrix != null)
			config.setMatrix(BlastConfig.Matrix.valueOf(matrix));
		config.setEvalue(eValue);
		config.setGapOpen(gapOpen);
		config.setGapExtend(gapExtend);

		return config;
	}
}