package org.nextprot.api.isoform.mapper.controller;

import org.jsondoc.core.annotation.*;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.isoform.mapper.domain.query.result.FeatureQueryResult;
import org.nextprot.api.isoform.mapper.domain.query.MultipleFeatureQuery;
import org.nextprot.api.isoform.mapper.domain.query.SingleFeatureQuery;
import org.nextprot.api.isoform.mapper.service.IsoformMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@Api(name = "Isoform Mapping", description = "Methods to check and map features over isoforms.", group = "Tools")
public class IsoformMappingController {

	@Autowired
	private IsoformMappingService isoformMappingService;

	@ApiMethod(path = "/validate-feature/{category}", verb = ApiVerb.GET, description = "Validate isoform feature", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/validate-feature/{category}", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public FeatureQueryResult validateIsoformFeature(
			@ApiPathParam(name = "category", description = "A feature category (either 'variant', 'mutagenesis' or 'ptm').",  allowedvalues = { "variant" })
			@PathVariable("category") String featureCategory,
			@ApiQueryParam(name = "feature", description = "An isoform feature ('variant' category uses HGVS format (http://varnomen.hgvs.org/recommendations/protein)).",  allowedvalues = { "SCN11A-p.Leu1158Pro" })
			@RequestParam(value = "feature") String feature,
			@ApiQueryParam(name = "accession", description = "An optional nextprot entry accession (deduced from feature gene name if undefined).",  allowedvalues = { })
			@RequestParam(value = "accession", required = false) String nextprotAccession) {

		return isoformMappingService.validateFeature(new SingleFeatureQuery(feature, featureCategory, nextprotAccession));
	}

	@ApiMethod(path = "/propagate-feature/{category}", verb = ApiVerb.GET, description = "Validate isoform feature and compute feature propagations on other isoforms", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/propagate-feature/{category}", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public FeatureQueryResult propagateIsoformFeature(
			@ApiPathParam(name = "category", description = "A feature category (either 'variant', 'mutagenesis' or 'ptm').",  allowedvalues = { "variant" })
			@PathVariable("category") String featureCategory,
			@ApiQueryParam(name = "feature", description = "An isoform feature ('variant' category uses HGVS format (http://varnomen.hgvs.org/recommendations/protein)).",  allowedvalues = { "SCN11A-p.Leu1158Pro" })
			@RequestParam(value = "feature") String feature,
			@ApiQueryParam(name = "accession", description = "An optional nextprot entry accession (deduced from feature gene name if undefined).",  allowedvalues = { })
			@RequestParam(value = "accession", required = false) String nextprotAccession) {

		return isoformMappingService.propagateFeature(new SingleFeatureQuery(feature, featureCategory, nextprotAccession));
	}

	@ApiMethod(path = "/validate-features", verb = ApiVerb.POST, description = "Validate isoform feature list", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping(value = "/validate-features", method = { RequestMethod.POST }, consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public Map<String, FeatureQueryResult> validateIsoformFeatures(@RequestBody @ApiBodyObject MultipleFeatureQuery multipleFeatureQuery) {

		return isoformMappingService.handleMultipleQueries(multipleFeatureQuery, isoformMappingService::validateFeature);
	}

	@ApiMethod(path = "/propagate-features", verb = ApiVerb.POST, description = "Validate isoform feature list and compute feature propagations on other isoforms", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping(value = "/propagate-features", method = { RequestMethod.POST }, consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public Map<String, FeatureQueryResult> propagateIsoformFeatures(@RequestBody @ApiBodyObject MultipleFeatureQuery multipleFeatureQuery) {

		return isoformMappingService.handleMultipleQueries(multipleFeatureQuery, isoformMappingService::propagateFeature);
	}
}

