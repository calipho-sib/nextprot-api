package org.nextprot.api.isoform.mapper.domain.impl;

import org.nextprot.api.isoform.mapper.domain.FeatureQuery;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryResult;

import com.google.common.base.Preconditions;

/**
 * Data transfer object that store mapping features results on isoforms
 */
public abstract class BaseFeatureQueryResult implements FeatureQueryResult {

	private static final long serialVersionUID = 1L;

	private final FeatureQuery query;

	public BaseFeatureQueryResult() {
		this.query = null;
	}

	public BaseFeatureQueryResult(FeatureQuery query) {

		Preconditions.checkNotNull(query);

		this.query = query;
	}

	public FeatureQuery getQuery() {
		return query;
	}

}
