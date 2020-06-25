package org.nextprot.api.isoform.mapper.service;

import org.nextprot.api.isoform.mapper.domain.feature.FeaturePropagator;
import org.nextprot.api.isoform.mapper.domain.feature.FeatureValidator;
import org.nextprot.api.isoform.mapper.domain.query.result.FeatureQueryResult;
import org.nextprot.api.isoform.mapper.domain.query.MultipleFeatureQuery;
import org.nextprot.api.isoform.mapper.domain.query.SingleFeatureQuery;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public interface IsoformMappingService extends FeatureValidator, FeaturePropagator {

	/**
	 * Execute the given single feature query function to each feature query
	 * @param multipleFeatureQuery multiple feature query
	 * @param function the function that process a FeatureQuery and produces FeatureQueryResult
	 * @return a map of results
	 */
	default Map<String, FeatureQueryResult> handleMultipleQueries(MultipleFeatureQuery multipleFeatureQuery,
																  Function<SingleFeatureQuery, FeatureQueryResult> function) {

		Map<String, FeatureQueryResult> results = new HashMap<>(multipleFeatureQuery.getFeatureList().size()+multipleFeatureQuery.getFeatureMaps().size());

		multipleFeatureQuery.getFeatureList().stream()
				.filter(feature -> !results.containsKey(feature))
				.forEach(feature -> results.put(feature, function.apply(new SingleFeatureQuery(feature, multipleFeatureQuery.getFeatureType(), multipleFeatureQuery.getAccession()))));

		multipleFeatureQuery.getFeatureMaps().stream()
				.filter(featureQuery -> !results.containsKey(featureQuery.get("feature")))
				.forEach(featureQuery -> results.put(featureQuery.get("feature"), function.apply(new SingleFeatureQuery(featureQuery.get("feature"), multipleFeatureQuery.getFeatureType(), featureQuery.get("accession")))));

		return results;
	}
}
