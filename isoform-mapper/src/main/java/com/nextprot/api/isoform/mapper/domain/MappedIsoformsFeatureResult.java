package com.nextprot.api.isoform.mapper.domain;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.constants.AnnotationCategory;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Data transfer object that store mapping features on isoforms
 */
public abstract class MappedIsoformsFeatureResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private final HashMap<String, Object> map;

	public MappedIsoformsFeatureResult(Query query) {

		Preconditions.checkNotNull(query);

		map = new HashMap<>(3);
		map.put("query", query);
	}

	/*protected Map<String, MappedIsoformFeatureResult> getData() {

		if (!map.containsKey("data")) {
			map.put("data", new HashMap<String, MappedIsoformFeatureResult>(10));
		}

		//noinspection unchecked
		return (Map<String, MappedIsoformFeatureResult>) map.get("data");
	}*/

	public void loadContentValue() {

		map.put(getContentName(), getContentValue());
	}

	protected abstract String getContentName();
	protected abstract Object getContentValue();

	public abstract boolean isSuccess();

	/*
         "query": {
             "accession": "NX_P01308",
             "feature": "SCN11A-p.Leu1158Pro",
             "feature-type": "sequence variant",
             "propagate": false
         }
         */
	public static class Query implements Serializable {

		private final String accession;
		private final String feature;
		private final String featureType;
		private final boolean propagate;

		public Query(String accession, String feature, AnnotationCategory featureType, boolean propagate) {

			Preconditions.checkNotNull(accession);
			Preconditions.checkArgument(accession.startsWith("NX_"), "should be a nextprot accession number");
			Preconditions.checkNotNull(feature);
			Preconditions.checkArgument(!feature.isEmpty(), "feature should be defined");
			Preconditions.checkNotNull(featureType);
			Preconditions.checkArgument(featureType == AnnotationCategory.VARIANT);

			this.accession = accession;
			this.feature = feature;
			this.featureType = featureType.toString();
			this.propagate = propagate;
		}

		public String getAccession() {
			return accession;
		}

		public String getFeature() {
			return feature;
		}

		public String getFeatureType() {
			return featureType;
		}

		public boolean isPropagate() {
			return propagate;
		}
	}


}
