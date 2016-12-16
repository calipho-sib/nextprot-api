package org.nextprot.api.blast.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.blast.domain.*;
import org.nextprot.api.blast.service.BlastProgram;
import org.nextprot.api.blast.service.BlastService;
import org.nextprot.api.commons.utils.ExceptionWithReason;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Api(name = "Blast", description = "Search protein sequence into neXtProt database.", group = "Tools")
public class BlastController {

	@Autowired
	private BlastService blastService;

    @Value("${blastp.bin}")
    private String blastBinPath;

    @Value("${makeblastdb.bin}")
    private String makeblastdbBinPath;

    @Value("${blastp.db}")
    private String blastDbPath;

	@ApiMethod(path = "/blast/seq/{sequence}", verb = ApiVerb.GET, description = "Search protein sequence", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/blast/seq/{sequence}", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public BlastProgramOutput blastProteinSequence(
			@ApiPathParam(name = "sequence", description = "A protein sequence query.",  allowedvalues = { "GTTYVTDKSEEDNEIESEEEVQPKTQGSRR" })
			@PathVariable("sequence") String sequence,
			@ApiPathParam(name = "header", description = "A query header.",  allowedvalues = { "protein sequence query" })
			@RequestParam(value = "header", defaultValue = "protein sequence query") String header,

			@ApiQueryParam(name = "matrix", description = "Scoring matrix name (BLOSUM45, BLOSUM50, BLOSUM62, BLOSUM80, BLOSUM90, PAM250, PAM30 or PAM70)", allowedvalues = { "BLOSUM62" })
			@RequestParam(value = "matrix", required = false) String matrix,
			@ApiQueryParam(name = "eValue", description = "Expected value (E) threshold for saving hits", allowedvalues = { "10" })
			@RequestParam(value = "evalue", required = false) Double eValue,
			@ApiQueryParam(name = "gapopen", description = "Cost to open a gap", allowedvalues = { "11" })
			@RequestParam(value = "gapopen", required = false) Integer gapOpen,
			@ApiQueryParam(name = "gapextend", description = "Cost to extend a gap", allowedvalues = { "1" })
			@RequestParam(value = "gapextend", required = false) Integer gapExtend) {

		try {
			BlastSequenceInput params = new BlastSequenceInput(blastBinPath, blastDbPath);
			params.setHeader(header);
			params.setSequence(sequence);
			params.setBlastSearchParams(BlastSearchParams.valueOf(matrix, eValue, gapOpen, gapExtend));

			return blastService.blastProteinSequence(params);
		} catch (ExceptionWithReason exceptionWithReason) {

			exceptionWithReason.getReason().setMessage("cannot execute blastp");
			return new BlastProgramFailure(null, exceptionWithReason);
		}
	}

    @ApiMethod(path = "/blast/isoform/{isoform}", verb = ApiVerb.GET, description = "Search isoform sequence from neXtProt isoform accession", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/blast/isoform/{isoform}", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public BlastProgramOutput blastIsoform(
            @ApiPathParam(name = "isoform", description = "An neXtProt isoform accession.", allowedvalues = { "NX_P01308-1" })
            @PathVariable("isoform") String isoform,
			@ApiQueryParam(name = "begin", description = "The first sequence position (should be strictly positive)")
			@RequestParam(value = "begin", required = false) Integer begin,
			@ApiQueryParam(name = "end", description = "The last sequence position (should be <= sequence length)")
			@RequestParam(value = "end", required = false) Integer end,

			@ApiQueryParam(name = "matrix", description = "Scoring matrix name (BLOSUM45, BLOSUM50, BLOSUM62, BLOSUM80, BLOSUM90, PAM250, PAM30 or PAM70)", allowedvalues = { "BLOSUM62" })
			@RequestParam(value = "matrix", required = false) String matrix,
			@ApiQueryParam(name = "evalue", description = "Expected value (E) threshold for saving hits", allowedvalues = { "10" })
			@RequestParam(value = "evalue", required = false) Double eValue,
			@ApiQueryParam(name = "gapopen", description = "Cost to open a gap", allowedvalues = { "11" })
			@RequestParam(value = "gapopen", required = false) Integer gapOpen,
			@ApiQueryParam(name = "gapextend", description = "Cost to extend a gap", allowedvalues = { "1" })
			@RequestParam(value = "gapextend", required = false) Integer gapExtend) {

		try {
			BlastIsoformInput params = new BlastIsoformInput(blastBinPath, blastDbPath);
			params.setIsoformAccession(isoform);
			params.setQuerySeqBegin(begin);
			params.setQuerySeqEnd(end);
			params.setBlastSearchParams(BlastSearchParams.valueOf(matrix, eValue, gapOpen, gapExtend));

			return blastService.blastIsoformSequence(params);
		} catch (ExceptionWithReason exceptionWithReason) {

			exceptionWithReason.getReason().setMessage("cannot execute blastp");
			return new BlastProgramFailure(null, exceptionWithReason);
		}
    }

    @ApiMethod(path = "/blast/createdb/", verb = ApiVerb.GET, description = "Create nextprot blast database", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/blast/createdb", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public BlastProgramOutput createBlastDb() {

		BlastProgram.Params params = new BlastProgram.Params(makeblastdbBinPath, blastDbPath);
		return blastService.makeNextprotBlastDb(params);
    }
}