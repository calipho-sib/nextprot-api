package org.nextprot.api.web.controller;

import com.nextprot.api.isoform.mapper.domain.FeatureQueryResult;
import com.nextprot.api.isoform.mapper.service.IsoformMappingService;
import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Lazy
@Controller
@Api(name = "Feature Isoform Mapping", description = "Methods to check and map features over isoforms")
public class IsoformMappingController {

	@Autowired
	private IsoformMappingService isoformMappingService;

	@ApiMethod(path = "/validate-feature/{category}", verb = ApiVerb.GET, description = "Validate isoform feature", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/validate-feature/{category}", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<FeatureQueryResult> validateIsoformFeature(
			@ApiPathParam(name = "category", description = "A feature category (either 'variant' or 'ptm').",  allowedvalues = { "variant" })
			@PathVariable("category") String featureCategory, HttpServletRequest request, HttpServletResponse response,
			@ApiQueryParam(name = "feature", description = "An isoform feature.",  allowedvalues = { "SCN11A-p.Leu1158Pro" })
			@RequestParam(value = "feature", required = true) String feature,
			@ApiQueryParam(name = "accession", description = "A nextprot accession: either isoform accession or entry accession when canonical.",  allowedvalues = { "NX_Q9UI33" })
			@RequestParam(value = "accession", required = true) String nextprotAccession) {

		FeatureQueryResult result = isoformMappingService.validateFeature(feature, featureCategory, nextprotAccession);

		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@ApiMethod(path = "/propagate-feature/{category}", verb = ApiVerb.GET, description = "Validate isoform feature and compute feature propagations on other isoforms", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/propagate-feature/{category}", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<FeatureQueryResult> propagateIsoformFeature(
			@ApiPathParam(name = "category", description = "A feature category (either 'variant' or 'ptm').",  allowedvalues = { "variant" })
			@PathVariable("category") String featureCategory, HttpServletRequest request, HttpServletResponse response,
			@ApiQueryParam(name = "feature", description = "An isoform feature.",  allowedvalues = { "SCN11A-p.Leu1158Pro" })
			@RequestParam(value = "feature", required = true) String feature,
			@ApiQueryParam(name = "accession", description = "A nextprot accession: either isoform accession or entry accession when canonical.",  allowedvalues = { "NX_Q9UI33" })
			@RequestParam(value = "accession", required = true) String nextprotAccession) {

		FeatureQueryResult result = isoformMappingService.propagateFeature(feature, featureCategory, nextprotAccession);

		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}

