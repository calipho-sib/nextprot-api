package org.nextprot.api.core.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.bio.variation.prot.digestion.ProteinDigesterBuilder;
import org.nextprot.api.commons.bio.variation.prot.digestion.ProteinDigestion;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.service.DigestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

// See also sources of mzjava-proteomics are available at https://bitbucket.org/sib-pig/mzjava-proteomics
@RestController
@Api(name = "Protein digestion", description = "Digest proteins with proteases", group = "Tools")
public class ProteinDigestionController {

	@Autowired
	private DigestionService digestionService;

	@RequestMapping(value = "/digestion/available-protease-list", method = { RequestMethod.GET }, produces = { MediaType.APPLICATION_JSON_VALUE })
	@ApiMethod(path = "/digestion/available-protease-list", verb = ApiVerb.GET, description = "list all available proteases")
	public List<String> listAllProteases() {

		return digestionService.getProteaseNames();
	}

	@RequestMapping(value = "/digestion/digest-all-proteins", method = { RequestMethod.GET }, produces = { MediaType.APPLICATION_JSON_VALUE })
	//@ApiMethod(path = "/digestion/digest-all-proteins", verb = ApiVerb.GET, description = "digest all neXtProt mature proteins with TRYPSIN (with a maximum of 2 missed cleavages)")
	public Set<String> digestAllMatureProteins() {

		return digestionService.digestAllMatureProteinsWithTrypsin();
	}

	@RequestMapping(value = "/digestion/{isoformOrEntryAccession}", method = { RequestMethod.GET }, produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
	@ApiMethod(path = "/digestion/{isoformOrEntryAccession}", verb = ApiVerb.GET, description = "digest a protein with a specific protease")
	public String digestProtein(
			@ApiPathParam(name = "isoformOrEntryAccession", description = "A neXtProt entry or isoform accession (i.e. NX_P01308 or NX_P01308-1).", allowedvalues = { "NX_P01308" })
			@PathVariable("isoformOrEntryAccession") String isoformOrEntryAccession,
			@ApiQueryParam(name = "protease", description = "a protease to digest a protein (trypsin by default)", allowedvalues = { "TRYPSIN" })
			@RequestParam(value = "protease", required = false) String protease,
			@ApiQueryParam(name = "minpeplen", description = "minimum peptide length (7 by default)", allowedvalues = { "7" })
			@RequestParam(value = "minpeplen", required = false) Integer minPepLen,
			@ApiQueryParam(name = "maxpeplen", description = "maximum peptide length (none by default)")
			@RequestParam(value = "maxpeplen", required = false) Integer maxPepLen,
			@ApiQueryParam(name = "maxmissedcleavages", description = "maximum number of missed cleavages (0 by default)", allowedvalues = { "0" })
			@RequestParam(value = "maxmissedcleavages", required = false) Integer maxMissedCleavages,
			@ApiQueryParam(name = "digestmaturepartsonly", description = "digest mature parts of protein if true (true by default)", allowedvalues = { "true" })
			@RequestParam(value = "digestmaturepartsonly", required = false) Boolean digestmaturepartsonly,
			HttpServletRequest request) {

		ProteinDigesterBuilder builder = new ProteinDigesterBuilder();
		if (protease != null) {
			builder.proteaseName(protease);
		}
		if (minPepLen != null) {
			builder.minPepLen(minPepLen);
		}
		if (maxPepLen != null) {
			builder.maxPepLen(maxPepLen);
		}
		if (maxMissedCleavages != null) {
			builder.maxMissedCleavageCount(maxMissedCleavages);
		}
		if (digestmaturepartsonly != null) {
			builder.withMaturePartsOnly(digestmaturepartsonly);
		}

		try {
			Set<String> peptides = digestionService.digestProteins(isoformOrEntryAccession, builder);

			if (request.getRequestURI().toLowerCase().endsWith(".json")) {
				ObjectMapper mapper = new ObjectMapper();

				try {
					return mapper.writeValueAsString(peptides);
				} catch (JsonProcessingException e) {
					throw new NextProtException(e);
				}
			}
			else {

				return String.join(", ", peptides);
			}
		} catch (ProteinDigestion.MissingIsoformException e) {
			throw new NextProtException(e.getMessage());
		}
	}
}
