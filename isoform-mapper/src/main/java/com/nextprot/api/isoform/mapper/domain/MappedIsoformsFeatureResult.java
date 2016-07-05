package com.nextprot.api.isoform.mapper.domain;

import com.google.common.base.Preconditions;

import java.io.Serializable;

/**
 * Data transfer object that store mapping features results on isoforms
 */
public abstract class MappedIsoformsFeatureResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private final FeatureQuery query;

	public MappedIsoformsFeatureResult(FeatureQuery query) {

		Preconditions.checkNotNull(query);

		this.query = query;
	}

	public FeatureQuery getQuery() {
		return query;
	}

	public abstract boolean isSuccess();

}
