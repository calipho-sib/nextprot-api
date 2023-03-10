package org.nextprot.api.isoform.mapper.domain.query.result.impl;

import com.google.common.base.Preconditions;
import org.nextprot.api.isoform.mapper.domain.query.FeatureQuery;
import org.nextprot.api.isoform.mapper.domain.query.result.FeatureQueryResult;

/**
 * Data transfer object that store mapping features results on isoforms
 */
public abstract class BaseFeatureQueryResult<FQ extends FeatureQuery> implements FeatureQueryResult {

	private static final long serialVersionUID = 20161117L;

	private final FQ query;

	public BaseFeatureQueryResult() {
		this.query = null;
	}

	public BaseFeatureQueryResult(FQ query) {

		Preconditions.checkNotNull(query);

		this.query = query;
	}

	@Override
	public FQ getQuery() {
		return query;
	}
}
