package org.nextprot.api.isoform.mapper.controller;

import org.jsondoc.core.annotation.*;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryResult;
import org.nextprot.api.isoform.mapper.domain.MultipleFeatureQuery;
import org.nextprot.api.isoform.mapper.service.IsoformMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Lazy
@Controller
@Api(name = "Feature Isoform Mapping", description = "Methods to check and map features over isoforms.", group = "Iso Mapper")
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

		return isoformMappingService.validateFeature(feature, featureCategory, nextprotAccession);
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

		return isoformMappingService.propagateFeature(feature, featureCategory, nextprotAccession);
	}

	/*
	{
		"featureType": "variant",
		"featureList": [
			"SCN11A-p.Leu1158Pro",
			"SCN11A-p.Leu1158Pro"
		],
		"accession": "NX_Q9UI33" // feature list accession: optional if deducible from gene defined in feature
	}

	or

	{
		"featureType": "variant",
		"featureMaps": [
			{
				"feature": "SCN11A-p.Leu1158Pro",
				"accession": "NX_Q9UI33"
			},
			{
				"feature": "SCN11A-p.Leu1158Pro",
				"accession": "NX_Q9UI33"
			}
		]
	}

	or both

	{
		"featureType": "variant",
		"featureList": [
			"SCN11A-p.Leu1158Pro",
			"SCN11A-p.Leu1158Pro"
		],
		"accession": "NX_Q9UI33",
		"featureMaps": [
			{
				"feature": "SCN11A-p.Leu1158Pro",
				"accession": "NX_Q9UI33" // optional if deducible from gene defined in feature
			},
			{
				"feature": "SCN11A-p.Leu1158Pro",
				"accession": "NX_Q9UI33"
			}
		]
	}
	 */

	@ApiMethod(path = "/propagate-features", verb = ApiVerb.POST, description = "Validate isoform feature list and compute feature propagations on other isoforms", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping(value = "/propagate-features", method = { RequestMethod.POST }, consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public Map<String, FeatureQueryResult> propagateIsoformFeaturePost(@RequestBody @ApiBodyObject MultipleFeatureQuery multipleFeatureQuery) {

		Map<String, FeatureQueryResult> results = new HashMap<>(multipleFeatureQuery.getFeatureList().size()+multipleFeatureQuery.getFeatureMaps().size());

		multipleFeatureQuery.getFeatureList().stream()
				.filter(feature -> !results.containsKey(feature))
				.forEach(feature -> results.put(feature, isoformMappingService.propagateFeature(feature, multipleFeatureQuery.getFeatureType(), multipleFeatureQuery.getAccession())));

		multipleFeatureQuery.getFeatureMaps().stream()
				.filter(featureQuery -> !results.containsKey(featureQuery.get("feature")))
				.forEach(featureQuery -> results.put(featureQuery.get("feature"), isoformMappingService.propagateFeature(featureQuery.get("feature"), multipleFeatureQuery.getFeatureType(), featureQuery.get("accession"))));

		return results;
	}
}

