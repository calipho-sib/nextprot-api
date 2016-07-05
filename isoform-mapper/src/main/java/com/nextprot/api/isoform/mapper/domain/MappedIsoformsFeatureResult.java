package com.nextprot.api.isoform.mapper.domain;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.constants.AnnotationCategory;

import java.io.Serializable;

/**
 * Data transfer object that store mapping features results on isoforms
 */
public abstract class MappedIsoformsFeatureResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Query query;

	public MappedIsoformsFeatureResult(Query query) {

		Preconditions.checkNotNull(query);

		this.query = query;
	}

	public Query getQuery() {
		return query;
	}

	public abstract boolean isSuccess();

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

			this.accession = accession;
			this.feature = feature;
			this.featureType = featureType.getApiTypeName();
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
