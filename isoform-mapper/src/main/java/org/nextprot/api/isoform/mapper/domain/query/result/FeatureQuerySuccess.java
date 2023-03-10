package org.nextprot.api.isoform.mapper.domain.query.result;

import org.nextprot.api.isoform.mapper.domain.query.result.impl.SingleFeatureQuerySuccessImpl.IsoformFeatureResult;

import java.util.Map;

public interface FeatureQuerySuccess extends FeatureQueryResult {
	
    Map<String, IsoformFeatureResult> getData();

	default boolean isSuccess() {
		return true;
	}
}
