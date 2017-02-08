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

	@ApiMethod(path = "/blast/sequence/{sequence}", verb = ApiVerb.GET, description = "Search protein sequence", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/blast/sequence/{sequence}", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public BlastProgramOutput blastProteinSequence(
			@ApiPathParam(name = "sequence", description = "A protein sequence query.",  allowedvalues = { "GTTYVTDKSEEDNEIESEEEVQPKTQGSRR" })
			@PathVariable("sequence") String sequence,
			@ApiQueryParam(name = "title", description = "A query title.",  allowedvalues = { "protein sequence query" })
			@RequestParam(value = "title", defaultValue = "protein sequence query") String title,
			@ApiQueryParam(name = "matrix", defaultvalue = "BLOSUM62", description = "Scoring matrix name (value among BLOSUM45, BLOSUM50, BLOSUM62, BLOSUM80, BLOSUM90, PAM250, PAM30 or PAM70)", allowedvalues = { "BLOSUM62" })
			@RequestParam(value = "matrix", required = false) String matrix,
			@ApiQueryParam(name = "eValue", defaultvalue = "10", description = "Expected value threshold for saving hits", allowedvalues = { "10" })
			@RequestParam(value = "evalue", required = false) Double eValue,
			@ApiQueryParam(name = "gapopen", defaultvalue = "11", description = "Cost to open a gap", allowedvalues = { "11" })
			@RequestParam(value = "gapopen", required = false) Integer gapOpen,
			@ApiQueryParam(name = "gapextend", defaultvalue = "1", description = "Cost to extend a gap", allowedvalues = { "1" })
			@RequestParam(value = "gapextend", required = false) Integer gapExtend) {

		BlastSequenceInput params = new BlastSequenceInput(blastBinPath, blastDbPath);
		params.setTitle(title);
		params.setSequence(sequence.replaceAll(" ", ""));

		try {
			params.setBlastSearchParams(BlastSearchParams.valueOf(matrix, eValue, gapOpen, gapExtend));

			return blastService.blastProteinSequence(params);
		} catch (ExceptionWithReason exceptionWithReason) {

			exceptionWithReason.getReason().setMessage("cannot execute blastp");
			return new BlastProgramFailure(params, exceptionWithReason);
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
			@ApiQueryParam(name = "matrix", defaultvalue = "BLOSUM62", description = "Scoring matrix name (value among BLOSUM45, BLOSUM50, BLOSUM62, BLOSUM80, BLOSUM90, PAM250, PAM30 or PAM70)", allowedvalues = { "BLOSUM62" })
			@RequestParam(value = "matrix", required = false) String matrix,
			@ApiQueryParam(name = "evalue", defaultvalue = "10", description = "Expected value threshold for saving hits", allowedvalues = { "10" })
			@RequestParam(value = "evalue", required = false) Double eValue,
			@ApiQueryParam(name = "gapopen",  defaultvalue = "11", description = "Cost to open a gap", allowedvalues = { "11" })
			@RequestParam(value = "gapopen", required = false) Integer gapOpen,
			@ApiQueryParam(name = "gapextend",  defaultvalue = "1", description = "Cost to extend a gap", allowedvalues = { "1" })
			@RequestParam(value = "gapextend", required = false) Integer gapExtend) {

		BlastIsoformInput params = new BlastIsoformInput(blastBinPath, blastDbPath);

		try {
			params.setIsoformAccession(isoform);
			params.setQuerySeqPositions(begin, end);
			params.setBlastSearchParams(BlastSearchParams.valueOf(matrix, eValue, gapOpen, gapExtend));

			return blastService.blastIsoformSequence(params);
		} catch (ExceptionWithReason exceptionWithReason) {

			exceptionWithReason.getReason().setMessage("cannot execute blastp");
			return new BlastProgramFailure(params, exceptionWithReason);
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