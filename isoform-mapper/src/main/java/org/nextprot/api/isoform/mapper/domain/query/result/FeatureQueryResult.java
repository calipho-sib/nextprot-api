package org.nextprot.api.isoform.mapper.domain.query.result;

import org.nextprot.api.isoform.mapper.domain.query.FeatureQuery;

import java.io.Serializable;

public interface FeatureQueryResult extends Serializable {

	FeatureQuery getQuery();

	boolean isSuccess();
}
